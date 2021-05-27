package pt.ipca.scoutsbag

import android.os.Bundle
import android.text.Html
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    companion object {
        val IP: String = "3.8.19.24"
        val PORT: String = "60000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getActionBar()?.setTitle(Html.fromHtml("<font color=\"red\">"))

        // Variables
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.itemIconTintList = null

        // Passing each fragment ID as a set of Ids
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_activity))

        // Build the navigation view
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Hide the action bar in the top
        supportActionBar?.hide()
    }

}