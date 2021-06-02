package pt.ipca.scoutsbag.activityManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.Team

class InviteDetailsActivity : AppCompatActivity() {

    // Global variables
    private lateinit var activity: ScoutActivity
    private lateinit var textViewTeams: TextView
    private var teams: List<Team> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_details)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)

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
        textViewTeams = findViewById<TextView>(R.id.textViewInvitedTeams)

        // Set data in the views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite

        // Get section images
        getSectionImage(1, 1)
        getSectionImage(2, 2)

        // Coroutine start
        GlobalScope.launch(Dispatchers.IO) {

            // Get all invited teams for this activity
            Backend.getAllInvitedTeams(activity.idActivity!!) {
                teams = it
            }
        }

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