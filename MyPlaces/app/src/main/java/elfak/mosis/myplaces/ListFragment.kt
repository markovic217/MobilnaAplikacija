package elfak.mosis.myplaces

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.viewModels
import elfak.mosis.myplaces.databinding.FragmentSecondBinding
import elfak.mosis.myplaces.model.MyPlacesViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val myPlacesViewModel: MyPlacesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //places = ArrayList<String>()
        myPlacesViewModel.addPlace("Tvrdjava")
        myPlacesViewModel.addPlace("Cair")
        myPlacesViewModel.addPlace("Park Svetog Save")
        myPlacesViewModel.addPlace("Trg Kralja Milana")
        val myPlacesList: ListView = requireView().findViewById<ListView>(R.id.my_places_list)
        myPlacesList.adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1, myPlacesViewModel.myPlacesList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}