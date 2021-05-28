package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.scoutsteste1.ScoutActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.ActivityType


class FragmentActivity : Fragment(), ActivitiesDbHelper {

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

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

            // Get the values to the lists
            activities = getAllActivities()
            activitiesTypes = getAllActivityTypes()

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        // Button on click events
        buttonAdd.setOnClickListener {
            val intent = Intent(activity, CreateActivityActivity::class.java)
            intent.putExtra("idActivity", activities.size + 1)
            startActivity(intent)
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
            textViewActivityType.text = getActivityTypeDesignation(activity.idType!!, activitiesTypes)
            textViewName.text = activity.nameActivity.toString()
            textViewDate.text = "Data: $dataInicio - $dataFim"
            textViewTime.text = "Hora: $horaInicio - $horaFim"
            textViewLocality.text = activity.startSite.toString()

            // Show activity details button event
            rowView.setOnClickListener {

                val intent = Intent(context, ActivityDetailsActivity::class.java)
                intent.putExtra("activity", activity.toJson().toString())
                startActivity(intent)

            }

            return rowView
        }
    }

}