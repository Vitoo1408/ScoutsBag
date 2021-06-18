package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.scoutsteste1.Catalog
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.*
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import java.sql.Time
import java.util.*

class ActivityEditCatalog : ActivityImageHelper() {

    private var imageUri: Uri? = null
    lateinit var catalogEditImage : ImageView
    lateinit var catalog : Catalog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_catalog)

        var editNameCatalog = findViewById<EditText>(R.id.editNameCatalog)
        var editCatalogDescription = findViewById<EditText>(R.id.editCatalogDescription)
        var ratingBarEditCatalog = findViewById<RatingBar>(R.id.ratingBarEditCatalog)
        val saveEditCatalog = findViewById<Button>(R.id.saveEditCatalog)
        var timePickerEditCatalog = findViewById<TimePicker>(R.id.timePickerEditCatalogo)
        catalogEditImage = findViewById<ImageView>(R.id.catalogEditImage)
        val bundle = intent.extras
        var id_catalog = 0


        timePickerEditCatalog.setIs24HourView(true)
        timePickerEditCatalog.minute = 0
        timePickerEditCatalog.hour = 0

        //variables that get the values from the time picker
        var timePickerMinute = timePickerEditCatalog.minute
        var timePickerHour = timePickerEditCatalog.hour

        //variable that will save the result of the time picked in minutes
        var timeCatalog = 0

        //calculation of the time picked in hours
        if(timePickerHour > 0) {
            timeCatalog = (timePickerHour * 60) + timePickerMinute
        }
        else {
            timeCatalog = timePickerMinute
        }

        bundle?.let {
            id_catalog = it.getInt("id_catalog",0)
        }

        GlobalScope.launch(Dispatchers.IO) {

            println("id_catalog + " + id_catalog)
            catalog = Backend.getCatalog(id_catalog)

            GlobalScope.launch(Dispatchers.Main) {
                editNameCatalog.text.append(catalog.nameCatalog!!)
                editCatalogDescription.text.append(catalog.catalogDescription!!)
                ratingBarEditCatalog.rating = catalog.classification!!.toFloat()

                if (catalog.instructionsTime != null) {
                    timePickerEditCatalog.minute = (catalog.instructionsTime!! % 60)
                    timePickerEditCatalog.hour = (catalog.instructionsTime!! / 60)
                }

                if (catalog.imageUrl != "") {
                    Picasso.with(this@ActivityEditCatalog).load(catalog.imageUrl).into(catalogEditImage)
                }

            }

        }


        catalogEditImage?.setOnClickListener {
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

        saveEditCatalog.setOnClickListener {

            val fileName = Utils.getFileName(this, imageUri!!)
            val filePath = Utils.getUriFilePath(this, imageUri!!)

            GlobalScope.launch(Dispatchers.IO){

                if(imageUri != null) {
                    Utils.uploadImage(filePath!!, fileName) {
                        UserLoggedIn.imageUrl = it
                    }
                }

                val client = OkHttpClient()
                val catalog = Catalog(
                    id_catalog.toInt(),
                    editNameCatalog.text.toString(),
                    editCatalogDescription.text.toString(),
                    ratingBarEditCatalog.rating.toInt(),
                    timeCatalog,
                    if (UserLoggedIn.imageUrl != null) UserLoggedIn.imageUrl else ""

                )
                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    catalog.toJson().toString()
                )
                Log.d("scoutsbag", catalog.toJson().toString())
                val request = Request.Builder()
                    .url("http://3.8.19.24:60000/api/v1/catalogs/${id_catalog.toInt()}")
                    .put(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    GlobalScope.launch (Dispatchers.Main){

                        if (response.message == "OK"){
                            val returnIntent = Intent(this@ActivityEditCatalog, MainActivity::class.java)
                            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(returnIntent)
                        }

                    }
                }
            }

        }
    }

    /*
       This function happen after picking photo, and make changes in the activity
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ActivityImageHelper.IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            catalogEditImage?.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

}