package pt.ipca.scoutsbag.activityManagement

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*


class CreateActivityActivity : AppCompatActivity() {

    // Global Variables
    var teams: MutableList<Team> = arrayListOf()
    private lateinit var listViewTeams: ListView

    //
    var onClickSection: (view: View)->Unit = {
        val imageView = it as ImageView

        imageView.isHovered = !imageView.isHovered

        if (!imageView.isHovered) {
            removeTeamsFromList()
            imageView.setBackgroundResource(0)
        }
        else {
            getTeamsList()
            imageView.setBackgroundResource(R.drawable.border)
        }

    }

    //
    var onClickTeam: (view: View)->Unit = {
        val button = it as Button

        button.isHovered = !button.isHovered

        if (!button.isHovered) {
            button.setBackgroundResource(R.drawable.custom_button_white)
            button.setTextColor(Color.BLACK)
        }
        else {
            button.setBackgroundResource(R.drawable.custom_button_orange)
            button.setTextColor(Color.WHITE)
        }
    }

    
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)

        // Pass the view objects to variables
        val dateStartTextView = findViewById<TextView>(R.id.dateStartButton)
        val dateEndTextView = findViewById<TextView>(R.id.dateEndButton)
        val addButton = findViewById<TextView>(R.id.buttonAddActivity)
        listViewTeams = findViewById<ListView>(R.id.listViewTeams)

        // Create the pop up window to select the date
        val dateStartPickerDialog = Utils.initDatePicker(dateStartTextView, this)
        val dateEndPickerDialog = Utils.initDatePicker(dateEndTextView, this)

        // On click section events
        findViewById<ImageView>(R.id.imageViewLobitos).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewExploradores).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewPioneiros).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewCaminheiros).setOnClickListener(onClickSection)

        // On click button events
        dateStartTextView.setOnClickListener {
            dateStartPickerDialog.show()
        }

        dateEndTextView.setOnClickListener {
            dateEndPickerDialog.show()
        }

        addButton.setOnClickListener {
            addActivity(this)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listViewTeams.adapter = TeamsAdapter()


    }


    /*

     */
    private fun addActivity(context: Context) {

        // Start coroutine
        GlobalScope.launch(Dispatchers.IO) {

            // Falta saber o que fazer quando a data estiver errada
            // e algum campo vazio

            // Build the activity
            val scoutActivity = ScoutActivity(
                intent.getIntExtra("idActivity", 0),
                findViewById<TextView>(R.id.editTextActivityName).text.toString(),
                1,
                findViewById<TextView>(R.id.editTextActivityDescription).text.toString(),
                findViewById<TextView>(R.id.editTextActivityLocalizationStart).text.toString(),
                Utils.dateTimeToMySql(findViewById<TextView>(R.id.dateStartButton).text.toString()),
                Utils.dateTimeToMySql(findViewById<TextView>(R.id.dateEndButton).text.toString()),
                "1234",
                findViewById<TextView>(R.id.editTextActivityLocalizationStart).text.toString(),
                findViewById<TextView>(R.id.editTextActivityLocalizationEnd).text.toString(),
                10.5f,
            )

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

                GlobalScope.launch (Dispatchers.Main){

                    if (response.message == "OK"){
                        val returnIntent = Intent(context, MainActivity::class.java)
                        startActivity(returnIntent)
                    }

                }
            }
        }
    }


    /*

     */
    private fun getTeamsList() {

        // Reset lists
        teams.clear()

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

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

                GlobalScope.launch(Dispatchers.Main) {
                    listViewTeams.adapter = TeamsAdapter()
                }
            }
        }
    }


    /*

     */
    private fun removeTeamsFromList() {

        // Reset lists
        teams.clear()
        listViewTeams.adapter = TeamsAdapter()

    }


    inner class TeamsAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return teams.size
        }

        override fun getItem(position: Int): Any {
            return teams[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_team, parent, false)

            // Get current activity
            val teamButton = rowView.findViewById<Button>(R.id.buttonTeam)

            teamButton.text = teams[position].teamName
            teamButton.setOnClickListener(onClickTeam)

            // Refresh the listView size
            val params: ViewGroup.LayoutParams = listViewTeams.layoutParams
            params.height = 300 * (teams.size)
            listViewTeams.layoutParams = params
            listViewTeams.requestLayout()

            return rowView
        }
    }
}