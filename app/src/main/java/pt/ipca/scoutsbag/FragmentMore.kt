package pt.ipca.scoutsbag

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipca.scoutsbag.activityManagement.CreateActivityActivity
import pt.ipca.scoutsbag.loginAndRegister.LogInOrRegisterActivity
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.userManagement.ColonyActivity

class FragmentMore: Fragment() {

    // Global Variables
    lateinit var row_colony: ConstraintLayout
    var buttonLogOut: Button? = null

    /*
       This function create the view
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_more, container, false)

        buttonLogOut = rootView.findViewById(R.id.buttonLogOut)

        // Set data
        row_colony = rootView.findViewById(R.id.row_colony)

        buttonLogOut?.setOnClickListener {
            val preferences = this.activity?.getSharedPreferences("userLogin", AppCompatActivity.MODE_PRIVATE)
            val editor = preferences?.edit()
            editor?.putString("loggedIn", "false")
            editor?.apply()

            //reset all user data
            UserLoggedIn.idUser = null
            UserLoggedIn.userName = null
            UserLoggedIn.birthDate = null
            UserLoggedIn.email = null
            UserLoggedIn.pass = null
            UserLoggedIn.codType = null
            UserLoggedIn.contact = null
            UserLoggedIn.gender = null
            UserLoggedIn.address = null
            UserLoggedIn.nin = null
            UserLoggedIn.imageUrl = null
            UserLoggedIn.postalCode = null
            UserLoggedIn.userActive =null
            UserLoggedIn.accepted = null
            UserLoggedIn.idTeam = null

            //go back to the login or register activity
            val intent = Intent(this.activity, LogInOrRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        println("CONA02")

        return rootView
    }

    /*
        This function configures the fragment after its creation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("CONA1")

        // Button on click events
        row_colony.setOnClickListener {
            val intent = Intent(activity, ColonyActivity::class.java)
            println("CONA2")
            startActivity(intent)
        }
    }


}
