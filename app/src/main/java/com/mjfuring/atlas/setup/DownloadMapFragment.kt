package com.mjfuring.atlas.setup

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.offline.*
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.*
import com.mjfuring.atlas.databinding.FragmentMapDownloadBinding
import com.mjfuring.atlas.splash.SplashFragmentDirections
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import kotlin.math.roundToInt

class DownloadMapFragment: BaseMapFragment<FragmentMapDownloadBinding>(){

    private val vmSetup: VmSetup by sharedViewModel()
    private lateinit var offlineManager: OfflineManager
    private var offlineRegion: OfflineRegion? = null

    override fun layoutRes(): Int = R.layout.fragment_map_download
    override fun mapRes(): Int = R.id.mapView

    override fun onInit() {
        defaultZoom = DEFAULT_DOWNLOAD_ZOOM
        offlineManager = OfflineManager.getInstance(requireContext())
        viewBinding?.apply {
            back.setOnClickListener {
                vmSetup.gotoFragment(1)
            }
            btnDownload.setOnClickListener {
                showMap()
            }
        }
        checkOfflineMap()
    }

    override fun mapReady(map: MapboxMap) {
        map.setStyle(Style.MAPBOX_STREETS){
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(LatLng(DEFAULT_LAT, DEFAULT_LON)).zoom(defaultZoom).build()
                ), 1000,
                object: MapboxMap.CancelableCallback{
                    override fun onFinish() {
                        prepareDownload(it.uri)
                    }
                    override fun onCancel() {}
                })
        }
    }

    private fun prepareDownload(uri: String){
        map?.apply {

            val definition = OfflineTilePyramidRegionDefinition(
                uri,
                projection.visibleRegion.latLngBounds,
                cameraPosition.zoom,
                maxZoomLevel,
                requireActivity().resources.displayMetrics.density
            )

            val metadata = try {
                val jsonObject = JSONObject()
                jsonObject.put(JSON_FIELD_REGION_NAME, EATS_REGION)
                val json = jsonObject.toString()
                json.toByteArray()
            } catch (exception: Exception) {
                Timber.e("Failed to encode metadata: %s", exception.message)
                null
            } ?: return

            offlineManager.createOfflineRegion(definition, metadata,
                object : OfflineManager.CreateOfflineRegionCallback {
                    override fun onCreate(offlineRegion: OfflineRegion) {
                        Timber.d("Offline region created")
                        this@DownloadMapFragment.offlineRegion = offlineRegion
                        startDownload(offlineRegion)
                    }
                    override fun onError(error: String) {
                        Timber.e("Error: %s", error)
                    }
                }
            )
        }

    }


    private fun startDownload(region: OfflineRegion) {

        region.setObserver(object : OfflineRegion.OfflineRegionObserver {
            override fun onStatusChanged(status: OfflineRegionStatus) {
                val percentage = if (status.requiredResourceCount >= 0) {
                    100.0 * status.completedResourceCount / status.requiredResourceCount
                } else {
                    0.0
                }

                Timber.d("Percentage: %s", percentage)

                viewBinding?.pBar?.apply {
                    if (status.isComplete) {
                        goToHome()
                        return
                    } else if (status.isRequiredResourceCountPrecise) {
                        isIndeterminate = false
                        progress = percentage.roundToInt()
                    }
                }

                Timber.d(
                    "%s/%s resources; %s bytes downloaded.",
                    status.completedResourceCount.toString(),
                    status.requiredResourceCount.toString(),
                    status.completedResourceSize.toString()
                )
            }

            override fun onError(error: OfflineRegionError) {
                Timber.e("onError reason: %s", error.reason)
                Timber.e("onError message: %s", error.message)
            }

            override fun mapboxTileCountLimitExceeded(limit: Long) {
                Timber.e("Mapbox tile count limit exceeded: %s", limit)
            }
        })

        region.setDownloadState(OfflineRegion.STATE_ACTIVE)

    }

    private fun getRegionName(offlineRegion: OfflineRegion): String {
        return try {
            val metadata = offlineRegion.metadata
            val json = String(metadata)
            val jsonObject = JSONObject(json)
            jsonObject.getString(JSON_FIELD_REGION_NAME)
        } catch (exception: java.lang.Exception) {
            Timber.e("Failed to decode metadata: %s", exception.message)
            ""
        }
    }

    private fun showMap(show: Boolean = true){
        viewBinding?.apply {
            mapView.isVisible = show
            pBar.isVisible = show
            btnDownload.isVisible = !show
            tvMsg.isVisible = !show
        }

    }

    private fun checkOfflineMap() {
        offlineManager.listOfflineRegions(object: OfflineManager.ListOfflineRegionsCallback{
            override fun onList(offlineRegions: Array<out OfflineRegion>?) {
                if (!offlineRegions.isNullOrEmpty()){
                    var found = false
                    for (region in offlineRegions) {
                        val name = getRegionName(region)
                        if (name == EATS_REGION){
                            found = true
                            break
                        }
                    }
                    if (found){
                        goToHome()
                    } else {
                        viewBinding?.btnDownload?.isVisible = true
                    }
                } else {
                    viewBinding?.btnDownload?.isVisible = true
                }
            }
            override fun onError(error: String?) {
                viewBinding?.btnDownload?.isVisible = true
            }
        })
    }

    private fun goToHome(){
        vmSetup.mapDownloaded()
        findNavController().navigate(
            SetupFragmentDirections.actionNavSetupToNavHome()
        )
    }

}


