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

class ActivityUserRequest : AppCompatActivity(), ColonyDbHelper {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : UsersAdapter
    var users : MutableList<User> = arrayListOf()
    var teams : MutableList<Team> = arrayListOf()

    /*
        This function create the view
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_request)

        getTeamsList()

        // Get the values to the lists
        GlobalScope.launch(Dispatchers.IO) {

            users = getAllUnacceptedUsers()


            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        // Set data
        listView = findViewById(R.id.listview_colony)
        adapter = UsersAdapter()
        listView.adapter = adapter
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
        val rowView = layoutInflater.inflate(R.layout.row_user_request, parent, false)

        // Get current activity
        val user = users[position]

        // Variables in the row
        val textViewName    = rowView.findViewById<TextView>(R.id.textView_user_name)
        val textViewSection = rowView.findViewById<TextView>(R.id.textView_user_section)
        val textViewTeam    = rowView.findViewById<TextView>(R.id.textView_user_team)
        val textViewNin     = rowView.findViewById<TextView>(R.id.textView_user_nin)

        // Set values in the row
        textViewName.text = user.userName.toString()
        textViewSection.text = getSectionName(getTeamById(user.idTeam!!).idSection!!)
        textViewTeam.text = getTeamById(user.idTeam!!).teamName
        textViewNin.text = user.nin.toString()

        rowView.setOnClickListener {
            val intent = Intent(this@ActivityUserRequest, ProfileActivity::class.java)
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
    for (i in 0 until teams.size) {
        if (teams[i].idTeam == id)
            response = teams[i]
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