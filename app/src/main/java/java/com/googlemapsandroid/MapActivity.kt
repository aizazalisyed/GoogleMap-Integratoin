    package java.com.googlemapsandroid

    import android.Manifest
    import android.content.Context
    import android.content.pm.PackageManager
    import android.location.Address
    import android.location.Geocoder
    import android.nfc.Tag
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log
    import android.view.KeyEvent
    import android.view.inputmethod.EditorInfo
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.FragmentManager
    import com.google.android.gms.location.FusedLocationProviderClient
    import com.google.android.gms.location.LocationServices
    import com.google.android.gms.maps.CameraUpdateFactory
    import com.google.android.gms.maps.GoogleMap
    import com.google.android.gms.maps.SupportMapFragment
    import com.google.android.gms.maps.model.LatLng
    import com.google.android.gms.tasks.Task
    import java.io.IOException
    import kotlin.math.log

    class MapActivity : AppCompatActivity() {

        val TAG = "MapActivity"
        val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        var permissionGranted : Boolean  = false
        val LOCATION_PERMISSION_REQUEST_CODE = 1234
        lateinit var mMap : GoogleMap
        lateinit var fusedLocationProviderClient : FusedLocationProviderClient
        val DEFAULT_ZOOM = 15f

        lateinit var searchText : EditText
        lateinit var searchIcon : ImageView


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_map)

            searchText = findViewById(R.id.input_search)
            searchIcon = findViewById(R.id.ic_magnify)
            getLocationPermission()

            searchIcon.setOnClickListener {
                Log.d(TAG, "Performing search")
                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
                geoLocate()
            }
        }



        fun geoLocate(){
            Log.d(TAG, "geo locating")
            var searchString = searchText.text.toString()
            var geocoder = Geocoder(this)

            var list = emptyList<Address>()

            try {
                list = geocoder.getFromLocationName(searchString, 1)!!

            }catch( e : IOException){
                Log.d(TAG, e.message.toString())
            }

            if(list.isNotEmpty()){
                var address = list[0]
                Log.d(TAG, address.toString())
            }
        }

        fun initMap() {
            var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

            mapFragment.getMapAsync { googleMap ->
                mMap = googleMap
                if (permissionGranted) {
                    getDeviceLocation()
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@getMapAsync
                    }
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        }

        fun getDeviceLocation(){

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            try {

                if(permissionGranted){
                var task = fusedLocationProviderClient.lastLocation
                task.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "found Location")
                        var currentLocation = task.result
                        moveCamera(
                            LatLng(currentLocation.latitude, currentLocation.longitude),
                            DEFAULT_ZOOM
                        )
                    } else {
                        Log.d(TAG, "unable to find Location")
                        Toast.makeText(this, "unable to find Location", Toast.LENGTH_SHORT).show()
                    }
                }
                }

            }catch (e: SecurityException){
                Log.d(TAG,e.message.toString())
            }
        }

        fun moveCamera(latLong: LatLng, zoom : Float){

            Log.d(TAG,"Moving Camera to" + latLong.latitude.toString() + "," + latLong.longitude.toString())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoom))
        }
        fun getLocationPermission(){
            val permissions = arrayOf(
                FINE_LOCATION,
               COARSE_LOCATION
            )

            if(ContextCompat.checkSelfPermission( applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission( applicationContext, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true
                    initMap()

                }
                else{
                    ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE )
                }
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE )
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            when(requestCode){
                LOCATION_PERMISSION_REQUEST_CODE ->  {
                    if(grantResults.isNotEmpty()){
                        for(result in grantResults){
                            if(result != PackageManager.PERMISSION_GRANTED){
                                permissionGranted = false
                                return
                            }
                        }
                        permissionGranted = true
                        //initialize our map
                        initMap()
                    }
                }
            }

        }
    }