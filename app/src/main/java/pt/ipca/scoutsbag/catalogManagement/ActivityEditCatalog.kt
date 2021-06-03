package pt.ipca.scoutsbag.catalogManagment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.scoutsteste1.Catalog
import com.example.scoutsteste1.Instruction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R

class ActivityEditCatalog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_catalog)

        var editNameCatalog = findViewById<EditText>(R.id.editNameCatalog)
        var editCatalogDescription = findViewById<EditText>(R.id.editCatalogDescription)
        var editCatalogClassification = findViewById<EditText>(R.id.editCatalogClassification)
        val saveEditCatalog = findViewById<Button>(R.id.saveEditCatalog)
        var teste = findViewById<TextView>(R.id.teste)
        val bundle = intent.extras
        var id_catalog = ""

        bundle?.let{
            id_catalog = it.getString("id_catalog").toString()
        }

        teste.text = id_catalog

        saveEditCatalog.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                val client = OkHttpClient()
                val catalog = Catalog(
                    id_catalog.toInt(),
                    editNameCatalog.text.toString(),
                    editCatalogDescription.text.toString()
                    ,editCatalogClassification.text.toString().toInt()
                    ,null


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
                            val returnIntent = Intent(this@ActivityEditCatalog, FragmentCatalog::class.java)
                            startActivity(returnIntent)
                        }

                    }
                }
            }

        }
    }
}