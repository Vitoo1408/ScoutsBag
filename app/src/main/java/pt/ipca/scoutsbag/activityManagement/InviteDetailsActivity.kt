package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.scoutsteste1.ScoutActivity
import com.squareup.picasso.Picasso
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

class InviteDetailsActivity : ScoutActivityDetailsHelper() {


    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_details)

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
        val buttonMaterial = findViewById<TextView>(R.id.buttonMaterial)

        // Set data in the views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite
        textViewPrice.text = activity.price.toString()
        textViewLocal.text = activity.activitySite

        // View requested material list
        buttonMaterial.setOnClickListener {
            openMaterialListDialog()
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

}