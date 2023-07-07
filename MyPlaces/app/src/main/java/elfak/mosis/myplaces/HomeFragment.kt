package elfak.mosis.myplaces

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.myplaces.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var auth: FirebaseAuth = Firebase.auth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(auth.currentUser)
        binding.buttonLogin?.setOnClickListener {
            if(!TextUtils.isEmpty(binding.editTextEmail?.text.toString()) && !TextUtils.isEmpty(binding.editTextPassword?.text.toString())) {
                val email = binding.editTextEmail?.text.toString()
                val password = binding.editTextPassword?.text.toString()
                login(email, password)
            }
            else
            {
                Toast.makeText(
                    context,
                    "Unesite email i password",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        binding.buttonRegister?.setOnClickListener {
            this.findNavController().navigate(R.id.action_HomeFragment_to_RegisterFragment)
        }
        binding.buttonLogout?.setOnClickListener{
            auth.signOut()
            updateUI(null)
        }
        // Initialize Firebase Auth

    }
    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    updateUI(task.result.user)
                //this.findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
    fun updateUI(auth: FirebaseUser?){
        if(auth == null){
            binding.buttonLogin?.visibility = VISIBLE
            binding.buttonRegister?.visibility = VISIBLE
            binding.buttonLogout?.visibility = GONE
            binding.editTextEmail?.visibility = VISIBLE
            binding.editTextPassword?.visibility = VISIBLE
            setHasOptionsMenu(false)
        }
        else
        {
            binding.buttonLogin?.visibility = GONE
            binding.buttonRegister?.visibility = GONE
            binding.buttonLogout?.visibility = VISIBLE
            binding.editTextEmail?.visibility = GONE
            binding.editTextPassword?.visibility = GONE
            setHasOptionsMenu(true)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.action_my_places_list -> {
                this.findNavController().navigate(R.id.action_HomeFragment_to_ListFragment)
                true
            }
            R.id.action_new_place -> {
                this.findNavController().navigate(R.id.action_HomeFragment_to_EditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}