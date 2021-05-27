package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

class ColonyActivity : AppCompatActivity(), ColonyDbHelper {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : UsersAdapter
    lateinit var buttonAddTeam : FloatingActionButton
    var users : MutableList<User> = arrayListOf()
    var teams : MutableList<Team> = arrayListOf()


    /*
        This function create the view
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colony)

        // Get the values to the lists
        GlobalScope.launch(Dispatchers.IO) {

            users = getAllUsers()

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

            // Set values in the row
            textViewName.text = user.userName.toString()
            //textViewSection.text = getSectionName(user.idTeam!!)
            // textViewTeam.text = getTeamById(user.idUser!!).teamName
            textViewNin.text = user.nin.toString()

            rowView.setOnClickListener {
                val intent = Intent(this@ColonyActivity, ProfileActivity::class.java)
                intent.putExtra("user", user.toJson().toString())
                startActivity(intent)
            }

            return rowView
        }
    }


    /*
        This function returns all teams in the api to an list
     */
    private fun getTeamsList(){

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

            // Create the http request
            val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/teams").build()

            // Send the request and analyze the response
            OkHttpClient().newCall(request).execute().use { response ->

                // Convert the response into string then into JsonArray
                val teamJsonArrayStr : String = response.body!!.string()
                val teamJsonArray = JSONArray(teamJsonArrayStr)

                // Add the elements in the list
                for (index in 0 until teamJsonArray.length()) {
                    val jsonArticle = teamJsonArray.get(index) as JSONObject
                    val team = Team.fromJson(jsonArticle)
                    teams.add(team)
                }

                // Update the list
                GlobalScope.launch (Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }


    /*

     */
    private fun getSectionName(id: Int): String{

        //
        return when (id) {
            1 -> "Lobitos"
            2 -> "Exploradores"
            3 -> "Pioneiros"
            else -> "Caminheiros"
        }

    }




    /*
        This function returns the team
     */
    private fun getTeamById(id: Int): Team {

        // Variables
        var response: Team? = null

        // Find the activity type
        for (element in teams) {
            if (element.idTeam == id)
                response = element
        }

        return response!!
    }

    /*
        This function returns the section designation

    private fun getSectionById(id: Int): Section {

        // Variables
        var response: Section? = null

        // Find the activity type
        for (element in sections) {
            if (element.idSection == id)
                response = element
        }

        return response!!
    }
    */


}