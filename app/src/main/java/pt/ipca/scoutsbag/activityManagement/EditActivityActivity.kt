package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipca.scoutsbag.R

class EditActivityActivity : AppCompatActivity(), ActivitiesDbHelper{

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_activity)
/*
        val editViewAxtivityName = findViewById<TextView>(R.id.editTextActivityName)
        val editViewAxtivityDescription = findViewById<TextView>(R.id.editTextActivityDescription)
        val editViewAxtivityDescription = findViewById<TextView>(R.id.dateStartButton)
        val editViewAxtivityDescription = findViewById<TextView>(R.id.dateEndButton)
        val editViewAxtivityDescription = findViewById<TextView>(R.id.editTextActivityLocalizationStart)
        val editViewAxtivityDescription = findViewById<TextView>(R.id.editTextActivityLocalizationEnd)
        val editViewAxtivityDescription = findViewById<TextView>(R.id.editTextActivityDescription)


 */
    }

}