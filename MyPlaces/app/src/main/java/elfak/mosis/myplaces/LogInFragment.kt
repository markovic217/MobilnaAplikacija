package elfak.mosis.myplaces

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.myplaces.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LogInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogInFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonLogin.setOnClickListener {
            if(!TextUtils.isEmpty(binding.editTextEmail.text.toString()) && !TextUtils.isEmpty(binding.editTextPassword.text.toString())) {
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                login(email, password)
            }else
            {
                Toast.makeText(
                    context,
                    "Unesite email i password",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        binding.buttonRegister.setOnClickListener {
            this.findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
         .addOnCompleteListener() { task ->
             if (task.isSuccessful) {
                 this.findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
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