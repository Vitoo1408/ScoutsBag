package pt.ipca.scoutsbag.activityManagement

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import java.util.*


class CreateActivityActivity : AppCompatActivity() {

    //
    private var onClickImage: (view: View)->Unit = {
        val imageView = it as ImageView

        imageView.isHovered = !imageView.isHovered

        if (!imageView.isHovered)
            imageView.setBackgroundResource(0)
        else
            imageView.setBackgroundResource(R.drawable.border)

    }

    
    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)

        // Pass the view objects to variables
        val dateStartTextView = findViewById<TextView>(R.id.dateStartButton)
        val dateEndTextView = findViewById<TextView>(R.id.dateEndButton)
        val addButton = findViewById<TextView>(R.id.buttonAddActivity)

        // Create the pop up window to select the date
        val dateStartPickerDialog = Utils.initDatePicker(dateStartTextView, this)
        val dateEndPickerDialog = Utils.initDatePicker(dateEndTextView, this)

        // On click section events
        findViewById<ImageView>(R.id.imageViewLobitos).setOnClickListener(onClickImage)
        findViewById<ImageView>(R.id.imageViewExploradores).setOnClickListener(onClickImage)
        findViewById<ImageView>(R.id.imageViewPioneiros).setOnClickListener(onClickImage)
        findViewById<ImageView>(R.id.imageViewCaminheiros).setOnClickListener(onClickImage)

        // On click button events
        dateStartTextView.setOnClickListener {
            dateStartPickerDialog.show()
        }

        dateEndTextView.setOnClickListener {
            dateEndPickerDialog.show()
        }

        addButton.setOnClickListener {
            addActivity(this)
        }

    }


    /*

     */
    private fun addActivity(context: Context) {

        // Start coroutine
        GlobalScope.launch(Dispatchers.IO) {

            // Falta saber o que fazer quando a data estiver errada
            // e algum campo vazio

            // Build the activity
            val scoutActivity = ScoutActivity(
                intent.getIntExtra("idActivity", 0),
                findViewById<TextView>(R.id.editTextActivityName).text.toString(),
                1,
                findViewById<TextView>(R.id.editTextActivityDescription).text.toString(),
                findViewById<TextView>(R.id.editTextActivityLocalizationStart).text.toString(),
                Utils.dateTimeToMySql(findViewById<TextView>(R.id.dateStartButton).text.toString()),
                Utils.dateTimeToMySql(findViewById<TextView>(R.id.dateEndButton).text.toString()),
                "1234",
                findViewById<TextView>(R.id.editTextActivityLocalizationStart).text.toString(),
                findViewById<TextView>(R.id.editTextActivityLocalizationEnd).text.toString(),
                10.5f,
            )

            // Prepare the from body request
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                scoutActivity.toJson().toString()
            )

            // Build the request
            val request = Request.Builder()
                .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/activities")
                .post(requestBody)
                .build()

            // Send the request and verify the response
            OkHttpClient().newCall(request).execute().use { response ->

                GlobalScope.launch (Dispatchers.Main){

                    if (response.message == "OK"){
                        val returnIntent = Intent(context, MainActivity::class.java)
                        startActivity(returnIntent)
                    }

                }
            }
        }

    }

}