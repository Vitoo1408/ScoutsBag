package pt.ipca.scoutsbag

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class CreateActivityActivity : AppCompatActivity() {

    // Global Variables
    lateinit var dateStartButton: Button
    lateinit var dateEndButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)

        // Pass the view objects to variables
        dateStartButton = findViewById(R.id.dateStartButton)
        dateEndButton = findViewById(R.id.dateEndButton)

        // Create the pop up window to select the date
        val dateStartPickerDialog = Utils.initDatePicker(dateStartButton, this)
        val dateEndPickerDialog = Utils.initDatePicker(dateEndButton, this)

        // On click button events
        dateStartButton.setOnClickListener {
            dateStartPickerDialog.show()
        }

        dateEndButton.setOnClickListener {
            dateEndPickerDialog.show()
        }

    }

}