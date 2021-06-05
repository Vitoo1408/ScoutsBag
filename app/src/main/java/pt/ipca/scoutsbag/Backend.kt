package pt.ipca.scoutsbag

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
import pt.ipca.scoutsbag.models.*

object Backend {

    /*
        ------------------------------------------------ Activities ------------------------------------------------
     */


    /*
        This function return an activity by an id
        @id = selected activity id
     */
    fun getActivity(id: Int): ScoutActivity {

        var activity : ScoutActivity? = null

        // Create the http request
        val request = Request.Builder().url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activities/$id").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val jsonArrayStr : String = response.body!!.string()
            val jsonArray = JSONArray(jsonArrayStr)

            // Add the elements in the list
            for (index in 0 until jsonArray.length()) {
                val jsonArticle = jsonArray.get(index) as JSONObject
                activity = ScoutActivity.fromJson(jsonArticle)
            }
        }

        return activity!!
    }


    /*
        This function return the last id in the activity table
        @id = selected activity id
     */
    fun getLastActivityId(): Int {

        var activityCounter : Int = 0

        // Create the http request
        val request = Request.Builder().url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activities").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val jsonArrayStr : String = response.body!!.string()
            val jsonArray = JSONArray(jsonArrayStr)

            // Add the elements in the list
            for (index in 0 until jsonArray.length()) {
                activityCounter ++
            }
        }

        return activityCounter
    }


    /*
        This function add the activity to the data base
        @scoutActivity = activity that will be added
        @changeActivity = a function that return the user to the previous activity
     */
    fun addActivity(scoutActivity: ScoutActivity, changeActivity: ()->Unit) {

        // Prepare the from body request
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            scoutActivity.toJson().toString()
        )

        // Build the request
        val request = Request.Builder()
            .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activities")
            .post(requestBody)
            .build()

        // Send the request and verify the response
        OkHttpClient().newCall(request).execute().use { response ->

            GlobalScope.launch (Dispatchers.Main) {

                if (response.message == "OK") {
                    changeActivity()
                }
            }
        }

    }


    /*
        This function remove the activity from the data base
        @idActivity =
        @changeActivity = a function that return the user to the previous activity
     */
    fun removeActivity(idActivity: Int, changeActivity: ()->Unit) {

        // Build the request
        val request = Request.Builder()
            .url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activities/$idActivity")
            .delete()
            .build()

        println("!! -" + request.method + "!!- " + request.url + "!!- " + request.body + "!!- " + request.headers)

        // Send the request and verify the response
        OkHttpClient().newCall(request).execute().use { response ->

            GlobalScope.launch (Dispatchers.Main) {

                if (response.message == "OK") {
                    changeActivity()
                }
            }
        }

    }


    /*
        ------------------------------------------------ Activity Types ------------------------------------------------
     */


    /*
        This function returns all activity types in the api to an list
        @callBack = return the list
     */
    fun getAllActivityTypes(callBack: (List<ActivityType>)->Unit) {

        val activityTypes : MutableList<ActivityType> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activityTypes").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val activityTypeJsonArrayStr : String = response.body!!.string()
            val activityTypeJsonArray = JSONArray(activityTypeJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until activityTypeJsonArray.length()) {
                val jsonArticle = activityTypeJsonArray.get(index) as JSONObject
                val activityType = ActivityType.fromJson(jsonArticle)
                activityTypes.add(activityType)
            }

            // Return list
            callBack(activityTypes)
        }
    }


    /*
        This function returns the activity type designation
        @id = selected activity type id
        @activitiesTypes = all activity types list
     */
    fun getActivityTypeDesignation(id: Int, activitiesTypes: List<ActivityType>): String {

        // Variables
        var response: ActivityType? = null

        // Find the activity type
        for (element in activitiesTypes) {
            if (element.idType == id)
                response = element
        }

        return response?.designation!!
    }


    /*
        This function returns the activity type image
        @id = activity type id
     */
    fun getActivityTypeImage(id: Int): Int {

        return when (id) {

            1 -> R.drawable.ic_walk
            2 -> R.drawable.ic_camping
            3 -> R.drawable.ic_charity
            4 -> R.drawable.ic_peddy_paper
            5 -> R.drawable.ic_cantonment
            6 -> R.drawable.ic_mass
            else -> R.drawable.ic_meeting
        }
    }


    /*
        ------------------------------------------------ Participation ------------------------------------------------
     */


    /*
        This function returns all activities accepted by the user in the api to an list
        @idUser = id of the logged user
        @callBack = return the list
     */
    fun getAllAcceptedActivities(idUser: Int, callBack: (List<ScoutActivity>)->Unit) {

        val activities : MutableList<ScoutActivity> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activitiesInvites").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val jsonArrayStr : String = response.body!!.string()
            val jsonArray = JSONArray(jsonArrayStr)

            // Add the elements in the list
            for (index in 0 until jsonArray.length()) {
                val jsonArticle = jsonArray.get(index) as JSONObject
                val invite = Invite.fromJson(jsonArticle)

                // If the user participate in the activity add to the list
                if (invite.idUser == idUser && invite.acceptedInvite == 1) {
                    val activity = getActivity(invite.idActivity!!)

                    // Check if the date is not outdated
                    if (!Utils.outdatedActivity(activity)) {
                        activities.add(activity)
                    }
                }
            }

            // Return list
            callBack(activities)
        }
    }


    /*
        This function returns all the invites from a specific user team in the api by an list
        @idUserTeam = team id of the selected user
        @callBack = return the list
     */
    fun getAllUserPendingActivities(idUser: Int, callBack: (List<ScoutActivity>)->Unit) {

        val activities : MutableList<ScoutActivity> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activitiesInvites").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val activityJsonArrayStr : String = response.body!!.string()
            val activityJsonArray = JSONArray(activityJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until activityJsonArray.length()) {
                val jsonArticle = activityJsonArray.get(index) as JSONObject
                val invite = Invite.fromJson(jsonArticle)

                if (invite.idUser == idUser && invite.acceptedInvite == null) {
                    val activity = getActivity(invite.idActivity!!)

                    // Check if the date is not outdated
                    if (!Utils.outdatedActivity(activity)) {
                        activities.add(activity)
                    }
                }
            }

            // Return list
            callBack(activities)
        }
    }


    /*
        This function returns all the invites from a specific user team in the api by an list
        @idUserTeam = team id of the selected user
        @callBack = return the list
     */
    fun getAllUserPastActivities(idUser: Int, callBack: (List<ScoutActivity>)->Unit) {

        val activities : MutableList<ScoutActivity> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activitiesInvites").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val activityJsonArrayStr : String = response.body!!.string()
            val activityJsonArray = JSONArray(activityJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until activityJsonArray.length()) {
                val jsonArticle = activityJsonArray.get(index) as JSONObject
                val invite = Invite.fromJson(jsonArticle)

                if (invite.idUser == idUser && invite.acceptedInvite == 1) {
                    val activity = getActivity(invite.idActivity!!)

                    // Check if the date is outdated
                    if (Utils.outdatedActivity(activity)) {
                        activities.add(activity)
                    }
                }
            }

            // Return list
            callBack(activities)
        }
    }


    /*
        This function add the invite in the data base
        @context = context of the activity
        @idTeam = team invited id
     */
    fun addInvite(invite: Invite) {

        // Prepare the from body request
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            invite.toJson().toString()
        )

        // Build the request
        val request = Request.Builder()
            .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activitiesInvites")
            .post(requestBody)
            .build()



        // Send the request and verify the response
        OkHttpClient().newCall(request).execute().use {
        }

    }


    /*
        This function add the invite in the data base
        @context = context of the activity
        @idTeam = team invited id
     */
    fun editInvite(invite: Invite, changeActivity: ()->Unit) {

        GlobalScope.launch(Dispatchers.IO) {

            // Prepare the from body request
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                invite.toJson().toString()
            )

            // Build the request
            val request = Request.Builder()
                .url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activitiesInvites/${invite.idActivity}/${invite.idUser}")
                .put(requestBody)
                .build()

            // Send the request and verify the response
            OkHttpClient().newCall(request).execute().use { response ->

                GlobalScope.launch (Dispatchers.Main){
                    if (response.message == "OK"){
                        changeActivity()
                    }
                }
            }
        }
    }


    /*
        ------------------------------------------------ Users ------------------------------------------------
    */


    /*
        This function returns all accepted users
        @callBack = return the list
     */
    fun getAllAcceptedUsers(callBack: (List<User>)->Unit) {

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
                if(user.idTeam != null)
                    users.add(user)
            }

            // Return list
            callBack(users)
        }

    }


    /*
        This function returns all unaccepted users
        @callBack = return the list
     */
    fun getAllUnacceptedUsers(callBack: (List<User>)->Unit) {

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
                if(user.accepted == 0)
                    users.add(user)
            }

            // Return list
            callBack(users)
        }
    }


    /*
        This function returns all unaccepted users
        @callBack = return the list
     */
    fun getAllTeamUsers(idTeam: Int, callBack: (List<User>)->Unit) {

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
                if(user.idTeam == idTeam)
                    users.add(user)
            }

            // Return list
            callBack(users)
        }
    }


    /*
        This function return a user by an id
        @id = selected team id
     */
    fun getUser(id: Int): User {

        var user : User? = null

        // Create the http request
        val request = Request.Builder().url("http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/users/$id").build()

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
        This function returns the name of the section by the given id
     */
    fun getSectionName(id: Int): String{

        return when (id) {
            1 -> "Lobitos"
            2 -> "Exploradores"
            3 -> "Pioneiros"
            else -> "Caminheiros"
        }

    }

    /*
        This function returns the team by the given id
     */
    fun getTeamById(id: Int, teams: List<Team>): Team {

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
        ------------------------------------------------ Teams ------------------------------------------------
     */


    /*
        This function returns all teams in the api to an list
        @callBack = return the list
     */
    fun getAllTeams(callBack: (List<Team>)->Unit) {

        // Invites list
        val teams: MutableList<Team> = arrayListOf()

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

            // Return list
            callBack(teams)
        }
    }


    /*
        This function returns all teams invited from a selected activity
        @idActivity = activity that the teams are invited
        @callBack = return the list
     */
    fun getAllInvitedUsers(idActivity: Int, callBack: (List<User>)->Unit) {

        // Invites list
        val users: MutableList<User> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url(
            "http://${MainActivity.IP}:${MainActivity.PORT}/api/v1/activitiesInvites/${idActivity}")
            .build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val inviteJsonArrayStr : String = response.body!!.string()
            val inviteJsonArray = JSONArray(inviteJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until inviteJsonArray.length()) {
                val jsonArticle = inviteJsonArray.get(index) as JSONObject
                val invite = Invite.fromJson(jsonArticle)
                users.add(getUser(invite.idUser!!))
            }
        }

        callBack(users)
    }


    /*
        This function add all the teams of an selected section into the list
        @idSection = selected section
        @callBack = return the list
     */
    fun getAllSectionTeams(idSection: Int, callBack: (List<Team>)->Unit) {

        val teams: MutableList<Team> = arrayListOf()

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
                if (team.idSection == idSection)
                    teams.add(team)
            }
        }

        callBack(teams)
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
    fun addTeam(team: Team, changeActivity: ()->Unit) {

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
                    changeActivity()
                }
            }
        }
    }

}