package elfak.mosis.myplaces

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.myplaces.data.Holes
import elfak.mosis.myplaces.data.User
import elfak.mosis.myplaces.model.LocationViewModel
import elfak.mosis.myplaces.model.HolesViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.LocationUtils
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.Integer.parseInt


class MapFragment : Fragment() {
    lateinit var map: MapView
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val holesViewModel: HolesViewModel by activityViewModels()
    private lateinit var database: DatabaseReference
    private val items = ArrayList<OverlayItem>();
    private var itemOverlay: ItemizedIconOverlay<OverlayItem>? = null
    private lateinit var gesture:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_new_place -> {
                this.findNavController().navigate(R.id.action_MapFragment_to_EditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setOnMapClickOverlay(){
        var receive = object:MapEventsReceiver{
            override fun singleTapConfirmedHelper(p:GeoPoint):Boolean{
                var lon = p.longitude.toString()
                var lat = p.latitude.toString()
                locationViewModel.setLocation(lon,lat)
                findNavController().popBackStack()
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        var overlayEvents = MapEventsOverlay(receive)
        map.overlays.add(overlayEvents)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        var item = menu.findItem(R.id.action_my_places_list)
        item.isVisible = false
        item= menu.findItem(R.id.action_show_map)
        item.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gesture = object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
            override fun  onItemSingleTapUp(index: Int,
                                            item: OverlayItem): Boolean {

                database.child("holes/${item.uid}").get().addOnSuccessListener {
                    val id: String = it.child("id").value.toString()
                    val imageURI: String = it.child("imageURI").value.toString()
                    val latitude: String = it.child("latitude").value.toString()
                    val longitude: String = it.child("longitude").value.toString()
                    val title: String = it.child("title").value.toString()
                    val postedBy: String? = it.child("postedBy").value.toString()
                    val hole: Holes = Holes(title,  imageURI, longitude, latitude, postedBy)
                    hole.id = id
                    holesViewModel.selected = hole
                    findNavController().navigate(R.id.action_MapFragment_to_ViewFragment)
                }
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }
        }
        super.onViewCreated(view, savedInstanceState)
        var ctx: Context? = activity?.applicationContext;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences((ctx)))
        map = requireView().findViewById<MapView>(R.id.map)
        map.setMultiTouchControls(true)
        if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        else{
            try {
                database.child("holes").get().addOnSuccessListener {
                    for (data in it.children) {
                        val id: String = data.child("id").value.toString()
                        val latitude: String = data.child("latitude").value.toString()
                        val longitude: String = data.child("longitude").value.toString()
                        var item = OverlayItem(
                            id,
                            "Hole",
                            "snippet",
                            GeoPoint(latitude.toDouble(), longitude.toDouble())
                            //GeoPoint(43.004, 21.9363)
                        )
                        items.add(item)
                    }
                }
            }
            catch(e:Exception){
                 Log.i("Firebase:", e?.message.toString())
            }
            setupMap()
        }

    }
    private fun setupMap(){
        map.controller.setZoom(15.0)

        var startPoint = GeoPoint(42.9963, 21.9430)
        map.controller.setCenter(startPoint)
        if(locationViewModel.setLocation){
            setOnMapClickOverlay()
        }
        else{
            if(holesViewModel.selected!= null){
                startPoint = GeoPoint(holesViewModel.selected!!.latitude.toDouble(), holesViewModel.selected!!.longitude.toDouble())
            }
            else{
                setMyLocationOverlay()
            }
        }
        map.controller.animateTo(startPoint)


    }
    private fun setMyLocationOverlay() {

        var myLocationOverlay = object: MyLocationNewOverlay(GpsMyLocationProvider(activity), map){
            override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
                super.onLocationChanged(location, source)
                var result: FloatArray = FloatArray(3)
                var filteredItems = ArrayList<OverlayItem>()
                try{
                    itemOverlay?.removeAllItems()
                }
                catch (e:Exception){

                }
                map.invalidate()
                items.forEach{
                    try {
                        Location.distanceBetween(
                            location?.latitude as Double,
                            location?.longitude as Double,
                            it.point.latitude,
                            it.point.longitude, result
                        )
                        if (result[0] < 1500)
                            filteredItems.add(it);
                    }
                    catch(e:Exception){
                        Log.i("Item:", e?.message.toString())
                    }
                }
                itemOverlay = ItemizedIconOverlay(filteredItems, gesture, context)
                map.overlays.add(itemOverlay)
            }
        }
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        map.overlays.add(myLocationOverlay)
        }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            isGranted:Boolean ->
            if(isGranted){
                setMyLocationOverlay()
                setOnMapClickOverlay()
            }
        }

    override fun onResume(){
        super.onResume()
        map.onResume()
    }

    override fun onPause(){
        super.onPause()
        map.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        database = Firebase.database.reference
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


}