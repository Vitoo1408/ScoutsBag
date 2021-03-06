package pt.ipca.scoutsbag

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.json.JSONObject
import pt.ipca.scoutsbag.activityManagement.FragmentInvite
import pt.ipca.scoutsbag.catalogManagement.FragmentCatalog
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class MainActivity : AppCompatActivity() {

    companion object {
        val IP: String = "3.8.19.24"
        val PORT: String = "60000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check internet connection
        Utils.connectionLiveData(this)

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

        //load all user details if not loaded yet
        //shared preferences initialization
        val preferences = getSharedPreferences("userLogin", MODE_PRIVATE)
        val userDetails = preferences.getString("userDetails", "")

        //convert user details json object of type string to a json object
        val userDetailsJsonObject = JSONObject(userDetails)

        //save all user details to the object UserLoggedIn
        UserLoggedIn.idUser = userDetailsJsonObject.getString("id_user").toInt()
        UserLoggedIn.userName = userDetailsJsonObject.getString("user_name")
        UserLoggedIn.birthDate = userDetailsJsonObject.getString("birth_date")
        UserLoggedIn.email = userDetailsJsonObject.getString("email")
        UserLoggedIn.pass = userDetailsJsonObject.getString("pass")
        UserLoggedIn.codType = userDetailsJsonObject.getString("cod_type")
        UserLoggedIn.contact = userDetailsJsonObject.getString("contact")
        UserLoggedIn.gender = userDetailsJsonObject.getString("gender")
        UserLoggedIn.address = userDetailsJsonObject.getString("address")
        if(userDetailsJsonObject.getString("nin") != "") UserLoggedIn.nin = userDetailsJsonObject.getString("nin")
        UserLoggedIn.imageUrl = userDetailsJsonObject.getString("image_url")
        UserLoggedIn.postalCode = userDetailsJsonObject.getString("postal_code")
        UserLoggedIn.userActive = userDetailsJsonObject.getString("user_active").toInt()
        UserLoggedIn.accepted = userDetailsJsonObject.getString("accepted").toInt()
        if(userDetailsJsonObject.getString("id_team") != "null") UserLoggedIn.idTeam = userDetailsJsonObject.getString("id_team").toInt() else UserLoggedIn.idTeam = 0
        UserLoggedIn.imageUrl = userDetailsJsonObject.getString("image_url")

        Log.d("UserLoggedIn", userDetails)
    }
}