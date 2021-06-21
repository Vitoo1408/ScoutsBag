package pt.ipca.scoutsbag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ipca.scoutsbag.activityManagement.ActivityHistoryActivity
import pt.ipca.scoutsbag.colonyManagement.ActivityUserRequest
import pt.ipca.scoutsbag.colonyManagement.ColonyActivity
import pt.ipca.scoutsbag.colonyManagement.EditProfileActivity
import pt.ipca.scoutsbag.inventoryManagement.InventoryActivity
import pt.ipca.scoutsbag.loginAndRegister.LogInOrRegisterActivity
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class FragmentMore: Fragment() {

    // Global Variables
    private lateinit var rowHistory: ConstraintLayout
    private lateinit var rowColony: ConstraintLayout
    private lateinit var rowProfile: ConstraintLayout
    private lateinit var rowRequest: ConstraintLayout
    private lateinit var rowInventory: ConstraintLayout
    private lateinit var rowLogout: ConstraintLayout

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

        // Set data
        rowColony = rootView.findViewById(R.id.row_colony)
        rowHistory = rootView.findViewById(R.id.row_activity_historic)
        rowRequest = rootView.findViewById(R.id.row_request)
        rowProfile = rootView.findViewById(R.id.row_profile)
        rowInventory = rootView.findViewById(R.id.row_inventory)
        rowLogout = rootView.findViewById(R.id.row_log_out)

        //hide user request row, inventory row and colony row from fragment more if user logged in is a scout
        if(UserLoggedIn.codType == "Esc"){
            rootView.findViewById<ConstraintLayout>(R.id.row_request).visibility = View.GONE
            rootView.findViewById<ConstraintLayout>(R.id.row_inventory).visibility = View.GONE
            rootView.findViewById<ConstraintLayout>(R.id.row_colony).visibility = View.GONE
        }

        return rootView
    }

    /*
        This function configures the fragment after its creation
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

        // Button on click events
        rowColony.setOnClickListener {
            val intent = Intent(activity, ColonyActivity::class.java)
            startActivity(intent)
        }

        rowHistory.setOnClickListener {
            val intent = Intent(activity, ActivityHistoryActivity::class.java)
            startActivity(intent)
        }

        rowRequest.setOnClickListener {
            val intent = Intent(activity, ActivityUserRequest::class.java)
            startActivity(intent)
        }

        rowProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }


        rowInventory.setOnClickListener {
            val intent = Intent(activity, InventoryActivity::class.java)
            startActivity(intent)
        }

        rowLogout.setOnClickListener {
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

            Toast.makeText(this.activity, "Terminou sessão com sucesso", Toast.LENGTH_LONG).show()

            //go back to the login or register activity
            val intent = Intent(this.activity, LogInOrRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}