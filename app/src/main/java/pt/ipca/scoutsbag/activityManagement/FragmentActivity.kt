package pt.ipca.scoutsbag.activityManagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.scoutsteste1.Activity
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
    var activities : MutableList<Activity> = arrayListOf()
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

        buttonAdd.setOnClickListener() {
            val intent = Intent(activity, CreateActivityActivity::class.java)
            startActivity(intent)
        }

        // Start Corroutine
        GlobalScope.launch(Dispatchers.IO) {

            // OkHttp possibilita o envio de pedidos http e a leitura das respostas
            val client = OkHttpClient()

            // criação do pedido http á api do .NET
            val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activities").build()

            // fazer a chamada com o pedido http e analisar a resposta
            client.newCall(request).execute().use { response ->

                // resposta do pedido http retornada em string
                val activityJsonArrayStr : String = response.body!!.string()

                // converter a str em um array de json, e esse json é de cavalos
                val activityJsonArray = JSONArray(activityJsonArrayStr)

                // add the horses to the list
                for (index in 0 until activityJsonArray.length()) {
                    val jsonArticle = activityJsonArray.get(index) as JSONObject
                    val activity = Activity.fromJson(jsonArticle)
                    activities.add(activity)
                }

                // Refresh the list adapter
                GlobalScope.launch (Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
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
            textViewActivityType.text = activity.idType.toString()
            textViewName.text = activity.nameActivity.toString()
            textViewDate.text = "Data: $dataInicio - $dataFim"
            textViewTime.text = "Hora: $horaInicio - $horaFim"
            textViewLocality.text = activity.startSite.toString()
            return rowView
        }
    }


    fun getActivityType(id: Int): ActivityType {

        var activityType: ActivityType? = null

        GlobalScope.launch(Dispatchers.IO) {

            // Variables
            val client = OkHttpClient()
            val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activityTypes").build()
            var activityTypesList : MutableList<ActivityType> = arrayListOf()

            client.newCall(request).execute().use { response ->

                val activityTypeJsonArrayStr : String = response.body!!.string()
                val activityTypeJsonArray = JSONArray(activityTypeJsonArrayStr)

                for (index in 0 until activityTypeJsonArray.length()) {
                    val jsonArticle = activityTypeJsonArray.get(index) as JSONObject
                    var activityType = ActivityType.fromJson(jsonArticle)

                    activityTypesList.add(activityType!!)
                }

                // Find the activity corret
            }
        }

        return activityType!!

    }

}
