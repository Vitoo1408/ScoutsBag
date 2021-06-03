package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.Section
import pt.ipca.scoutsbag.models.Team

class ActivityDetailsActivity : AppCompatActivity() {

    // Global variables
    private lateinit var activity: ScoutActivity
    private var teams: List<Team> = arrayListOf()
    private var sections: MutableList<Section> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)

        // Get all sections
        for (i in 0 until 4)
            sections.add(Section(i, false))

        // Variables
        val startDate = Utils.mySqlDateTimeToString(activity.startDate.toString())
        val endDate = Utils.mySqlDateTimeToString(activity.finishDate.toString())

        // Variables in the activity
        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewDescription = findViewById<TextView>(R.id.textViewDescription)
        val textViewStartDate = findViewById<TextView>(R.id.textViewStartDate)
        val textViewEndDate = findViewById<TextView>(R.id.textViewEndDate)
        val textViewStartLocal = findViewById<TextView>(R.id.textViewLocalizationStart)
        val textViewEndLocal = findViewById<TextView>(R.id.textViewLocalizationEnd)

        // Set data in the views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite

        // Get all invited teams for this activity
        GlobalScope.launch(Dispatchers.IO) {
            Backend.getAllInvitedTeams(activity.idActivity!!) {
                teams = it
            }

            // Get all invited sections
            GlobalScope.launch(Dispatchers.Main) {

                // Verify if the section is already displayed
                for (i in teams.indices) {
                    val teamSection = sections[teams[i].idSection!!-1]

                    if (!teamSection.active!!) {
                        getSectionImage(teams[i].idSection!!, i + 1)
                        teamSection.active = true
                    }
                }
            }
        }

    }


    /*
        This function create the action bar above the activity
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_edit_menu, menu)
        title = activity.nameActivity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return true
    }


    /*
        This function define the events of the action bar buttons
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId){
            R.id.itemDelete -> {
                GlobalScope.launch(Dispatchers.IO) {
                    Backend.removeActivity(activity.idActivity!!) {
                        val intent = Intent(this@ActivityDetailsActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                return true
            }
            R.id.itemEdit -> {
                val intent = Intent(this, EditActivityActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return false
    }


    /*
        Enable the images of the selected sections
        @section = selected section
        @position = slot in the view
     */
    private fun getSectionImage(section: Int, position: Int) {

        // Get the position of the image in the view
        val imageSlot = when (position) {
            1 -> R.id.imageViewSlot1
            2 -> R.id.imageViewSlot2
            3 -> R.id.imageViewSlot3
            else -> R.id.imageViewSlot4
        }

        // Get the image of the section
        val imageResource = when (section) {
            1 -> R.drawable.icon_lobitos
            2 -> R.drawable.icon_exploradores
            3 -> R.drawable.icon_pioneiros
            else -> R.drawable.icon_caminheiros
        }

        // Add section image in the selected position
        val imageView = findViewById<ImageView>(imageSlot)
        imageView.setImageResource(imageResource)
    }


}