package elfak.mosis.myplaces

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.NavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import elfak.mosis.myplaces.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var auth : FirebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val fab: FloatingActionButton = findViewById(R.id.fab)
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.fab.hide()
        /*navController.addOnDestinationChangedListener{controller, destination, arguments ->
            if(destination.id == R.id.EditFragment || destination.id == R.id.ViewFragment)
                binding.fab.hide()
            else
                binding.fab.show()
        }*/

        binding.fab.setOnClickListener { view ->
            if (navController.currentDestination?.id == R.id.HomeFragment)
                navController.navigate(R.id.action_HomeFragment_to_EditFragment)
            else if (navController.currentDestination?.id == R.id.ListFragment)
                navController.navigate(R.id.action_ListFragment_to_EditFragment)
            else if (navController.currentDestination?.id == R.id.MapFragment)
                navController.navigate(R.id.action_MapFragment_to_EditFragment)

        }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId) {
            R.id.action_show_map -> {
                if(navController.currentDestination?.id == R.id.HomeFragment)
                navController.navigate(R.id.action_HomeFragment_to_MapFragment)
                else if(navController.currentDestination?.id == R.id.ListFragment)
                    navController.navigate(R.id.action_ListFragment_to_MapFragment)
            }
            R.id.action_about -> {
                val i: Intent = Intent(this,About::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}