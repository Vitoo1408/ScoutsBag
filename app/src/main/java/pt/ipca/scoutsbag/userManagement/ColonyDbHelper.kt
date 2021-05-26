package pt.ipca.scoutsbag.userManagement

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
import pt.ipca.scoutsbag.models.Team

interface ColonyDbHelper {

    /*
        ------------------------------------------------ Teams ------------------------------------------------
     */


    /*
        This function returns all teams invited from a selected activity
        @idActivity = activity that the teams are invited
     */
    fun getAllInvitedTeams(idActivity: Int): MutableList<Team> {

        // Invites list
        val invites: MutableList<Invite> = arrayListOf()
        val teams: MutableList<Team> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url(
            "http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activitiesInvites/${idActivity}")
            .build()

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
            teams.add(getTeam(invites[i].idTeam!!))
            // criar la uma lista  a beira de invites e retornar a lista de teams.
        }

        return teams
    }


    /*
        This function return a team by an id
        @id = selected team id
     */
    fun getTeam(id: Int): Team {

        var team : Team? = null

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
                team = Team.fromJson(jsonArticle)
            }
        }

        return team!!
    }


}