package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.view.*
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.*

class ActivityDetailsActivity: ScoutActivityDetailsHelper() {

    var percentage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //check internet connection
        Utils.connectionLiveData(this)

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
        val textViewStatistics = findViewById<TextView>(R.id.textViewStatistics)
        val buttonMaterial = findViewById<TextView>(R.id.buttonMaterial)

        // Set data in the views
        textViewName.text = activity.nameActivity
        textViewDescription.text = activity.activityDescription
        textViewPrice.text = activity.price.toString()
        textViewStartDate.text = startDate
        textViewEndDate.text = endDate
        textViewLocal.text = activity.activitySite
        textViewStartLocal.text = activity.startSite
        textViewEndLocal.text = activity.finishSite

        // Get invites accepted percentage

        GlobalScope.launch(Dispatchers.IO) {
            Backend.getAllActivityInvitesPercentage(activity.idActivity!!) {
                percentage = it
            }

            GlobalScope.launch(Dispatchers.Main) {
                textViewStatistics.text = "$percentage%"
            }
        }

        // View requested material list
        buttonMaterial.setOnClickListener {
            openMaterialListDialog()
        }

    }

}