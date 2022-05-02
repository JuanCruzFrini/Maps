package com.example.maps

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{

    private lateinit var map:GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createFragment()
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
        createPolylines()
    }

    private fun createPolylines(){
        //primero creamos las opciones
        val polylineOptions = PolylineOptions()
                .add(LatLng(40.419173113350965,-3.705976009368897))
                .add(LatLng( 40.4150807746539, -3.706072568893432))
                .add(LatLng( 40.41517062907432, -3.7012016773223873))
                .add(LatLng( 40.41713105928677, -3.7037122249603267))
                .add(LatLng( 40.41926296230622,  -3.701287508010864))
                .add(LatLng( 40.419173113350965, -3.7048280239105225))
                .add(LatLng(40.419173113350965,-3.705976009368897))
                .width(20f)
                .color(ContextCompat.getColor(this, R.color.purple_200))

        //creamos la polylinea y le añadimos las opciones recien hechas
        val polyline = map.addPolyline(polylineOptions)

        polyline.startCap = RoundCap() // redondea borde inicial
        //polyline.startCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground))

        //añadimos un patron de trazo a la linea
        val pattern = listOf(Dot(), Gap(10f), Dash(50f), Gap(10f))
        polyline.pattern = pattern

        polyline.isClickable = true

        map.setOnPolylineClickListener { changeColor(polyline) }
    }

    //cambia el color de las polylineas al clickear
    fun changeColor(polyline: Polyline){
        val color = (0..3).random()
        when(color){
            0 -> polyline.color = ContextCompat.getColor(this, R.color.black)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.teal_200)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.purple_700)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.teal_700)
        }
    }

    //creamos un marcador
    private fun createMarker() {
        val coordinates = LatLng(40.417287, -3.703609)
        val marker = MarkerOptions().position(coordinates).title("El club del bicho SIUUUUU")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 15f),
            4000,
            null
        )
    }

    // Esta el permiso de localizacion activado?
    // devuelve si esta el permiso aceptado (true or false)
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    //intentamos activar la localizacion
    private fun enableLocation(){
        if (!::map.isInitialized) return // si el mapa no se creo, salta este metodo
        if (isLocationPermissionGranted()){
            map.isMyLocationEnabled  = true
        } else {
            requestLocationPermission()
        }
    }

    //pedimos los permisos
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    //verificamos que los permisos se aceptaron
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "Para activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else ->{ }
        }
    }

    //controlamos cuando el user vuelva a la app, si los permisos siguen activos
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    //si tocamos el boton de localizacion, de la impl de GoogleMap.OnMyLocationButtonClickListener
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "boton pulsado", Toast.LENGTH_SHORT).show()
        return false // Si lo seteamos a true, no nos lleva a la ubicacion al tocar el boton
    }

    //si tocamos la localizacion, de la impl de GoogleMap.OnMyLocationClickListener
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}