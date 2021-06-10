package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Team

class AddTeam: AppCompatActivity() {

    // Global Variables
    var sectionImages: MutableList<ImageView> = arrayListOf()

    var onClickSection: (view: View)->Unit = {
        val imageView = it as ImageView

        for (i in 0 until sectionImages.size){
            sectionImages[i].setBackgroundResource(0)
            sectionImages[i].isHovered = false
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
        setContentView(R.layout.activity_add_team)

        // Pass the view objects to variables
        val buttonSaveTeam = findViewById<Button>(R.id.buttonSaveTeam)
        val button = findViewById<Button>(R.id.button)
        sectionImages.add(findViewById(R.id.imageViewLobitos))
        sectionImages.add(findViewById(R.id.imageViewExploradores))
        sectionImages.add(findViewById(R.id.imageViewPioneiros))
        sectionImages.add(findViewById(R.id.imageViewCaminheiros))

        // On click section events
        for (i in 0 until sectionImages.size){
            sectionImages[i].setOnClickListener(onClickSection)
        }

        buttonSaveTeam.setOnClickListener {

            // Build the team
            val team = Team(
                intent.getIntExtra("idTeam", 0),
                findViewById<TextView>(R.id.editTextTeamName).text.toString(),
                getSection()
            )
            Backend.addTeam(team, changeActivity)

        }

    }

    fun getSection(): Int {

        var sectionId: Int = 0

        for (i in 0 until sectionImages.size){
            if(sectionImages[i].isHovered)
                sectionId = i
        }

        return sectionId + 1
    }

}