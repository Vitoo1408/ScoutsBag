package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.scoutsteste1.Catalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R

class AddCatalog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_catalog)

        var editTextNameCatalog = findViewById<EditText>(R.id.editTextNameCatalog)
        var editTextDescriptionCatalog = findViewById<EditText>(R.id.editTextDescriptionCatalog)
        var editTextClassificationCatalog = findViewById<EditText>(R.id.editTextClassificationCatalog)
        var editTextTimeCatalog = findViewById<EditText>(R.id.editTextTimeCatalog)
        val buttonSaveCatalog = findViewById<Button>(R.id.buttonSaveCatalog)

        buttonSaveCatalog.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                val client = OkHttpClient()
                val catalog = Catalog(
                    null,
                    editTextNameCatalog.text.toString(),
                    editTextDescriptionCatalog.text.toString(),
                    editTextClassificationCatalog.text.toString().toInt(),
                    editTextTimeCatalog.text.toString()
                )

                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    catalog.toJson().toString()
                )

                Log.d("scoutsbag", catalog.toJson().toString())
                val request = Request.Builder()
                    .url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/catalogs")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->

                    GlobalScope.launch (Dispatchers.Main){

                        if (response.message == "OK"){
                            val returnIntent = Intent(this@AddCatalog, MainActivity::class.java)
                            startActivity(returnIntent)
                        }

                    }
                }



            }

        }
    }
}