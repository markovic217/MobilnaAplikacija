package elfak.mosis.myplaces

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.myplaces.databinding.FragmentViewBinding
import elfak.mosis.myplaces.model.HolesViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewFragment : Fragment() {
    private val holesViewModel: HolesViewModel by activityViewModels()
    private var _binding: FragmentViewBinding? = null
    private val binding get() = _binding!!
    private val storageRef = Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val pathReference = storageRef.child("images/${holesViewModel.selected?.id.toString()}")
        try {
            pathReference.downloadUrl.addOnSuccessListener {
                Log.i("It:", it.toString())
                Glide.with(this).load(it).into(requireView().findViewById<ImageView>(R.id.viewmyplace_imageview))
            //requireView().findViewById<ImageView>(R.id.viewmyplace_imageview).setImageURI(it)
            }
        }
        catch(e: Exception){
            Log.i("Storage:", e?.message.toString())
        }
        super.onViewCreated(view, savedInstanceState)
        binding.viewmyplaceNameText.text=holesViewModel.selected?.title
        binding.viewmyplaceImageview.setImageURI(Uri.parse(holesViewModel.selected?.imageURI))
        binding.viewmyplaceUserText.text = holesViewModel.selected?.postedBy
        binding.viewmyplaceFinishedButton.setOnClickListener{
            holesViewModel.selected = null
            findNavController().popBackStack();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=  null
        holesViewModel.selected = null
    }
}