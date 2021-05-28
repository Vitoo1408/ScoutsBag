package pt.ipca.scoutsbag.colonyManagement

import com.example.scoutsteste1.Invite
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User

interface ColonyDbHelper {

    /*
        ------------------------------------------------ Users ------------------------------------------------
    */


    /*
        This function returns all teams invited from a selected activity
        @idActivity = activity that the teams are invited
     */
    fun getAllUsers(): MutableList<User> {

        val users : MutableList<User> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/users").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val activityJsonArrayStr : String = response.body!!.string()
            val activityJsonArray = JSONArray(activityJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until activityJsonArray.length()) {
                val jsonArticle = activityJsonArray.get(index) as JSONObject
                val user = User.fromJson(jsonArticle)
                users.add(user)
            }
        }

        return users
    }


    /*
        This function return a user by an id
        @id = selected team id
     */
    fun getUser(id: Int): User {

        var user : User? = null

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
                user = User.fromJson(jsonArticle)
            }
        }

        return user!!
    }


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


    /*
        This adds a team into the database
        @id = selected team id
    */
    fun addTeam(team: Team, changeActitivty: ()->Unit) {



            // Prepare the from body request
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                team.toJson().toString()
            )

            // Build the request
            val request = Request.Builder()
                .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/teams")
                .post(requestBody)
                .build()

            // Send the request and verify the response
            OkHttpClient().newCall(request).execute().use { response ->

                GlobalScope.launch (Dispatchers.Main){

                    if (response.message == "OK"){
                        changeActitivty()
                    }

                }
            }


    }


}