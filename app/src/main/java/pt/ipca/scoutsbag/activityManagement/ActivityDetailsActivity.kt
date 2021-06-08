package pt.ipca.scoutsbag.activityManagement

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
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

class ActivityDetailsActivity : AppCompatActivity() {

    // Global variables
    private lateinit var activity: ScoutActivity
    var materials: List<Material> = arrayListOf()
    private var users: List<User> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)

        // Variables
        val startDate = Utils.mySqlDateTimeToString(activity.startDate!!)
        val endDate = Utils.mySqlDateTimeToString(activity.finishDate!!)

        // Variables in the activity
        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewDescription = findViewById<TextView>(R.id.textViewDescription)
        val textViewPrice = findViewById<TextView>(R.id.textViewActivityPrice)
        val textViewStartDate = findViewById<TextView>(R.id.textViewStartDate)
        val textViewEndDate = findViewById<TextView>(R.id.textViewEndDate)
        val textViewLocal = findViewById<TextView>(R.id.textViewActivityLocalization)
        val textViewStartLocal = findViewById<TextView>(R.id.textViewLocalizationStart)
        val textViewEndLocal = findViewById<TextView>(R.id.textViewLocalizationEnd)
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

        GlobalScope.launch(Dispatchers.IO) {

            // Get all materials requested for this activity
            Backend.getAllActivityMaterial(activity.idActivity!!) {
                materials = it
            }

            // Get all invited teams for this activity
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

        // View requested material list
        buttonMaterial.setOnClickListener {
            openMaterialListDialog()
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

        //hide delete and edit icon from activity details
        if(UserLoggedIn.codType == "Esc"){
            return false
        }
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
                intent.putExtra("activity", activity.toJson().toString())
                startActivity(intent)
                return true
            }
        }

        return false
    }


    /*
        This function display a dialog window with a list with
        material that can be selected to this activity
     */
    private fun openMaterialListDialog() {

        // Variables
        val alertDialog = AlertDialog.Builder(this)
        val row = layoutInflater.inflate(R.layout.dialog_material_selected, null)
        val listView = row.findViewById<ListView>(R.id.listViewMaterials)
        val mAdapter = MaterialsAdapter()

        // Set data
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        // Create dialog
        alertDialog.setAdapter(mAdapter) { _, _ -> }
        alertDialog.setView(row)
        alertDialog.create().show()
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

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    inner class MaterialsAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return materials.size
        }

        override fun getItem(position: Int): Any {
            return materials[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_activity_material, parent, false)

            // Variables
            val material = materials[position]
            val textViewName     = rowView.findViewById<TextView>(R.id.textViewMaterialName)
            val textViewQuantity = rowView.findViewById<TextView>(R.id.textViewMaterialQuantity)

            // Set data
            textViewName.text = material.nameMaterial
            textViewQuantity.text = "Quantidade: " + material.qntStock.toString()

            return rowView
        }
    }
}