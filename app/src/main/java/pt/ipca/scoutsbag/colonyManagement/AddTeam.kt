package pt.ipca.scoutsbag.colonyManagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Team

class AddTeam: AppCompatActivity() {

    // Global Variables
    var sectionImages: MutableList<ImageView> = arrayListOf()

    var buttonSaveTeam : Button? = null

    var onClickSection: (view: View)->Unit = {
        val imageView = it as ImageView

        for (i in 0 until sectionImages.size){
            sectionImages[i].setBackgroundResource(0)
            sectionImages[i].isHovered = false
        }

        imageView.isHovered = true
        imageView.setBackgroundResource(R.drawable.border)

        // Inactivate button
        buttonSaveTeam!!.setBackgroundResource(R.drawable.custom_button_orange)
        buttonSaveTeam!!.isClickable = true

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
        buttonSaveTeam = findViewById(R.id.buttonSaveTeam)
        sectionImages.add(findViewById(R.id.imageViewLobitos))
        sectionImages.add(findViewById(R.id.imageViewExploradores))
        sectionImages.add(findViewById(R.id.imageViewPioneiros))
        sectionImages.add(findViewById(R.id.imageViewCaminheiros))

        // On click section events
        for (i in 0 until sectionImages.size){
            sectionImages[i].setOnClickListener(onClickSection)
        }

        buttonSaveTeam!!.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {
                // Build the team
                val team = Team(
                    intent.getIntExtra("idTeam", 0),
                    findViewById<TextView>(R.id.editTextTeamName).text.toString(),
                    getSection()
                )
                Backend.addTeam(team, changeActivity)

            }
        }

        // Inactivate button
        buttonSaveTeam!!.setBackgroundResource(R.drawable.custom_button_white_unfocused)
        buttonSaveTeam!!.isClickable = false

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