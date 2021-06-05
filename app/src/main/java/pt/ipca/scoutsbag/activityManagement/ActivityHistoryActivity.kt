package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.ActivityType

class ActivityHistoryActivity : AppCompatActivity() {

    // Global Variables
    var activities: List<ScoutActivity> = arrayListOf()
    var activitiesTypes: List<ActivityType> = arrayListOf()
    lateinit var adapter : ActivitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Set listView Adapter
        val listView = findViewById<ListView>(R.id.listViewActivities)
        adapter = ActivitiesAdapter()
        listView.adapter = adapter

        // Get the values to the lists
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllUserPastActivities(UserLoggedIn.idUser!!) {
                activities = it
            }
            Backend.getAllActivityTypes {
                activitiesTypes = it
            }

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

    }


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
            val imageViewActivity = rowView.findViewById<ImageView>(R.id.imageView_activity)
            val textViewDay = rowView.findViewById<TextView>(R.id.textView_activity_day)
            val textViewMonth = rowView.findViewById<TextView>(R.id.textView_activity_month)
            val textViewActivityType = rowView.findViewById<TextView>(R.id.textView_activity_type)
            val textViewName = rowView.findViewById<TextView>(R.id.textView_activity_name)
            val textViewDate = rowView.findViewById<TextView>(R.id.textView_activity_date)
            val textViewTime = rowView.findViewById<TextView>(R.id.textView_activity_time)
            val textViewLocality = rowView.findViewById<TextView>(R.id.textView_activity_locality)

            // Set values in the row
            imageViewActivity.setImageResource(Backend.getActivityTypeImage(activity.idType!!))
            textViewDay.text = Utils.getDay(activity.startDate.toString())
            textViewMonth.text = Utils.getMonthFormat(Utils.getMonth(activity.startDate.toString()).toInt())
            textViewActivityType.text = Backend.getActivityTypeDesignation(activity.idType!!, activitiesTypes)
            textViewName.text = activity.nameActivity.toString()
            textViewDate.text = "Data: $dataInicio - $dataFim"
            textViewTime.text = "Hora: $horaInicio - $horaFim"
            textViewLocality.text = activity.activitySite.toString()

            // Show activity details button event
            rowView.setOnClickListener {
                val intent = Intent(this@ActivityHistoryActivity, ActivityDetailsActivity::class.java)
                intent.putExtra("activity", activity.toJson().toString())
                startActivity(intent)
            }

            return rowView
        }
    }
}