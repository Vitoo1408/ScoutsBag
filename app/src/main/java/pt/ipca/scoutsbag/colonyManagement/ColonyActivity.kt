package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

class ColonyActivity : AppCompatActivity() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : UsersAdapter
    lateinit var buttonAddTeam : FloatingActionButton
    var users : List<User> = arrayListOf()
    var teams : List<Team> = arrayListOf()

    /*
        This function create the view
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colony)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Agrupamento"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        // Get the values to the lists
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllTeams {
                teams = it
            }

            Backend.getAllAcceptedUsers {
                users = it
            }

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        // Set data
        listView = findViewById(R.id.listview_colony)
        adapter = UsersAdapter()
        buttonAddTeam = findViewById(R.id.buttonAddTeam)
        listView.adapter = adapter

        // Button on click events
        buttonAddTeam.setOnClickListener {
            val intent = Intent(this, AddTeam::class.java)
            intent.putExtra("idTeam", teams.size + 1)
            startActivity(intent)
        }

    }


    inner class UsersAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return users.size
        }

        override fun getItem(position: Int): Any {
            return users[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_colony, parent, false)

            // Get current activity
            val user = users[position]

            // Variables in the row
            val textViewName    = rowView.findViewById<TextView>(R.id.textView_user_name)
            val textViewSection = rowView.findViewById<TextView>(R.id.textView_user_section)
            val textViewTeam    = rowView.findViewById<TextView>(R.id.textView_user_team)
            val textViewNin     = rowView.findViewById<TextView>(R.id.textView_user_nin)
            val profileImage    = rowView.findViewById<ImageView>(R.id.profileImageColony)

            // Set values in the row
            textViewName.text = user.userName.toString()
            if(user.idTeam!! != null) {
                textViewSection.text = Backend.getSectionName(Backend.getTeamById(user.idTeam!!, teams).idSection!!)
                textViewTeam.text = Backend.getTeamById(user.idTeam!!, teams).teamName
            } else {
                textViewSection.text = "Sem secção"
                textViewTeam.text = "Sem Equipa"
            }

            if(user.nin != "" && user.nin != null && user.nin != "null") {
                textViewNin.text = "Nin: " + user.nin.toString()
            } else {
                textViewNin.text = "Nin :"
            }

            if(!user.imageUrl.isNullOrEmpty()) {
                Picasso.with(this@ColonyActivity).load(user.imageUrl).into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.ic_perfil)
            }


            rowView.setOnClickListener {
                val intent = Intent(this@ColonyActivity, ProfileActivity::class.java)
                intent.putExtra("user", user.toJson().toString())
                intent.putExtra("team", textViewTeam.text.toString())
                intent.putExtra("section", textViewSection.text.toString())
                startActivity(intent)
            }

            return rowView
        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}