package pt.ipca.scoutsbag.catalogManagement

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.example.scoutsteste1.Catalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import java.util.*


class AddCatalog : ActivityImageHelper() {

    // Global variables
    private var imageUri: Uri? = null
    lateinit var catalogAddImage : ImageView
    var imageUrl: String? = null
    lateinit var editTextNameCatalog: EditText
    lateinit var editTextDescriptionCatalog: EditText
    lateinit var timePickerCatalog: TimePicker
    lateinit var rattingBarCatalog: RatingBar
    lateinit var buttonSaveCatalog: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_catalog)

        checkConnectivity()

        editTextNameCatalog = findViewById(R.id.editTextNameCatalog)
        editTextDescriptionCatalog = findViewById(R.id.editTextDescriptionCatalog)
        rattingBarCatalog = findViewById(R.id.ratingBarCatalogo)
        timePickerCatalog = findViewById(R.id.timePickerCatalog)
        buttonSaveCatalog = findViewById(R.id.buttonSaveCatalog)
        catalogAddImage = findViewById(R.id.catalogAddImage)

        editTextNameCatalog.addTextChangedListener(textWatcher)
        editTextDescriptionCatalog.addTextChangedListener(textWatcher)

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

            var fileName: String? = null
            var filePath: String? = null

            if (imageUri != null) {
                fileName = Utils.getFileName(this, imageUri!!)
                filePath = Utils.getUriFilePath(this, imageUri!!)
            }

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
                    if (imageUrl != null) imageUrl else ""
                )

                Backend.addCatalog(catalog,changeActivity)
            }

        }

    }


    // This function check if all data is written so the user can click in the confirm button
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            // Verify if all data is written and selected
            buttonSaveCatalog.isEnabled = (
                            editTextNameCatalog.text.toString().trim().isNotEmpty() &&
                            editTextDescriptionCatalog.text.toString().trim().isNotEmpty()
                    )

            if (buttonSaveCatalog.isEnabled) {
                // Inactivate accept button
                buttonSaveCatalog.setBackgroundResource(R.drawable.custom_button_orange)
                buttonSaveCatalog.setTextColor(resources.getColor(R.color.white))
            }
            else {
                // Inactivate accept button
                buttonSaveCatalog.setBackgroundResource(R.drawable.custom_button_white)
                buttonSaveCatalog.setTextColor(resources.getColor(R.color.orange))
            }
        }

        override fun afterTextChanged(s: Editable) {}
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