package pt.ipca.scoutsbag.catalogManagement

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.example.scoutsteste1.Catalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import java.util.*


class AddCatalog : ActivityImageHelper() {

    private var imageUri: Uri? = null
    lateinit var catalogAddImage : ImageView
    lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_catalog)

        checkConnectivity()

        var editTextNameCatalog = findViewById<EditText>(R.id.editTextNameCatalog)
        var editTextDescriptionCatalog = findViewById<EditText>(R.id.editTextDescriptionCatalog)
        var rattingBarCatalog = findViewById<RatingBar>(R.id.ratingBarCatalogo)
        var timePickerCatalog = findViewById<TimePicker>(R.id.timePickerCatalog)
        val buttonSaveCatalog = findViewById<Button>(R.id.buttonSaveCatalog)

        catalogAddImage = findViewById(R.id.catalogAddImage)

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

        catalogAddImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }


        buttonSaveCatalog.setOnClickListener {

            val fileName = Utils.getFileName(this, imageUri!!)
            val filePath = Utils.getUriFilePath(this, imageUri!!)

            GlobalScope.launch(Dispatchers.IO){

                if(imageUri != null) {
                    Utils.uploadImage(filePath!!, fileName) {
                        imageUrl = it
                    }
                }

                val catalog = Catalog(
                    null,
                    editTextNameCatalog.text.toString(),
                    editTextDescriptionCatalog.text.toString(),
                    rattingBarCatalog.rating.toInt(),
                    timeCatalog,
                    imageUrl
                )

                Backend.addCatalog(catalog,changeActivity)
            }

        }



    }

    /*
       This function happen after picking photo, and make changes in the activity
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            catalogAddImage?.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

}