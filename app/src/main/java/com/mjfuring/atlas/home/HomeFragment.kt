package com.mjfuring.atlas.home

import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mjfuring.atlas.R
import com.mjfuring.atlas.VmMain
import com.mjfuring.atlas.common.BaseMapFragment
import com.mjfuring.atlas.common.DEFAULT_DOWNLOAD_ZOOM
import com.mjfuring.atlas.common.DEFAULT_LAT
import com.mjfuring.atlas.common.DEFAULT_LON
import com.mjfuring.atlas.databinding.FragmentHomeBinding
import com.mjfuring.atlas.databinding.FragmentSplashBinding
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment: BaseMapFragment<FragmentHomeBinding>() {

    private val vmMain: VmMain by sharedViewModel()
    private var mapIsReady = false

    override fun layoutRes(): Int = R.layout.fragment_home
    override fun mapRes(): Int = R.id.mapView

    override fun onInit() {
        defaultZoom = DEFAULT_DOWNLOAD_ZOOM
        viewBinding?.apply {

            observeEvents()
        }
    }

    override fun mapReady(map: MapboxMap) {
        map.setStyle(Style.MAPBOX_STREETS){
            setupSource(it)
            setUpImage(it)
            setUpMarkerLayer(it)
            setUpInfoWindowLayer(it)
            enableLocationComponent(it)

        }
        map.addOnMapClickListener {
            onClickIcon(map.projection.toScreenLocation(it))
        }
    }

    override fun onFirstTrack(latLng: LatLng) {
        moveToPosition(latLng.latitude, latLng.longitude)
        vmMain.listRequest()
    }

    private fun observeEvents() {
        vmMain.apply {
            observeData<List<Incident>>(listEvent, {
                addMarkers(it)
                fitInBox(it)
            })
        }
    }


}