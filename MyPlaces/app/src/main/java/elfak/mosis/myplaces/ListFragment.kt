package elfak.mosis.myplaces

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.myplaces.data.Holes
import elfak.mosis.myplaces.databinding.FragmentSecondBinding
import elfak.mosis.myplaces.model.HolesViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var database: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val holesViewModel: HolesViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val holesList: ListView = requireView().findViewById<ListView>(R.id.my_places_list)
        //places = ArrayList<String>()
        database.child("holes").get().addOnSuccessListener {
            for(data in it.children){
                val id: String = data.child("id").value.toString()
                val imageURI: String = data.child("imageURI").value.toString()
                val latitude: String = data.child("latitude").value.toString()
                val longitude: String = data.child("longitude").value.toString()
                val title: String = data.child("title").value.toString()
                val postedBy: String? = data.child("postedBy").value.toString()
                val hole: Holes = Holes(title,  imageURI, longitude, latitude, postedBy)
                hole.id = id
                holesViewModel.addHole(hole)
            }

            holesList.adapter = ArrayAdapter<Holes>(view.context, android.R.layout.simple_list_item_1, holesViewModel.HolesList)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        holesList.setOnItemClickListener(object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var hole: Holes = p0?.adapter?.getItem(p2) as Holes
                Toast.makeText(view.context, hole.toString(), Toast.LENGTH_SHORT).show()
                holesViewModel.selected = hole;
                findNavController().navigate(R.id.action_ListFragment_to_ViewFragment)
            }
        })
        holesList.setOnCreateContextMenuListener(object: View.OnCreateContextMenuListener {
            override fun onCreateContextMenu(
                menu: ContextMenu,
                v: View?,
                menuInfo: ContextMenu.ContextMenuInfo
            ) {
                val info = menuInfo as AdapterContextMenuInfo
                val hole:Holes = holesViewModel.HolesList[info.position]
                menu.setHeaderTitle(hole.title)
                menu.add(0, 1, 1, "View place")
                menu.add(0, 2, 2, "Edit place")
                menu.add(0, 3, 3, "Delete place")
                menu.add(0, 4, 4, "Show on map")
            }
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        if (item.itemId === 1) {
            holesViewModel.selected = holesViewModel.HolesList[info.position]
            this.findNavController().navigate(R.id.action_ListFragment_to_ViewFragment)
        } else if (item.itemId === 2) {
            holesViewModel.selected = holesViewModel.HolesList[info.position]
            this.findNavController().navigate(R.id.action_ListFragment_to_EditFragment)
        }
        if (item.itemId === 3) {
            holesViewModel.HolesList.removeAt(info.position)
            val holesList: ListView = requireView().findViewById<ListView>(R.id.my_places_list)
            holesList.adapter = this@ListFragment.context?.let { ArrayAdapter<Holes>(it, android.R.layout.simple_list_item_1, holesViewModel.HolesList) }
        }else if (item.itemId === 4) {
            holesViewModel.selected = holesViewModel.HolesList[info.position]
            this.findNavController().navigate(R.id.action_ListFragment_to_MapFragment)
        }
        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.action_new_place -> {
                this.findNavController().navigate(R.id.action_ListFragment_to_EditFragment)
                true
            }
           else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_my_places_list)
        item.isVisible = false;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        holesViewModel.HolesList = ArrayList<Holes>()
        _binding = null
    }
}