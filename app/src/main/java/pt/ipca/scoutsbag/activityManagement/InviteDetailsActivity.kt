package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
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
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.*
import java.util.ArrayList

class InviteDetailsActivity : AppCompatActivity() {

    // Global variables
    private lateinit var activity: ScoutActivity
    private var users: List<User> = arrayListOf()

    // This function is for return to the previous activity after a operation
    var changeActivity: ()->Unit = {
        val returnIntent = Intent(this, MainActivity::class.java)
        startActivity(returnIntent)
    }

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
        val textViewPrice = findViewById<TextView>(R.id.textViewActivityPrice)
        val textViewStartDate = findViewById<TextView>(R.id.textViewStartDate)
        val textViewEndDate = findViewById<TextView>(R.id.textViewEndDate)
        val textViewLocal = findViewById<TextView>(R.id.textViewActivityLocalization)
        val textViewStartLocal = findViewById<TextView>(R.id.textViewLocalizationStart)
        val textViewEndLocal = findViewById<TextView>(R.id.textViewLocalizationEnd)
        val buttonRefuse = findViewById<TextView>(R.id.button_refuse)
        val buttonConfirm = findViewById<TextView>(R.id.button_confirm)

        // Set data in the views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite
        textViewPrice.text = activity.price.toString()
        textViewLocal.text = activity.activitySite

        // Get all invited teams for this activity
        GlobalScope.launch(Dispatchers.IO) {
            Backend.getAllInvitedUsers(activity.idActivity!!) {
                users = it
            }

            // Get all sections
            val sections: MutableList<Section> = arrayListOf()
            for (i in 0 until 4)
                sections.add(Section(i, false))

            // Verify if the section is already displayed
            var position = 1
            for (i in users.indices) {
                if (users[i].idTeam != null) {

                    // Get the user section
                    val team = Backend.getTeam(users[i].idTeam!!)
                    val teamSection = sections[team.idSection!!-1]

                    // Display the image
                    if (!teamSection.active!!) {
                        teamSection.active = true
                        GlobalScope.launch(Dispatchers.Main) {
                            getSectionImage(teamSection.idSection!!, position)
                            position++
                        }
                    }

                }
            }
        }

        // Get the user participation for this invite
        val invite = Invite(
            activity.idActivity,
            UserLoggedIn.idUser,
            null
        )

        // Accept ou refuse the invite
        buttonRefuse.setOnClickListener {
            invite.acceptedInvite = 0
            Backend.editInvite(invite, changeActivity)
        }

        buttonConfirm.setOnClickListener {
            invite.acceptedInvite = 1
            Backend.editInvite(invite, changeActivity)
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