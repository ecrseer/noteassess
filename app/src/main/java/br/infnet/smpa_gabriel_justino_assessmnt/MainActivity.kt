package br.infnet.smpa_gabriel_justino_assessmnt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.ActivityMainBinding

import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel:MainActivityViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
    }
    fun showSnackMesssage(message:String){
        Snackbar.make(binding.container,
            "$message",
            Snackbar.LENGTH_LONG+4242).show()
    }
    fun watchNavigationGuard(loggedIn: Boolean) {
        if (loggedIn) navController.navigate(R.id.navigation_dashboard)
        else navController.navigate(R.id.navigation_home)
    }



    override fun onStart() {
        super.onStart()
        with(viewModel){
            actionsState.observe(this@MainActivity, Observer {actionState->
                if(actionState.lastActionTaken == PossibleActions.FILEWRITEN){
                    showSnackMesssage("Arquivo escrito em "+actionState.filePathChanged)
                }
            })
            isUserLoggedIn.observe(this@MainActivity, Observer { loggedIn ->
                watchNavigationGuard(loggedIn)
            })

        }


    }
}