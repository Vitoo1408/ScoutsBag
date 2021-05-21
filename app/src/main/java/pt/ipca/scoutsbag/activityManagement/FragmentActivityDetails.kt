package pt.ipca.scoutsbag.activityManagement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.scoutsteste1.Invite
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.Team


class FragmentActivityDetails : Fragment() {

    // Global variables
    private lateinit var activity: ScoutActivity

    private lateinit var textViewName: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var textViewStartDate: TextView
    private lateinit var textViewEndDate: TextView
    private lateinit var textViewStartLocal: TextView
    private lateinit var textViewEndLocal: TextView
    private lateinit var textViewTeams: TextView

    private var teams: MutableList<Team> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the selected activity
        arguments?.let {
            val activityJsonStr = it.getString("activity")
            val activityJson = JSONObject(activityJsonStr)
            activity = ScoutActivity.fromJson(activityJson)
        }

        // Get lists from db
        getInvitedTeamsList(activity.idActivity!!)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_activity_details, container, false)

        // Send the objects in the view to the variables
        textViewName = rootView.findViewById(R.id.textViewName)
        textViewDescription = rootView.findViewById(R.id.textViewDescription)
        textViewStartDate = rootView.findViewById(R.id.textViewStartDate)
        textViewEndDate = rootView.findViewById(R.id.textViewEndDate)
        textViewStartLocal = rootView.findViewById(R.id.textViewLocalizationStart)
        textViewEndLocal = rootView.findViewById(R.id.textViewLocalizationEnd)
        textViewTeams = rootView.findViewById(R.id.textViewInvitedTeams)

        // Get the section images and display in the rootView
        getSectionImage(1, rootView, 1)
        getSectionImage(2, rootView, 2)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Variables
        val startDate = Utils.mySqlDateTimeToString(activity.startDate.toString())
        val endDate = Utils.mySqlDateTimeToString(activity.finishDate.toString())

        // Fill the text Views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite

    }


    /*

     */
    private fun getSectionImage(section: Int, rootView: View, position: Int) {

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
        val imageView = rootView.findViewById<ImageView>(imageSlot)
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