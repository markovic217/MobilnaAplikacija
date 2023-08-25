package elfak.mosis.myplaces

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.myplaces.data.Holes
import elfak.mosis.myplaces.databinding.FragmentSecondBinding
import elfak.mosis.myplaces.model.LocationViewModel
import elfak.mosis.myplaces.model.HolesViewModel
import elfak.mosis.myplaces.model.UserViewModel
import java.io.File
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var database: DatabaseReference
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val holesViewModel: HolesViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val storageRef = Firebase.storage.reference
    private val auth = Firebase.auth

    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        imageView = requireView().findViewById<ImageView>(R.id.imageView3)
        requireView().findViewById<Button>(R.id.buttonChooseImage3).setOnClickListener{
            try {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            }
            catch(e: Exception){
                Log.i("firebase:", e?.message.toString())
            }
        }
        val editName: EditText = requireView().findViewById<EditText>(R.id.editmyplace_name_edit)
        //val editDesc: EditText = requireView().findViewById<EditText>(R.id.editmyplace_desc_edit)
        val editLongitude: EditText = requireView().findViewById<EditText>(R.id.editmyplace_longitude_edit)
        val lonObserver = Observer<String> { newValue -> editLongitude.setText(newValue.toString())}
        locationViewModel.longitude.observe(viewLifecycleOwner, lonObserver)
        val editLatitude: EditText = requireView().findViewById<EditText>(R.id.editmyplace_latitude_edit)
        val latObserver = Observer<String> { newValue -> editLatitude.setText(newValue.toString())}
        locationViewModel.latitude.observe(viewLifecycleOwner, latObserver)

        if(holesViewModel.selected!=null){
            editName.setText(holesViewModel.selected?.title)
            imageView.setImageURI(Uri.parse(holesViewModel.selected?.imageURI))
        }
        val addButton:Button = requireView().findViewById<Button>(R.id.editmyplace_finished_button);
        addButton.isEnabled = false;
        if(holesViewModel.selected!=null)
            addButton.setText(R.string.editmyplace_save_label)
        editName.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                addButton.isEnabled = (editName.text.length>0)
            }
            override fun beforeTextChanged(c:CharSequence?, start:Int, count:Int, after:Int){

            }
            override fun onTextChanged(c:CharSequence?, start:Int, count:Int, after:Int){

            }
        })
        addButton.setOnClickListener{
            val editName: EditText = requireView().findViewById<EditText>(R.id.editmyplace_name_edit)
            val name: String = editName.text.toString()
            //val editDesc: EditText = requireView().findViewById<EditText>(R.id.editmyplace_desc_edit)
            //val desc:String = editDesc.text.toString()
            val longitude:String = editLongitude.text.toString()
            val latitude:String = editLatitude.text.toString()
            val holeObject = Holes(name, imageUri.toString(), longitude, latitude, userViewModel.loggedUser?.name)
            if(holesViewModel.selected!=null){
               holesViewModel.selected?.title = name
                holesViewModel.selected?.imageURI = imageUri.toString()
                holesViewModel.selected?.longitude = longitude
                holesViewModel.selected?.latitude = latitude
            }
            else{
                holesViewModel.addHole(holeObject)
            try {
                database.child("holes").child(holeObject.id.toString()).setValue(holeObject)
                //var file = Uri.fromFile(File(imageUri.toString()))
                val imageRef = storageRef.child("images/${holeObject.id.toString()}")
                val uploadTask = imageRef.putFile(imageUri as Uri)
                Log.i("firebase:", auth.currentUser?.uid.toString())
            }
            catch(e: Exception){
                Log.i("firebase:", e?.message.toString())
            }
            }
            holesViewModel.selected = null
            locationViewModel.setLocation("", "")
            findNavController().popBackStack();
        }
        val cancelButton:Button = requireView().findViewById<Button>(R.id.editmyplace_cancel_button)
        cancelButton.setOnClickListener{
            holesViewModel.selected = null
            locationViewModel.setLocation("", "")
            findNavController().popBackStack();
        }
        val setButton: Button = requireView().findViewById<Button>(R.id.editmyplace_location_button)
        setButton.setOnClickListener{
            locationViewModel.setLocation = true;
            findNavController().navigate(R.id.action_EditFragment_to_MapFragment)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        database = Firebase.database.reference
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
                imageUri = data?.data
                imageView.setImageURI(imageUri)
            }
        }
        catch(e: Exception){
            Log.i("firebase:", e?.message.toString())
        }
    }

    override fun onDestroyView() {
        holesViewModel.HolesList = ArrayList<Holes>()
        super.onDestroyView()
    }
}