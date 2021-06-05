package pt.ipca.scoutsbag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import pt.ipca.scoutsbag.colonyManagement.ActivityUserRequest
import pt.ipca.scoutsbag.colonyManagement.ColonyActivity
import pt.ipca.scoutsbag.loginAndRegister.LogInOrRegisterActivity
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class FragmentMore: Fragment() {

    // Global Variables
    lateinit var row_colony: ConstraintLayout
    lateinit var row_request: ConstraintLayout
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
        row_request = rootView.findViewById(R.id.row_request)

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

        return rootView
    }

     /*
        This function configures the fragment after its creation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button on click events
        row_colony.setOnClickListener {
            val intent = Intent(activity, ColonyActivity::class.java)
            startActivity(intent)
        }

         row_request.setOnClickListener {
             val intent = Intent(activity, ActivityUserRequest::class.java)
             startActivity(intent)
         }
    }
}