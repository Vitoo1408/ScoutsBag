package pt.ipca.scoutsbag.activityManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.scoutsteste1.Invite
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.Team

class ActivityDetailsActivity : AppCompatActivity() {

    // Global variables
    private lateinit var activity: ScoutActivity
    private lateinit var textViewTeams: TextView

    private var teams: MutableList<Team> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr)
        activity = ScoutActivity.fromJson(activityJson)

        // Variables
        val startDate = Utils.mySqlDateTimeToString(activity.startDate.toString())
        val endDate = Utils.mySqlDateTimeToString(activity.finishDate.toString())

        // Variables in the activity
        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewDescription = findViewById<TextView>(R.id.textViewDescription)
        val textViewStartDate = findViewById<TextView>(R.id.textViewStartDate)
        val textViewEndDate = findViewById<TextView>(R.id.textViewEndDate)
        val textViewStartLocal = findViewById<TextView>(R.id.textViewLocalizationStart)
        val textViewEndLocal = findViewById<TextView>(R.id.textViewLocalizationEnd)
        textViewTeams = findViewById<TextView>(R.id.textViewInvitedTeams)

        // Set data in the views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite

        // Get section images
        getSectionImage(1, 1)
        getSectionImage(2, 2)

        // Get lists from db
        getInvitedTeamsList(activity.idActivity!!)

    }


    /*

     */
    private fun getSectionImage(section: Int, position: Int) {

        // Get the position of the image in the view
        val imageSlot = when (position) {
            1 -> R.id.imageViewSlot1
            2 -> R.id.imageViewSlot2
            3 -> R.id.imageViewSlot3
            else -> R.id.imageViewSlot4
        }

        // Get the image of the section
        val imageResource = when (section) {
            1 -> R.drawable.icon_lobitos
            2 -> R.drawable.icon_exploradores
            3 -> R.drawable.icon_pioneiros
            else -> R.drawable.icon_caminheiros
        }

        // Add section image in the selected position
        val imageView = findViewById<ImageView>(imageSlot)
        imageView.setImageResource(imageResource)
    }


    /*

     */
    private fun getInvitedTeamsList(id: Int) {

        // Invites list
        val invites: MutableList<Invite> = arrayListOf()

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

            // Create the http request
            val request = Request.Builder().url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activitiesInvites/$id").build()

            // Send the request and analyze the response
            OkHttpClient().newCall(request).execute().use { response ->

                // Convert the response into string then into JsonArray
                val teamJsonArrayStr : String = response.body!!.string()
                val teamJsonArray = JSONArray(teamJsonArrayStr)

                // Add the elements in the list
                for (index in 0 until teamJsonArray.length()) {
                    val jsonArticle = teamJsonArray.get(index) as JSONObject
                    val invite = Invite.fromJson(jsonArticle)
                    invites.add(invite)
                }
            }

            // Get the invited teams
            for (i in 0 until invites.size) {
                getTeam(invites[i].idTeam!!)
            }

        }

    }

    private fun getTeam(id: Int) {

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

            // Create the http request
            val request = Request.Builder().url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/teams/$id").build()

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

                    // Show team
                    GlobalScope.launch(Dispatchers.Main) {
                        textViewTeams.text = "${textViewTeams.text} ${team.teamName}, "
                    }
                }

            }

        }

    }

}