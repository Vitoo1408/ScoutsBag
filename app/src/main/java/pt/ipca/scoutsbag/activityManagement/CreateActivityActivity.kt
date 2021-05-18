package pt.ipca.scoutsbag.activityManagement

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils


class CreateActivityActivity : AppCompatActivity() {

    // Global Variables
    lateinit var dateStartTextView: TextView
    lateinit var dateEndTextView: TextView

    //
    private var onClickImage: (view: View)->Unit = {
        val imageView = it as ImageView

        imageView.isHovered = !imageView.isHovered

        if (imageView.isHovered)
            imageView.setBackgroundResource(0)
        else
            imageView.setBackgroundResource(R.drawable.border)

    }

    
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

    }

}