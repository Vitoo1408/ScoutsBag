package pt.ipca.scoutsbag.activityManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.ActivityType
import pt.ipca.scoutsbag.models.Team


class FragmentInvite : Fragment(), ActivitiesDbHelper {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : InvitesAdapter
    var invites : MutableList<Invite> = arrayListOf()
    var activitiesTypes : MutableList<ActivityType> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_invite, container, false)

        // Set data
        listView = rootView.findViewById(R.id.listViewInvites)
        adapter = InvitesAdapter()
        listView.adapter = adapter

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

            // Get the values to the lists
            invites = getAllTeamInvites(UserLoggedIn.idTeam!!)
            activitiesTypes = getAllActivityTypes()

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

    }


    inner class InvitesAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return invites.size
        }

        override fun getItem(position: Int): Any {
            return invites[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_invite, parent, false)

            // Coroutine start
            GlobalScope.launch(Dispatchers.IO) {

                // Get the activity from the current invite
                val activity = getActivity(invites[position].idActivity!!)

                // Set data in the row
                GlobalScope.launch(Dispatchers.Main) {

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
                    imageViewActivity.setImageResource(getActivityTypeImage(activity.idType!!))
                    textViewDay.text = Utils.getDay(activity.startDate.toString())
                    textViewMonth.text = Utils.getMonth(activity.startDate.toString())
                    textViewActivityType.text = getActivityTypeDesignation(activity.idType!!, activitiesTypes)
                    textViewName.text = activity.nameActivity.toString()
                    textViewDate.text = "Data: $dataInicio - $dataFim"
                    textViewTime.text = "Hora: $horaInicio - $horaFim"
                    textViewLocality.text = activity.activitySite.toString()

                    // Show activity details button event
                    rowView.setOnClickListener {
                        val intent = Intent(context, InviteDetailsActivity::class.java)
                        intent.putExtra("activity", activity.toJson().toString())
                        startActivity(intent)
                    }
                }
            }

            return rowView
        }
    }

}