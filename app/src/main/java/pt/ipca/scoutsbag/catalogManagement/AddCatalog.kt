package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TimePicker
import com.example.scoutsteste1.Catalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R

class AddCatalog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_catalog)

        var editTextNameCatalog = findViewById<EditText>(R.id.editTextNameCatalog)
        var editTextDescriptionCatalog = findViewById<EditText>(R.id.editTextDescriptionCatalog)
        var rattingBarCatalog = findViewById<RatingBar>(R.id.ratingBarCatalogo)
        val buttonSaveCatalog = findViewById<Button>(R.id.buttonSaveCatalog)
        var timePickerCatalog = findViewById<TimePicker>(R.id.timePickerCatalog)

        //
        timePickerCatalog.setIs24HourView(true)

        //variables that get the values from the time picker
        var timePickerMinute = timePickerCatalog.minute
        var timePickerHour = timePickerCatalog.hour

        //variable that will save the result of the time picked in minutes
        var timeCatalog = 0

        //calculation of the time picked in hours
        if(timePickerHour > 0)
        {
            timeCatalog = (timePickerHour * 60) + timePickerMinute
        }
        else
        {
            timeCatalog = timePickerMinute
        }


        var changeActivity: ()->Unit = {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
        }


        buttonSaveCatalog.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                val catalog = Catalog(
                    null,
                    editTextNameCatalog.text.toString(),
                    editTextDescriptionCatalog.text.toString(),
                    rattingBarCatalog.rating.toInt(),
                    timeCatalog
                )

                Backend.addCatalog(catalog,changeActivity)
            }

        }
    }
}