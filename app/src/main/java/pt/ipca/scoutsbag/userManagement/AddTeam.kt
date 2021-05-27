package pt.ipca.scoutsbag.userManagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Team

class AddTeam: AppCompatActivity() {

    /*
    var onClickSection: (view: View)->Unit = {
        val imageView = it as ImageView

        imageView.isHovered = !imageView.isHovered

        if (!imageView.isHovered) {
            imageView.setBackgroundResource(0)
        }
        else {
            imageView.setBackgroundResource(R.drawable.border)
        }

    }
     */

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_team)

        // Pass the view objects to variables
        val buttonSaveTeam = findViewById<TextView>(R.id.buttonSaveTeam)

        /* // On click section events
        findViewById<ImageView>(R.id.imageViewLobitos).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewExploradores).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewPioneiros).setOnClickListener(onClickSection)
        findViewById<ImageView>(R.id.imageViewCaminheiros).setOnClickListener(onClickSection)
        */

        buttonSaveTeam.setOnClickListener {
            addTeam(this)

        }
    }

    private fun addTeam(context: Context) {

        // Start coroutine
        GlobalScope.launch(Dispatchers.IO) {

            // Build the activity
            val team = Team(
                    intent.getIntExtra("idTeam", 0),
                    findViewById<TextView>(R.id.editTextTeamName).text.toString(),
                2
            )

            // Prepare the from body request
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                team.toJson().toString()
            )

            // Build the request
            val request = Request.Builder()
                .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/teams")
                .post(requestBody)
                .build()

            // Send the request and verify the response
            OkHttpClient().newCall(request).execute().use { response ->

                GlobalScope.launch (Dispatchers.Main){

                    if (response.message == "OK"){
                        val returnIntent = Intent(context, ColonyActivity::class.java)
                        startActivity(returnIntent)
                    }

                }
            }
        }

    }


}