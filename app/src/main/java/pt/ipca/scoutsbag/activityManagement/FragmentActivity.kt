package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.scoutsteste1.ScoutActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.ActivityType
import java.util.*


class FragmentActivity : Fragment() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : ActivitiesAdapter
    var activities : MutableList<ScoutActivity> = arrayListOf()
    var activitiesTypes : List<ActivityType> = arrayListOf()
    lateinit var buttonAdd : FloatingActionButton
    lateinit var textViewWelcome : TextView

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

        var emptyElement = rootView.findViewById<TextView>(R.id.emptyElementActivity)

        // Set data
        buttonAdd = rootView.findViewById(R.id.buttonAddActivity)
        textViewWelcome = rootView.findViewById(R.id.TextViewWelcome)
        listView = rootView.findViewById(R.id.listViewActivities)
        adapter = ActivitiesAdapter()
        listView.adapter = adapter
        listView.setEmptyView(emptyElement)




        //hide add button if a scout logs in
        if(UserLoggedIn.codType == "Esc"){
            rootView.findViewById<FloatingActionButton>(R.id.buttonAddActivity).visibility = View.GONE
        }

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
            Log.d("UserLoggedinId", UserLoggedIn.idUser.toString())
            Backend.getAllAcceptedActivities(UserLoggedIn.idUser!!) {
                activities.addAll(it)
                sortActivities()
            }
            Backend.getAllActivityTypes {
                activitiesTypes = it
            }

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        // Button on click events
        buttonAdd.setOnClickListener {
            val intent = Intent(activity, CreateActivityActivity::class.java)
            startActivity(intent)
        }

        // Welcome Text
        textViewWelcome.text = "${textViewWelcome.text} ${UserLoggedIn.userName}"

    }


    /*
        This is responsible for sort activities by the date and show the latest first
    */
    private fun sortActivities() {

        for (i in 0 until activities.size) {

            for (j in 0 until activities.size - 1) {

                if (firstDateLatestThanSecond(activities[j].startDate!!, activities[j+1].startDate!!)) {

                    val temp = activities[j]
                    activities[j] = activities[j + 1]
                    activities[j + 1] = temp
                }
            }
        }
    }


    /*
        This function compares two dates and return the latest
     */
    private fun firstDateLatestThanSecond(date1: String, date2: String): Boolean {

        // Variables
        val c: Calendar = Calendar.getInstance()

        // Activity Date
        val d1Day   = Utils.getDay(date1).toInt()
        val d1Month = Utils.getMonth(date1).toInt()
        val d1Year  = Utils.getYear(date1).toInt()

        // Current Date
        val d2Day   = Utils.getDay(date2).toInt()
        val d2Month = Utils.getMonth(date2).toInt()
        val d2Year  = Utils.getYear(date2).toInt()

        // Check if it is outdated
        return if (d1Year > d2Year) {
            true
        } else if (d1Year == d2Year && d1Month > d2Month) {
            true
        } else (d1Year == d2Year && d1Month == d2Month && d1Day > d2Day)
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
                val intent = Intent(context, ActivityDetailsActivity::class.java)
                intent.putExtra("activity", activity.toJson().toString())
                startActivity(intent)
            }

            return rowView
        }
    }

}