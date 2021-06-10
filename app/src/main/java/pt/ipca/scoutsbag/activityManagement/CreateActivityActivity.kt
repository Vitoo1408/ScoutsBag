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
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.*

class CreateActivityActivity : AppCompatActivity() {

    // Global Variables
    var teams: MutableList<Team> = arrayListOf()
    var selectedTeams: MutableList<Team> = arrayListOf()
    var materials: List<Material> = arrayListOf()
    var selectedMaterials: MutableList<ActivityMaterial> = arrayListOf()
    private var activityTypesImages: MutableList<ImageView> = arrayListOf()
    private lateinit var listViewTeams: ListView
    lateinit var adapter: TeamsAdapter
    var activityId: Int? = null


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

        updateTeamButtons()

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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)
        //set actionbar title
        supportActionBar?.title = "Criar atividade"

        // Interact with the data base
        GlobalScope.launch(Dispatchers.IO) {

            // Get the new activity ID
            activityId = Backend.getLastActivityId() + 1

            // Get all materials
            Backend.getAllMaterials {
                materials = it
            }
        }

        // Pass the view objects to variables
        val dateStartTextView = findViewById<TextView>(R.id.dateStartButton)
        val dateEndTextView = findViewById<TextView>(R.id.dateEndButton)
        val addButton = findViewById<Button>(R.id.buttonAddActivity)
        val buttonMaterial = findViewById<TextView>(R.id.buttonMaterial)
        listViewTeams = findViewById(R.id.listViewTeams)
        adapter = TeamsAdapter()
        listViewTeams.adapter = adapter

        // Create the pop up window to select the date
        val dateStartPickerDialog = Utils.initDatePicker(dateStartTextView, this)
        val dateEndPickerDialog = Utils.initDatePicker(dateEndTextView, this)

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

        // On click button events
        dateStartTextView.setOnClickListener {
            dateStartPickerDialog.show()
        }

        dateEndTextView.setOnClickListener {
            dateEndPickerDialog.show()
        }

        // Add material to the activity
        buttonMaterial.setOnClickListener {
            selectedMaterials.removeAll(selectedMaterials)
            openSelectMaterialDialog()
        }

        // Add activity and invite teams button event
        addButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {

                // Build the activity that will be added
                val scoutActivity = ScoutActivity(
                    activityId,
                    findViewById<EditText>(R.id.editTextActivityName).text.toString(),
                    getSelectedActivityType(),
                    findViewById<EditText>(R.id.editTextActivityDescription).text.toString(),
                    findViewById<EditText>(R.id.editTextActivityLocalizationStart).text.toString(),
                    Utils.dateTimeToMySql(findViewById<TextView>(R.id.dateStartButton).text.toString()),
                    Utils.dateTimeToMySql(findViewById<TextView>(R.id.dateEndButton).text.toString()),
                    findViewById<EditText>(R.id.editTextActivityLocalization).text.toString(),
                    findViewById<EditText>(R.id.editTextActivityLocalizationStart).text.toString(),
                    findViewById<EditText>(R.id.editTextActivityLocalizationEnd).text.toString(),
                    findViewById<EditText>(R.id.editTextActivityPrice).text.toString().toFloat()
                )

                // Add activity
                Backend.addActivity(scoutActivity, changeActivity)

                // Invite all users selected to this activity
                for (team in selectedTeams) {

                    // Get all users from the current team
                    var teamUsers: List<User> = arrayListOf()
                    Backend.getAllTeamUsers(team.idTeam!!) {
                        teamUsers = it
                    }

                    // Create the team invite to activity
                    Backend.addActivityTeam(
                        ActivityTeam(activityId, team.idTeam)
                    )

                    // Create invites for all users in the team
                    for (user in teamUsers) {

                        // Build the invite
                        val invite = Invite(
                            activityId,
                            user.idUser,
                            null
                        )

                        // Create invite
                        Backend.addInvite(invite)
                    }
                }

                // Create the material request for the activity
                for (activityMaterial in selectedMaterials) {
                    Backend.addActivityMaterial(activityMaterial)
                }

            }

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
            if (teams[i].idSection == idSection) {

                // Find the team in selected teams and remove it
                for (j in selectedTeams.size-1 downTo 0) {
                    if (teams[i].idTeam == selectedTeams[j].idTeam)
                        selectedTeams.removeAt(j)
                }

                teams.removeAt(i)
            }
        }

        adapter.notifyDataSetChanged()
    }


    /*
        This function update all buttons in the View
        @idSection = selected section
     */
    private fun updateTeamButtons() {

        val tempList: MutableList<Team> = arrayListOf()
        tempList.addAll(teams)

        teams.removeAll(teams)
        teams.addAll(tempList)

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

            // Get current team
            val team = teams[position]
            val teamButton = rowView.findViewById<Button>(R.id.buttonTeam)

            // Set data
            teamButton.text = teams[position].teamName
            teamButton.setOnClickListener(onClickTeam)

            // See if the team is already selected
            for (i in 0 until selectedTeams.size) {
                if (team.idTeam == selectedTeams[i].idTeam) {
                    onClickTeam(teamButton)
                }
            }

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
                            if (selectedMaterials[i].idActivity == activityId && selectedMaterials[i].idMaterial == material.idMaterial) {
                                selectedMaterials.removeAt(i)
                                materialFound = true
                            }
                        }
                    }

                }
                else {

                    val activityMaterial = ActivityMaterial(
                        activityId,
                        material.idMaterial,
                        textViewQuantity.text.toString().toInt()
                    )

                    selectedMaterials.add(activityMaterial)
                }

            }

            return rowView
        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}