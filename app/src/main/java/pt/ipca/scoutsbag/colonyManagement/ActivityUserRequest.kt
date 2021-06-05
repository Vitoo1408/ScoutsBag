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
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

class ActivityUserRequest : AppCompatActivity() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : UsersAdapter
    var users : List<User> = arrayListOf()
    var teams : List<Team> = arrayListOf()

    /*
        This function create the view
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_request)

        // Get the values to the lists
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllTeams {
                teams = it
            }

            Backend.getAllUnacceptedUsers {
                users = it
            }

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        // Set data
        listView = findViewById(R.id.listViewUserRequest)
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
            //textViewSection.text = Backend.getSectionName(Backend.getTeamById(user.idTeam!!, teams).idSection!!)
            //textViewTeam.text = Backend.getTeamById(user.idTeam!!, teams).teamName
            textViewNin.text = user.nin.toString()

            rowView.setOnClickListener {
                val intent = Intent(this@ActivityUserRequest, ActivityReplyRequest::class.java)
                intent.putExtra("user", user.toJson().toString())
                startActivity(intent)
            }

            return rowView
        }
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