package in2000.gruppe40.dron.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.RectF
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.FillManager
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import com.mapbox.mapboxsdk.style.sources.VectorSource
import in2000.gruppe40.dron.R
import in2000.gruppe40.dron.model.SharedPreference
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(), LocationListener, OnMapReadyCallback, PermissionsListener {

    private lateinit var mapboxMap: MapboxMap
    //Brukes i illegalZones-metodene
    private lateinit var fillManager: FillManager
    private var showIllegalZones: Boolean = false
    private var cleanedFillManager: Boolean = false
    private var latLngChecks: ArrayList<LatLng> = ArrayList()
    private var circles: ArrayList<List<LatLng>> = ArrayList()
    val PERMISSION_ID = 1
    lateinit var loadedStyle: Style
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createMap(savedInstanceState)
    }

    /**
     * Returnerer lokasjonen til brukeren.
     * Er avhengig av AppCompatActivity(), så kan ikke gjøres direkte i weatherviewModel..
     *
     */

    private fun isEnabled() : Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false
        }
        return true
    }

    fun createMap(savedInstanceState: Bundle?){
        if(isEnabled()){
            val sp = SharedPreference(this)
            if (sp.getFloat(sp.windTAG) == -1F) {
                sp.removeFloat(sp.windTAG)
                sp.saveFloat(sp.windTAG, 4.5F)
            }
            if (sp.getFloat(sp.rainTAG) == -1F) {
                sp.removeFloat(sp.rainTAG)
                sp.saveFloat(sp.rainTAG, 2.5F)
            }
            if (sp.getFloat(sp.fogTAG) == -1F) {
                sp.removeFloat(sp.fogTAG)
                sp.saveFloat(sp.fogTAG, 10.45F)
            }

            //Henter her API-Key fra strings.xml
            Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

            setContentView(R.layout.activity_main)

            mapView.getMapAsync { mapboxMap ->
                onMapReady(mapboxMap)

                mapboxMap.getStyle {
                    val source = VectorSource("vector-source", "mapbox://mapbox.mapbox-streets-v8")
                    it.addSource(source)
                }

                mapboxMap.addOnFlingListener {
                    updateIllegalZones()
                }
            }

            val searchButton = findViewById<ImageButton>(R.id.Search)
            val illegalZonesButton = findViewById<FloatingActionButton>(R.id.action_a)
            val settingsButton = findViewById<ImageButton>(R.id.Settings)

            settingsButton.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            searchButton.setOnClickListener {
                showSearchbar(savedInstanceState)
            }

            illegalZonesButton.setOnClickListener {
                showIllegalZones = !showIllegalZones

                if (showIllegalZones) {
                    updateIllegalZones()
                } else {
                    removeIllegalZones()
                }
            }
            }else{
            buildAlertMessageNoGps(savedInstanceState)
        }

    }

    private fun updatePosition(lat: Double, lng: Double) {
        val loc = Location("")
        loc.latitude = lat
        loc.longitude = lng

        findViewById<WeatherView>(R.id.weatherView).updatePos(loc)

        mapView.getMapAsync { map ->
            map.moveCamera(
                // zoom: område på kartet. 0 and 22. 0 er kontinenter, 11 er byer, 22 er bygninger og interessepunkter
                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 12.0)
            )
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "Skru på posisjon for å bruke kartet.", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "Skru på posisjon for å bruke kartet.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun buildAlertMessageNoGps(savedInstanceState: Bundle?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Posisjon ikke tillatt, vil du tillate?")
            .setCancelable(false)
            .setPositiveButton("Ja")
            { _, _ -> if(!isEnabled()){startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))}
                createMap(savedInstanceState)
             }
            .setNegativeButton("Nei") { dialog, _ -> dialog.cancel(); Toast.makeText(this, "Appen trenger brukerposisjon.", Toast.LENGTH_LONG).show(); finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    @SuppressLint("MissingPermission")
    fun enableLocationComponent(loadedMapStyle: Style) {
        // Sjekker om stedstjenester er tillatt
        loadedStyle = loadedMapStyle
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Lager og endrer LocationComponent sine innstillinger
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapbox_blue))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Endrer LocationComponent sine innstillinger
            mapboxMap.locationComponent.apply {

                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
                zoomWhileTracking(10.0)

                //Får tak i koordinatene med lat og lon
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if(location  == null){
                            enableLocationComponent(loadedMapStyle)
                        }else{
                            // Fant lastlocation
                            findViewById<WeatherView>(R.id.weatherView).updatePos(location)

                            //Oppdaterer kartets plassering til nåværende lat og lon:
                            updatePosition(location?.latitude!!, location?.longitude!!)
                        }
                    }
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //Tillatt, får tak i lokasjons informasjon
                enableLocationComponent(loadedStyle)
            }else{
                Toast.makeText(this, "Appen trenger posisjon for å fungere", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun showSearchbar(savedInstanceState: Bundle?) {
        val autocompleteFragment: PlaceAutocompleteFragment
        val transaction : FragmentTransaction

        if (savedInstanceState == null) {

            autocompleteFragment =
                PlaceAutocompleteFragment.newInstance(getString(R.string.mapbox_access_token))
            transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, autocompleteFragment, "fab_location_search")
            transaction.commit()

        } else {
            autocompleteFragment =
                supportFragmentManager.findFragmentByTag("fab_location_search") as PlaceAutocompleteFragment
        }

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                val coordinates = carmenFeature.center()?.coordinates()
                if (coordinates != null) {
                    updatePosition(coordinates[1], coordinates[0])
                    onCancel()
                }
            }

            override fun onCancel() {
                val fragment = supportFragmentManager.findFragmentByTag("fab_location_search")
                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
            }
        })
    }

    private fun removeIllegalZones() {
        if (this::fillManager.isInitialized) {
            fillManager.deleteAll()
            cleanedFillManager = true
        }
    }

    private fun updateIllegalZones() {
        mapView.getMapAsync { map ->
            map.getStyle {

                //Add new zones
                if (showIllegalZones) {

                    val rectF = RectF(
                        mapView.left.toFloat(),
                        mapView.top.toFloat(),
                        mapView.right.toFloat(),
                        mapView.bottom.toFloat()
                    )

                    val features = map.queryRenderedFeatures(rectF, "airport label")

                    val latLngs = ArrayList<LatLng>()
                    val radius = 5000.0

                    //Gå gjennom alle features og hent ut koordinatene
                    for (feature in features) {
                        val geometry = feature.geometry()

                        if (geometry is com.mapbox.geojson.Point) {
                            val coordinates = geometry.coordinates()
                            val latLng = LatLng(coordinates.elementAt(1), coordinates.elementAt(0))
                            latLngs.add(latLng)
                        }
                    }

                    //Regn ut til sirkler bare hvis koordinatene ikke finnes fra før
                    var foundNewZone = false
                    for (latLng in latLngs) {
                        var match = false
                        for (latLngCheck in latLngChecks) {
                            if (latLngCheck.equals(latLng)) {
                                match = true
                            }
                        }
                        if (!match) {
                            latLngChecks.add(latLng)
                            foundNewZone = true
                            circles.add(polygonCircleForCoordinate(latLng, radius))
                        }
                    }

                    //Ny zone funnet eller fillManager har blitt tømt
                    if (foundNewZone || cleanedFillManager) {
                        cleanedFillManager = false

                        //Fjern alle sirklene fra fillManager
                        if (this::fillManager.isInitialized) {
                            fillManager.deleteAll()
                        } else {
                            fillManager = FillManager(mapView, map, it)
                        }

                        //Legg til sirklene på nytt
                        val fillOptions = FillOptions()
                            .withLatLngs(circles)
                            .withFillColor("#dc1010")
                            .withFillOpacity(.5f)
                        fillManager.create(fillOptions)
                    }
                }
            }
        }
    }

    private fun polygonCircleForCoordinate(location: LatLng, radius: Double): ArrayList<LatLng> {
        val degreesBetweenPoints = 8 //45 sides
        val numberOfPoints = Math.floor((360 / degreesBetweenPoints).toDouble()).toInt()
        val distRadians = radius / 6371000.0 // earth radius in meters
        val centerLatRadians = location.latitude * Math.PI / 180
        val centerLonRadians = location.longitude * Math.PI / 180
        val polygons = arrayListOf<LatLng>() //array to hold all the points
        for (index in 0 until numberOfPoints) {
            val degrees = (index * degreesBetweenPoints).toDouble()
            val degreeRadians = degrees * Math.PI / 180
            val pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians))
            val pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians))
            val pointLat = pointLatRadians * 180 / Math.PI
            val pointLon = pointLonRadians * 180 / Math.PI
            val point = LatLng(pointLat, pointLon)
            polygons.add(point)
        }
        return polygons
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.Builder().fromUri(
                "mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"
            )
        ) {
            //Loader kartet, legger til posisjon info under
            enableLocationComponent(it)
        }
    }

    fun center(@Suppress("UNUSED_PARAMETER")view: View) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    //Fant lastlocation
                    val lat = location?.latitude!!
                    val lon = location?.longitude!!
                    updatePosition(lat, lon)
                }else{
                    Toast.makeText(this, "Fant ikke koordinater.", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

}

