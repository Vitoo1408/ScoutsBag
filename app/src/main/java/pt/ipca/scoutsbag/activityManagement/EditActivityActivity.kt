package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.widget.*
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.ActivityMaterial
import pt.ipca.scoutsbag.models.Section

class EditActivityActivity : ScoutActivityCreationHelper() {

    // Global Variables
    private lateinit var activity: ScoutActivity


    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)
        activityId = activity.idActivity

        // Variables
        listViewTeams = findViewById(R.id.listViewTeams)
        teamAdapter = TeamsAdapter()
        listViewTeams.adapter = teamAdapter

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
        val imageViewLobitos = findViewById<ImageView>(R.id.imageViewLobitos)
        val imageViewExploradores = findViewById<ImageView>(R.id.imageViewExploradores)
        val imageViewPioneiros = findViewById<ImageView>(R.id.imageViewPioneiros)
        val imageViewCaminheiros = findViewById<ImageView>(R.id.imageViewCaminheiros)

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

        // Select the selected sections

        // Get all sections
        val sections: MutableList<Section> = arrayListOf()
        for (i in 0 until 4)
            sections.add(Section(i, false))

       GlobalScope.launch(Dispatchers.IO) {

           // Get all previous invited teams
           Backend.getAllInvitedTeams(activity.idActivity!!) { list ->
               selectedTeams.addAll(list)
           }

           // Verify if the section is already displayed
           for (i in 0 until selectedTeams.size) {

               // Get the team section
               val section = selectedTeams[i].idSection!!
               val teamSection = sections[section]

               // Select if its not already selected
               if (!teamSection.active!!) {
                   teamSection.active = true
                   GlobalScope.launch(Dispatchers.Main) {

                       // Select the corresponding section
                       val sectionImage: ImageView = when (section) {
                           1 -> imageViewLobitos
                           2 -> imageViewExploradores
                           3 -> imageViewPioneiros
                           else -> imageViewCaminheiros
                       }

                       onClickSection(sectionImage)
                   }
               }
               //teams[i]
               //onClickTeam()

           }



       }

        /*
                */

        // On click section events
        imageViewLobitos.setOnClickListener(onClickSection)
        imageViewExploradores.setOnClickListener(onClickSection)
        imageViewPioneiros.setOnClickListener(onClickSection)
        imageViewCaminheiros.setOnClickListener(onClickSection)

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

}