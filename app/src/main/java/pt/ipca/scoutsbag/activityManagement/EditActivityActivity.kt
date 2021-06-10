package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.ActivityMaterial
import pt.ipca.scoutsbag.models.Material
import pt.ipca.scoutsbag.models.Team

class EditActivityActivity : AppCompatActivity() {

    // Global Variables
    private lateinit var activity: ScoutActivity
    var teams: MutableList<Team> = arrayListOf()
    var selectedTeams: MutableList<Team> = arrayListOf()
    var materials: List<Material> = arrayListOf()
    var selectedMaterials: MutableList<ActivityMaterial> = arrayListOf()
    private var activityTypesImages: MutableList<ImageView> = arrayListOf()
    private lateinit var listViewTeams: ListView
    lateinit var adapter: TeamsAdapter

    // This function is for select an section by clicking on the section image
    private var onClickSection: (view: View)->Unit = {
        // Variables
        val imageView = it as ImageView
        val sectionId: Int = getSectionID(imageView.id)
        var buttonSpacing = 600

        imageView.isHovered = !imageView.isHovered

        // Remove border and teams
        if (!imageView.isHovered) {
            removeSectionTeams(sectionId)
            imageView.setBackgroundResource(0)
            buttonSpacing *= -1
        }
        // Add border and teams
        else {

            // Add all teams of the selected section
            GlobalScope.launch(Dispatchers.IO) {

                Backend.getAllSectionTeams(sectionId) { list ->
                    teams.addAll(list)
                }

                // Refresh the list
                GlobalScope.launch(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }

            imageView.setBackgroundResource(R.drawable.border)
        }

        // Refresh the listView size
        val params: ViewGroup.LayoutParams = listViewTeams.layoutParams
        params.height = params.height + buttonSpacing
        listViewTeams.layoutParams = params
        listViewTeams.requestLayout()
    }


    // This function is for select an team by clicking on the team button
    var onClickTeam: (view: View)->Unit = {
        val button = it as Button
        val team = findTeamByItsButton(button)

        button.isHovered = !button.isHovered

        // Set button as white
        if (!button.isHovered) {
            button.setBackgroundResource(R.drawable.custom_button_white)
            button.setTextColor(Color.BLACK)
            selectedTeams.remove(team)
        }
        // Set button as orange
        else {
            button.setBackgroundResource(R.drawable.custom_button_orange)
            button.setTextColor(Color.WHITE)
            selectedTeams.add(team)
        }
    }


    // This function is for select an team by clicking on the team button
    var onClickActivityType: (view: View)->Unit = {
        val imageView = it as ImageView

        for (i in 0 until activityTypesImages.size){
            activityTypesImages[i].setBackgroundResource(0)
            activityTypesImages[i].isHovered = false
        }

        imageView.isHovered = true
        imageView.setBackgroundResource(R.drawable.border)
    }


    // This function is for return to the previous activity after a operation
    var changeActivity: ()->Unit = {
        val returnIntent = Intent(this, MainActivity::class.java)
        startActivity(returnIntent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)

        // Variables
        listViewTeams = findViewById(R.id.listViewTeams)
        adapter = TeamsAdapter()
        listViewTeams.adapter = adapter

        // Variables in the view
        val editViewActivityName = findViewById<TextView>(R.id.editTextActivityName)
        val editViewActivityDescription = findViewById<TextView>(R.id.editTextActivityDescription)
        val dateStartButton = findViewById<TextView>(R.id.dateStartButton)
        val dateEndButton = findViewById<TextView>(R.id.dateEndButton)
        val editTextActivityPrice = findViewById<TextView>(R.id.editTextActivityPrice)
        val editTextActivityLocalization = findViewById<TextView>(R.id.editTextActivityLocalization)
        val editTextActivityLocalizationStart = findViewById<TextView>(R.id.editTextActivityLocalizationStart)
        val editTextActivityLocalizationEnd = findViewById<TextView>(R.id.editTextActivityLocalizationEnd)
        val buttonEdit = findViewById<TextView>(R.id.buttonAddActivity)
        val buttonMaterial = findViewById<TextView>(R.id.buttonMaterial)

        // Create the pop up window to select the date
        val dateStartPickerDialog = Utils.initDatePicker(dateStartButton, this)
        val dateEndPickerDialog = Utils.initDatePicker(dateEndButton, this)

        // Set values in the view
        editViewActivityName.text = activity.nameActivity
        editViewActivityDescription.text = activity.activityDescription
        dateStartButton.text = Utils.mySqlDateTimeToString(activity.startDate!!)
        dateEndButton.text = Utils.mySqlDateTimeToString(activity.finishDate!!)
        editTextActivityPrice.text = activity.price.toString()
        editTextActivityLocalization.text = activity.activitySite
        editTextActivityLocalizationStart.text = activity.startSite
        editTextActivityLocalizationEnd.text = activity.finishSite
        buttonEdit.text = "Editar Atividade"

        // Get all materials from this activity from data base
        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllMaterials {
                materials = it
            }

            Backend.getAllActivityMaterial(activity.idActivity!!) {

                for (i in it.indices) {
                    val activityMaterial = ActivityMaterial(
                        activity.idActivity,
                        it[0].idMaterial,
                        it[0].qntStock
                    )
                    selectedMaterials.add(activityMaterial)
                }

                GlobalScope.launch(Dispatchers.Main) {
                    buttonMaterial.text = "Itens selecionados: ${selectedMaterials.size}"
                }
            }
        }

        // Select the sections already selected
        /*
        ------------------------------------------------------------ AINDA POR FAZER AQUI -------------
        ------------------------------------------------- E MESMO PRA FAZER?
         */

        // On click section events
        findViewById<ImageView>(R.id.imageViewLobitos).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewExploradores).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewPioneiros).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewCaminheiros).setOnClickListener(onClickSection)

        // Add activity type images to the list
        activityTypesImages.add(findViewById(R.id.imageViewActivityType1))
        activityTypesImages.add(findViewById(R.id.imageViewActivityType2))
        activityTypesImages.add(findViewById(R.id.imageViewActivityType3))
        activityTypesImages.add(findViewById(R.id.imageViewActivityType4))
        activityTypesImages.add(findViewById(R.id.imageViewActivityType5))
        activityTypesImages.add(findViewById(R.id.imageViewActivityType6))
        activityTypesImages.add(findViewById(R.id.imageViewActivityType7))

        // On click activity type
        for (image in activityTypesImages)
            image.setOnClickListener(onClickActivityType)

        // Select the activity type already selected
        onClickActivityType(activityTypesImages[activity.idType!!-1])

        // On click button events
        dateStartButton.setOnClickListener {
            dateStartPickerDialog.show()
        }

        dateEndButton.setOnClickListener {
            dateEndPickerDialog.show()
        }

        // Add material to the activity
        buttonMaterial.setOnClickListener {
            selectedMaterials.removeAll(selectedMaterials)
            openSelectMaterialDialog()
        }

        buttonEdit.setOnClickListener {
            // FAZER O PUT
        }

    }


    /*
        This function display a dialog window with a list with
        material that can be selected to this activity
     */
    private fun openSelectMaterialDialog() {

        // Variables
        val alertDialog = AlertDialog.Builder(this)
        val row = layoutInflater.inflate(R.layout.dialog_material_selected, null)
        val listView = row.findViewById<ListView>(R.id.listViewMaterials)
        val mAdapter = MaterialsAdapter()

        // Set data
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        alertDialog.setOnCancelListener {
            findViewById<TextView>(R.id.buttonMaterial).text = "Itens selecionados: ${selectedMaterials.size}"
        }

        // Create dialog
        alertDialog.setAdapter(mAdapter) { _, _ -> }
        alertDialog.setView(row)
        alertDialog.create().show()
    }


    /*
        This function all the teams of an selected section from the list
        @idSection = selected section
     */
    private fun removeSectionTeams(idSection: Int) {

        // Find the teams of the selected section
        for (i in teams.size-1 downTo 0) {
            if (teams[i].idSection == idSection)
                teams.removeAt(i)
        }

        adapter.notifyDataSetChanged()
    }


    /*
        This function return the section id on the data base depending on the imageView id
        @imageViewId = selected imageView id
     */
    private fun getSectionID(imageViewId: Int): Int {

        return when(imageViewId) {
            R.id.imageViewLobitos -> 1
            R.id.imageViewExploradores -> 2
            R.id.imageViewPioneiros -> 3
            else -> 4
        }

    }


    /*
        This function return the Activity Type Id By looking which image is selected
     */
    private fun getSelectedActivityType(): Int {

        var activityTypeId: Int = 0

        for (i in 0 until activityTypesImages.size){
            if(activityTypesImages[i].isHovered)
                activityTypeId = i
        }

        return activityTypeId + 1
    }


    /*
        This function return a team depending on the selected team button
        @button = Selected team button
     */
    private fun findTeamByItsButton(button: Button): Team {

        var team: Team? = null

        for (i in 0 until teams.size) {
            if (teams[i].teamName == button.text)
                team = teams[i]
        }

        return team!!
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

            return rowView
        }
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
            val rowView = layoutInflater.inflate(R.layout.row_selected_material, parent, false)

            // Variables
            val material = materials[position]
            val textViewName     = rowView.findViewById<TextView>(R.id.textViewMaterialName)
            val textViewQuantity = rowView.findViewById<TextView>(R.id.editViewMaterialQuantity)
            val checkBoxMaterial = rowView.findViewById<CheckBox>(R.id.checkBoxMaterial)

            // Set data
            textViewName.text = material.nameMaterial
            textViewQuantity.text = material.qntStock.toString()

            checkBoxMaterial.setOnClickListener {

                if (!checkBoxMaterial.isChecked) {

                    var materialFound = false
                    for (i in 0 until selectedMaterials.size) {
                        if (!materialFound) {
                            if (selectedMaterials[i].idActivity == activity.idActivity && selectedMaterials[i].idMaterial == material.idMaterial) {
                                selectedMaterials.removeAt(i)
                                materialFound = true
                            }
                        }
                    }

                }
                else {

                    val activityMaterial = ActivityMaterial(
                        activity.idActivity,
                        material.idMaterial,
                        textViewQuantity.text.toString().toInt()
                    )

                    selectedMaterials.add(activityMaterial)
                }

            }

            return rowView
        }
    }
}