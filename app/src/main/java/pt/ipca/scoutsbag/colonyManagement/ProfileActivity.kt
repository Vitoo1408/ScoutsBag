package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.Invite
import pt.ipca.scoutsbag.models.User
import java.util.*
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    lateinit var user: User
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //check internet connection
        Utils.connectionLiveData(this)

        // Get the user from the last activity
        val userJsonStr = intent.getStringExtra("user")!!
        user = User.fromJson(JSONObject(userJsonStr))
        val userTeam = intent.getStringExtra("team")
        val userSection = intent.getStringExtra("section")

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = user.userName
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        // Variables
        val textName = findViewById<TextView>(R.id.scoutName)
        val textSection = findViewById<TextView>(R.id.scoutSection)
        val textNin = findViewById<TextView>(R.id.TextViewNin)
        val textTeam = findViewById<TextView>(R.id.scoutTeam)
        val textPhone = findViewById<TextView>(R.id.textPhone)
        val textMail = findViewById<TextView>(R.id.textMail)
        val textBirthDate = findViewById<TextView>(R.id.textBirthDate)
        val textAddress = findViewById<TextView>(R.id.textAddress)
        val textPostalCode = findViewById<TextView>(R.id.textPostalCode)
        val butEdit = findViewById<FloatingActionButton>(R.id.editProfile)
        val profileImage = findViewById<ImageView>(R.id.imageProfile)

        val textPercentageSinceBeginning = findViewById<TextView>(R.id.textTitleStatisticForever)
        val textPercentageMonth = findViewById<TextView>(R.id.textTitleStatisticMonth)
        val textPercentageYear = findViewById<TextView>(R.id.textTitleStatisticYear)

        // Set data
        //load profile image
        if(user.imageUrl != "" && user.imageUrl != "null") {
            Picasso.with(this).load(user.imageUrl).into(profileImage)
        }
        textName.text = user.userName
        if(userSection != "") {
            textSection.text = userSection
        }
        if(userTeam != "") {
            textTeam.text = userTeam
        }

        textPhone.text = user.contact
        textMail.text = user.email
        textBirthDate.text = Utils.mySqlDateToString(user.birthDate!!)
        textAddress.text = user.address
        textPostalCode.text = user.postalCode
        textNin.text = user.nin

        GlobalScope.launch(Dispatchers.IO) {
            val beginningPercentageValue = getActivityParticipationPercentageSinceBeginning()
            val monthPercentageValue = getActivityParticipationPercentageMonth()
            val yearPercentageValue = getActivityParticipationPercentageYear()

            GlobalScope.launch(Dispatchers.Main) {
                textPercentageSinceBeginning.text = "Desde Sempre: ${beginningPercentageValue.roundToInt()}%"
                textPercentageMonth.text = "Ultimo Mês: ${monthPercentageValue.roundToInt()}%"
                textPercentageYear.text =  "Ultimo Ano: ${yearPercentageValue.roundToInt()}%"
            }
        }

        butEdit.setOnClickListener() {
            val intent = Intent(this, EditScoutProfileActivity::class.java)
            intent.putExtra("user", userJsonStr)
            startActivity(intent)
        }

    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun getActivityParticipationPercentageSinceBeginning(): Float {

        // Variables
        var totalInvites = 0f
        var acceptedInvites = 0f

        // Get all user invites
        var invites : List<Invite> = arrayListOf()
        Backend.getAllUserInvites(user.idUser!!) {
            invites = it

            // Add to the total counter if the user have accepted or rejected
            for (i in it) {
                if (i.acceptedInvite != null) {
                    totalInvites++
                }
                // Add to the total counter if the invite is pending but is outdated
                else {
                    val activity = Backend.getActivity(i.idActivity!!)
                    if (i.acceptedInvite == null && Utils.outdatedActivity(activity))
                        totalInvites++
                }
            }
        }

        // Get the accepted amount
        for (i in invites) {
            if (i.acceptedInvite == 1)
                acceptedInvites++
        }

        // Return the percentage
        val result = (acceptedInvites / totalInvites) * 100
        return if (result.isNaN()) 0f else result
    }


    private fun getActivityParticipationPercentageMonth(): Float {

        // Variables
        var totalInvites = 0f
        var acceptedInvites = 0f

        // Get all user last month invites
        val invites : MutableList<Invite> = arrayListOf()
        Backend.getAllUserInvites(user.idUser!!) {

            // Verify if the invite is from the last month
            for (i in it) {
                val activity = Backend.getActivity(i.idActivity!!)
                if (dateLatestThanLastMonth(activity.startDate!!))
                    invites.add(i)
            }
        }

        // Add to the total counter if the user have accepted or rejected
        for (i in invites) {
            if (i.acceptedInvite != null) {
                totalInvites++
            }
            // Add to the total counter if the invite is pending but is outdated
            else {
                val activity = Backend.getActivity(i.idActivity!!)
                if (i.acceptedInvite == null && Utils.outdatedActivity(activity))
                    totalInvites++
            }
        }

        // Get the accepted amount
        for (i in invites) {
            if (i.acceptedInvite == 1)
                acceptedInvites++
        }

        // Return the percentage
        val result = (acceptedInvites / totalInvites) * 100
        return if (result.isNaN()) 0f else result
    }


    private fun getActivityParticipationPercentageYear(): Float {

        // Variables
        var totalInvites = 0f
        var acceptedInvites = 0f

        // Get all user last month invites
        val invites : MutableList<Invite> = arrayListOf()
        Backend.getAllUserInvites(user.idUser!!) {

            // Verify if the invite is from the last month
            for (i in it) {
                val activity = Backend.getActivity(i.idActivity!!)
                if (dateLatestThanLastYear(activity.startDate!!))
                    invites.add(i)
            }
        }

        // Add to the total counter if the user have accepted or rejected
        for (i in invites) {
            if (i.acceptedInvite != null) {
                totalInvites++
            }
            // Add to the total counter if the invite is pending but is outdated
            else {
                val activity = Backend.getActivity(i.idActivity!!)
                if (i.acceptedInvite == null && Utils.outdatedActivity(activity))
                    totalInvites++
            }
        }

        // Get the accepted amount
        for (i in invites) {
            if (i.acceptedInvite == 1)
                acceptedInvites++
        }

        // Return the percentage
        val result = (acceptedInvites / totalInvites) * 100
        return if (result.isNaN()) 0f else result
    }


    /*
        This function compares a date with last month
     */
    private fun dateLatestThanLastMonth(date: String): Boolean {

        // Variables
        val c: Calendar = Calendar.getInstance()

        // Activity Date
        val dMonth = Utils.getMonth(date).toInt()
        val dYear  = Utils.getYear(date).toInt()

        // Current Date
        val cMonth = c.get(Calendar.MONTH) + 1
        val cYear  = c.get(Calendar.YEAR)

        // Check if it is outdated
        return if (dMonth < cMonth) {
            true
        } else dMonth == cMonth && dYear <= cYear
    }


    /*
        This function compares a date with last year
     */
    private fun dateLatestThanLastYear(date: String): Boolean {

        // Variables
        val c: Calendar = Calendar.getInstance()

        // Activity Date
        val dYear  = Utils.getYear(date).toInt()

        // Current Date
        val cYear  = c.get(Calendar.YEAR)

        // Check if it is outdated
        return dYear <= cYear
    }
}