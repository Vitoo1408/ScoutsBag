package pt.ipca.scoutsbag.activityManagement

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.size
import androidx.navigation.fragment.findNavController
import com.example.scoutsteste1.ScoutActivity
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
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.ActivityType


class FragmentActivity : Fragment() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : ActivitiesAdapter
    var activities : MutableList<ScoutActivity> = arrayListOf()
    var activitiesTypes : MutableList<ActivityType> = arrayListOf()
    lateinit var buttonAdd : FloatingActionButton


    /*
        This function create the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_activity, container, false)

        // Set data
        buttonAdd = rootView.findViewById(R.id.buttonAddActivity)
        listView = rootView.findViewById(R.id.listViewActivities)
        adapter = ActivitiesAdapter()
        listView.adapter = adapter

        return rootView
    }


    /*
        This function configures the fragment after its creation
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the values to the list
        activities = getActivitiesList()
        activitiesTypes = getActivityTypesList()

        // Button on click events
        buttonAdd.setOnClickListener {
            val intent = Intent(activity, CreateActivityActivity::class.java)
            intent.putExtra("idActivity", activities.size + 1)
            startActivity(intent)
        }
    }


    /*
        nao sei o que escrever aqui ainda
     */
    inner class ActivitiesAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return activities.size
        }

        override fun getItem(position: Int): Any {
            return activities[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_activity, parent, false)

            // Get current activity
            val activity = activities[position]

            // Variables
            val dataInicio = Utils.changeDateFormat(Utils.mySqlDateToString(activity.startDate.toString()))
            val dataFim = Utils.changeDateFormat(Utils.mySqlDateToString(activity.finishDate.toString()))
            val horaInicio = Utils.mySqlTimeToString(activity.startDate.toString())
            val horaFim = Utils.mySqlTimeToString(activity.finishDate.toString())

            // Variables in the row
            val textViewDay = rowView.findViewById<TextView>(R.id.textView_activity_day)
            val textViewMonth = rowView.findViewById<TextView>(R.id.textView_activity_month)
            val textViewActivityType = rowView.findViewById<TextView>(R.id.textView_activity_type)
            val textViewName = rowView.findViewById<TextView>(R.id.textView_activity_name)
            val textViewDate = rowView.findViewById<TextView>(R.id.textView_activity_date)
            val textViewTime = rowView.findViewById<TextView>(R.id.textView_activity_time)
            val textViewLocality = rowView.findViewById<TextView>(R.id.textView_activity_locality)

            // Set values in the row
            textViewDay.text = Utils.getDay(activity.startDate.toString())
            textViewMonth.text = Utils.getMonth(activity.startDate.toString())
            textViewActivityType.text = getActivityTypeById(activity.idType!!).designation
            textViewName.text = activity.nameActivity.toString()
            textViewDate.text = "Data: $dataInicio - $dataFim"
            textViewTime.text = "Hora: $horaInicio - $horaFim"
            textViewLocality.text = activity.startSite.toString()

            // Show activity details button event
            rowView.setOnClickListener {
                val action = FragmentActivityDirections.actionNavigationActivityToNavigationActivityDetails(activity.toJson().toString())
                this@FragmentActivity.findNavController().navigate(action)
            }

            return rowView
        }
    }


    /*
        This function returns all activities in the api by a list
     */
    private fun getActivitiesList(): MutableList<ScoutActivity> {

        // List that will be returned
        val activitiesList : MutableList<ScoutActivity> = arrayListOf()

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

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
                    activitiesList.add(activity)
                }

                // Update the list
                GlobalScope.launch (Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }

        return activitiesList
    }


    /*
        This function returns all activity types in the api by a list
     */
    private fun getActivityTypesList(): MutableList<ActivityType> {

        // List that will be returned
        val activityTypesList : MutableList<ActivityType> = arrayListOf()

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

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
                    activityTypesList.add(activityType)
                }

            }
        }

        return activityTypesList
    }


    /*
        This function returns the activity type designation
        @id = activity type id
     */
    private fun getActivityTypeById(id: Int): ActivityType {

        // Variables
        var response: ActivityType? = null

        // Find the activity type
        for (element in activitiesTypes) {
            if (element.idType == id)
                response = element
        }

        return response!!
    }

}
