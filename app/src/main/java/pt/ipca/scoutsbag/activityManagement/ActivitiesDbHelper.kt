package pt.ipca.scoutsbag.activityManagement

import android.content.Context
import android.content.Intent
import android.widget.TextView
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
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.ActivityType
import pt.ipca.scoutsbag.models.Team

interface ActivitiesDbHelper {

    /*
        ------------------------------------------------ Activities ------------------------------------------------
     */


    /*
        This function returns all activities in the api to an list
     */
    fun getAllActivities(): MutableList<ScoutActivity> {

        val activities : MutableList<ScoutActivity> = arrayListOf()

        // Create the http request
        val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activities").build()

        // Send the request and analyze the response
        OkHttpClient().newCall(request).execute().use { response ->

            // Convert the response into string then into JsonArray
            val activityJsonArrayStr : String = response.body!!.string()
            val activityJsonArray = JSONArray(activityJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until activityJsonArray.length()) {
                val jsonArticle = activityJsonArray.get(index) as JSONObject
                val activity = ScoutActivity.fromJson(jsonArticle)
                activities.add(activity)
            }
        }

        return activities
    }


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
            val teamJsonArrayStr : String = response.body!!.string()
            val teamJsonArray = JSONArray(teamJsonArrayStr)

            // Add the elements in the list
            for (index in 0 until teamJsonArray.length()) {
                val jsonArticle = teamJsonArray.get(index) as JSONObject
                activity = ScoutActivity.fromJson(jsonArticle)
            }
        }

        return activity!!
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
     */
    fun getAllActivityTypes(): MutableList<ActivityType> {

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
        }

        return activityTypes
    }


    /*
        This function returns the activity type designation
        @id = selected activity type id
        @activitiesTypes = all activity types list
     */
    fun getActivityTypeDesignation(id: Int, activitiesTypes: MutableList<ActivityType>): String {

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

            1 -> R.drawable.icon_empty
            2 -> R.drawable.icon_empty
            3 -> R.drawable.icon_empty
            4 -> R.drawable.icon_empty
            5 -> R.drawable.icon_empty
            6 -> R.drawable.icon_empty
            else -> R.drawable.icon_empty
        }
    }


    /*
        ------------------------------------------------ Invites ------------------------------------------------
     */


    /*
        This function returns all the invites from a specific user team in the api by an list
        @idUserTeam = team id of the selected user
     */
    fun getAllTeamInvites(idUserTeam: Int): MutableList<Invite> {

        val invites : MutableList<Invite> = arrayListOf()

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
                if (invite.idTeam == idUserTeam)
                    invites.add(invite)
            }
        }

        return invites
    }


    /*
        This function add the invite in the data base
        @context = context of the activity
        @idTeam = team invited id
     */
    fun addInvite(invite: Invite, changeActivity: () -> Unit) {

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
        OkHttpClient().newCall(request).execute().use { response ->

            GlobalScope.launch (Dispatchers.Main){

                if (response.message == "OK"){
                    changeActivity()
                }
            }
        }

    }


}