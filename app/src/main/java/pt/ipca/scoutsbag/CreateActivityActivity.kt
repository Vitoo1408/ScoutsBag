package pt.ipca.scoutsbag

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class CreateActivityActivity : AppCompatActivity() {

    // Global Variables
    lateinit var dateStartTextView: TextView
    lateinit var dateEndTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)

        // Pass the view objects to variables
        dateStartTextView = findViewById(R.id.dateStartButton)
        dateEndTextView = findViewById(R.id.dateEndButton)

        // Create the pop up window to select the date
        val dateStartPickerDialog = Utils.initDatePicker(dateStartTextView, this)
        val dateEndPickerDialog = Utils.initDatePicker(dateEndTextView, this)

        // On click button events
        dateStartTextView.setOnClickListener {
            dateStartPickerDialog.show()
        }

        dateEndTextView.setOnClickListener {
            dateEndPickerDialog.show()
        }

    }

}