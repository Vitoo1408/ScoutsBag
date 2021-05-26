package pt.ipca.scoutsbag.activityManagement

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
import pt.ipca.scoutsbag.models.ActivityType

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
        @activitiesTypes = activity types list
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
        ------------------------------------------------ Invites ------------------------------------------------
     */


    /*
        This function returns all activities in the api to an list
     */
    fun getAllInvites(): MutableList<Invite> {

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
                invites.add(invite)
            }
        }

        return invites
    }



}