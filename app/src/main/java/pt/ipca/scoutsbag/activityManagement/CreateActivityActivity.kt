package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.widget.*
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.models.*

class CreateActivityActivity : ScoutActivityCreationHelper() {



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
        teamAdapter = TeamsAdapter()
        listViewTeams.adapter = teamAdapter

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


}