package com.mjfuring.atlas.track

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.CancelableCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mjfuring.atlas.R
import com.mjfuring.atlas.common.BaseMapFragment
import com.mjfuring.atlas.databinding.FragmentTrackBinding
import com.mjfuring.atlas.db.model.Incident


class FragmentTrack : BaseMapFragment<FragmentTrackBinding>() {

    private lateinit var incident: Incident
    private var bounds = LatLngBounds.Builder()

    override fun layoutRes(): Int = R.layout.fragment_track

    override fun mapRes(): Int = R.id.mapView

    override fun onInit() {
        incident = FragmentTrackArgs.fromBundle(requireArguments()).incident
    }

    override fun mapReady(map: MapboxMap) {
        addMarker(incident)
        map.setStyle(Style.MAPBOX_STREETS) {
            setupSource(it)
            setUpImage(it)
            setUpMarkerLayer(it)
            setUpInfoWindowLayer(it)
            enableLocationComponent(it)
        }
    }

    override fun onFirstTrack(latLng: LatLng) {
        bounds.include(LatLng(incident.latitude, incident.longitude))
        bounds.include(LatLng(latLng.latitude, latLng.longitude))
        map?.apply {
            easeCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300), 2000,
                object : CancelableCallback {
                    override fun onFinish() {
                        easeCamera(CameraUpdateFactory.tiltTo(45.0))
                    }
                    override fun onCancel() {
                    }
                })
        }
    }


}
