package com.mjfuring.atlas.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.MapboxConstants
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mjfuring.atlas.R
import com.mjfuring.atlas.db.model.Incident
import com.mjfuring.atlas.db.model.LatLong
import com.mjfuring.base.Base
import com.mjfuring.base.view.Dialog


abstract class BaseMapFragment<B : ViewDataBinding>: Fragment(), Base, PermissionsListener {

    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"

    var map: MapboxMap? = null
    var mapView: MapView? = null
    var viewBinding: B? = null
    val symbolLayers = ArrayList<Feature>()
    var defaultZoom = DEFAULT_ZOOM
    var firstTrackSuccess = false

    private var source: GeoJsonSource? = null
    private var collection: FeatureCollection? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationEngine: LocationEngine? = null
    private var callback = LocationChangeListeningCallback()

    open fun mapReady(map: MapboxMap){}
    open fun onFirstTrack(latLng: LatLng){}
    open fun onLocationUpdate(latLong: LatLong){}
    open fun onIconSelected(id: Long){}
    open fun onCreate(viewBinding: B?){}

    @LayoutRes
    abstract fun layoutRes(): Int

    abstract fun mapRes(): Int

    override var dialog: Dialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onInit()
    }

    override fun context(): Context? {
        return context
    }

    override fun lifeCycle(): LifecycleOwner {
        return this
    }

    override fun bindings(): B {
        return viewBinding!!
    }

    override fun onDestroyView() {/**/
        viewBinding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog = Dialog(context) { onDialogOk() }
        viewBinding = DataBindingUtil.inflate(inflater, layoutRes(), container, false)
        onCreate(viewBinding)
        mapView = viewBinding!!.root.findViewById(mapRes())
        mapView?.onCreate(savedInstanceState)

        var runMapReady = true
        savedInstanceState?.apply {
            if (getBoolean(MapboxConstants.STATE_HAS_SAVED_STATE)) {
                runMapReady = false
                mapView?.onCreate(savedInstanceState)
            }
        }
        mapView?.getMapAsync{
            map = it
            if (runMapReady){
                mapReady(it)
            }
        }
        return viewBinding!!.root
    }


    fun setupSource(loadedStyle: Style) {
        collection = FeatureCollection.fromFeatures(symbolLayers)
        source = GeoJsonSource(GEOJSON_SOURCE_ID, collection).apply {
            loadedStyle.addSource(this)
        }
    }

    fun setUpImage(loadedStyle: Style) {
        loadedStyle.addImage(
            RED_ICON_ID, BitmapFactory.decodeResource(
                this.resources, R.drawable.red_marker
            )
        )
        loadedStyle.addImage(
            YELLOW_ICON_ID, BitmapFactory.decodeResource(
                this.resources, R.drawable.yellow_marker
            )
        )
        loadedStyle.addImage(
            BLUE_ICON_ID, BitmapFactory.decodeResource(
                this.resources, R.drawable.blue_marker
            )
        )
    }

    fun setUpMarkerLayer(loadedStyle: Style) {
        loadedStyle.addLayer(
            SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                    iconImage(
                        match(
                            get(ICON_PROPERTY), literal(RED_ICON_ID),
                            stop(YELLOW_ICON_ID, YELLOW_ICON_ID),
                            stop(RED_ICON_ID, RED_ICON_ID))
                    ),
                    iconAllowOverlap(true),
                    iconOffset(arrayOf(0f, -8f))
                )
        )
    }

    fun setUpInfoWindowLayer(loadedStyle: Style) {
        loadedStyle.addLayer(
            SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties( /* show image with id title based on the value of the name feature property */
                    iconImage("{name}"),  /* set anchor of icon to bottom-left */
                    iconAnchor(ICON_ANCHOR_BOTTOM),  /* all info window and marker image to appear at the same time*/
                    iconAllowOverlap(true),  /* offset the info window to be above the marker */
                    iconOffset(arrayOf(-2f, -28f))
                ) /* add a filter to show only when selected feature property is true */
                .withFilter(eq(get(PROPERTY_SELECTED), literal(true)))
        )
    }

    fun refreshSource() {
        source?.apply {
           if (collection != null){
               setGeoJson(collection)
           }
        }
    }


    fun setSelected(index: Int) {
        collection?.apply {
            val feature: Feature = features()?.get(index) ?: return
            setFeatureSelectState(feature, true)
            refreshSource()
        }
    }

    fun setFeatureSelectState(
        feature: Feature,
        selectedState: Boolean
    ) {
        if (feature.properties() != null) {
            feature.properties()!!.addProperty(PROPERTY_SELECTED, selectedState)
            refreshSource()
        }
    }

    fun featureSelectStatus(index: Int): Boolean {
        return collection?.features()?.get(index)?.getBooleanProperty(PROPERTY_SELECTED) ?: return false
    }

    fun setImageGenResults(imageMap: HashMap<String?, Bitmap?>?) {
        map?.apply {
            getStyle { style ->
                if (imageMap != null) {
                    style.addImages(imageMap)
                }
            }
        }
    }

    fun selectFeatureById(id: Long, callOnSelected: Boolean = false): Boolean {
        collection?.features()?.apply {
            for (i in 0 until size) {
                if (this[i].getNumberProperty(PROPERTY_ID) == id) {
                    setFeatureSelectState(this[i], true)
                    if(callOnSelected){
                        onIconSelected(id)
                    }
                } else {
                    setFeatureSelectState(this[i], false)
                }
            }
            return true
        }
        return false
    }

    fun onClickIcon(screenPoint: PointF): Boolean {
        map?.apply {
            val features: List<Feature> = queryRenderedFeatures(screenPoint, MARKER_LAYER_ID)
            if (features.isNotEmpty()){
                return selectFeatureById(features[0].getNumberProperty(PROPERTY_ID).toLong(), true)
            }
        }
        return false
    }



    fun moveToPosition(lat: Double, lon: Double, zoom: Double? = null){
        map?.apply {
            var newZoom = cameraPosition.zoom
            if (zoom != null){
                newZoom = zoom
            } else {
                if (newZoom < 5){
                    newZoom = defaultZoom
                }
            }
            animateCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(LatLng(lat, lon)).zoom(newZoom).build()
            ), 1000)
        }
    }



    fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .useDefaultLocationEngine(false)
                .build()
            map?.locationComponent?.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true                       // Enable to make component visible
                cameraMode = CameraMode.TRACKING                        // Set the component's camera mode
                renderMode = RenderMode.COMPASS                         // Set the component's render mode
            }
            initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(activity)
        }
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request = LocationEngineRequest
            .Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()
        locationEngine?.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine?.getLastLocation(callback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private inner class LocationChangeListeningCallback: LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.apply {
                map?.apply {
                    locationComponent.forceLocationUpdate(result.lastLocation)
                    if (!firstTrackSuccess){
                        firstTrackSuccess = true
                        onFirstTrack(LatLng(latitude, longitude))
                    }
                    onLocationUpdate(LatLong(latitude, longitude))
                }
            }
        }
        override fun onFailure(exception: Exception) {}
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //Toast.makeText(this, "Permission not granted!!", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            map?.getStyle {
                enableLocationComponent(it)
            }
        } else {

        }
    }

    private fun createFeature(request: Incident): Feature {
        request.apply {
            return Feature.fromGeometry(
                Point.fromLngLat(longitude, latitude)
            ).apply {
                addBooleanProperty(PROPERTY_SELECTED, false)
                addStringProperty(PROPERTY_NAME, number)
                addNumberProperty(PROPERTY_ID, id)
                addStringProperty(ICON_PROPERTY, RED_ICON_ID)
            }
        }
    }

    fun addMarkers(request: List<Incident>){
        symbolLayers.clear()
        request.forEach {
            symbolLayers.add(createFeature(it))
        }
        collection = FeatureCollection.fromFeatures(symbolLayers)

        refreshSource()
    }

    fun fitInBox(request: List<Incident>){
        if (request.size > 1){
            val bounds = LatLngBounds.Builder()
            request.forEach {
                bounds.include(LatLng(it.latitude, it.longitude))
            }
            map?.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100), 2000)
        }
    }
    fun clearMarkers(){
        symbolLayers.clear()
        collection = FeatureCollection.fromFeatures(symbolLayers)
        refreshSource()
    }



}