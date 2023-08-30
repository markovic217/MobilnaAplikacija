package elfak.mosis.myplaces

import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import elfak.mosis.myplaces.data.User
import elfak.mosis.myplaces.databinding.FragmentRegisterBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private val storageRef = Firebase.storage.reference

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        database = Firebase.database.reference
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    private val pickImage = 100
    private val takePhoto = 123
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView = binding.imageView

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val name = binding.editTextName.text.toString()
            val surname = binding.editTextSurname.text.toString()
            val phoneNumber = binding.editTextPhoneNumber.text.toString()
            if (!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(surname) &&
                !TextUtils.isEmpty(phoneNumber) &&
                !TextUtils.isEmpty(imageUri.toString())) {


                register(email, password, name, surname, phoneNumber, imageUri.toString())
            }
            else
            {
                Toast.makeText(
                    context,
                    "Unesite odgovarajuca polja",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        binding.buttonBack.setOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.buttonChooseImage.setOnClickListener{
            try {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, pickImage)
            }
            catch(e: Exception){
                Log.i("firebase:", e?.message.toString())
            }
        }
        binding.buttonTakePhoto.setOnClickListener({
            try{
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Start the activity with camera_intent, and request pic id
                startActivityForResult(cameraIntent, takePhoto)
            }
            catch(e: Exception){
                Log.i("firebase:", e?.message.toString())
            }
        })
        // Initialize Firebase Auth
        auth = Firebase.auth
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK && requestCode == pickImage) {
                imageUri = data?.data
                imageView.setImageURI(imageUri)
            }
            if(resultCode == RESULT_OK && requestCode == takePhoto){
                imageUri = data?.data
                imageView.setImageBitmap(data!!.extras!!["data"] as Bitmap?)
                data!!.data
            }
        }
        catch(e: Exception){
            Log.i("firebase:", e?.message.toString())
        }
    }


    fun register(email: String, password: String, name: String, surname: String, phoneNumber: String, pictureSrc: String){
        val user = User(email, password, name, surname, phoneNumber, pictureSrc)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Uspesna registracija",
                        Toast.LENGTH_SHORT,
                    ).show()
                    try {
                        database.child("users").child(task.result.user?.uid.toString()).setValue(user)
                        val imageRef = storageRef.child("avatars/${task.result.user?.uid.toString()}")
                        val uploadTask = imageRef.putFile(imageUri as Uri)
                        /*database.child("users").child(em).get().addOnSuccessListener {
                            Log.i("firebase", "Got value ${it.value}")
                        }.addOnFailureListener{
                            Log.e("firebase", "Error getting data", it)
                        }*/
                    }
                    catch(e: Exception){
                       Log.i("firebase:", e?.message.toString())
                    }
                        this.findNavController().popBackStack()
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}