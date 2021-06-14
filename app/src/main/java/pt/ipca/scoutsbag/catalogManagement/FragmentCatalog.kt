package pt.ipca.scoutsbag.catalogManagement

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import com.example.scoutsteste1.Catalog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class FragmentCatalog : Fragment() {

    // Global Variables
    lateinit var listViewCatalog : ListView
    lateinit var adapter : CatalogAdapter
    var catalogs : MutableList<Catalog> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_catalog, container, false)
        val buttonAddCatalog = rootView.findViewById<FloatingActionButton>(R.id.buttonAddCatalog)

        listViewCatalog = rootView.findViewById<ListView>(R.id.listViewCatalog)
        adapter = CatalogAdapter()
        listViewCatalog?.adapter = adapter

        //hide button catalog if user logged in is a scout
        if(UserLoggedIn.codType == "Esc"){
            rootView.findViewById<FloatingActionButton>(R.id.buttonAddCatalog).visibility = View.GONE
        }


        buttonAddCatalog.setOnClickListener {
            val intent = Intent(activity, AddCatalog::class.java)
            startActivity(intent)
        }

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {


            val client = OkHttpClient()
            val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/catalogs").build()

            client.newCall(request).execute().use { response ->

                catalogs.clear()

                val str : String = response.body!!.string()
                val jsonArrayActivity = JSONArray(str)

                for (index in 0 until jsonArrayActivity.length()) {
                    val jsonArticle = jsonArrayActivity.get(index) as JSONObject
                    val catalog = Catalog.fromJson(jsonArticle)
                    catalogs.add(catalog)
                }

                GlobalScope.launch (Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }

            }

        }


    }


    inner class CatalogAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return catalogs.size
        }

        override fun getItem(position: Int): Any {
            return catalogs[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_catalog, parent, false)

            // Variables
            val catalog = catalogs[position]
            val textViewNameCatalog = rowView.findViewById<TextView>(R.id.textViewNameCatalog)
            val textViewDescriptionCatalog = rowView.findViewById<TextView>(R.id.textViewDescriptionCatalog)
            val textViewTimeCatalog = rowView.findViewById<TextView>(R.id.textViewTimeCatalog)
            var rattingBarDifficulty = rowView.findViewById<RatingBar>(R.id.ratingBarDifficulty)
            val catalogImage = rowView.findViewById<ImageView>(R.id.catalogImage)


            rattingBarDifficulty.isIndicator

            //Set Data
            textViewNameCatalog.text = catalogs[position].nameCatalog
            textViewDescriptionCatalog.text = catalogs[position].catalogDescription
            rattingBarDifficulty.rating = catalogs[position].classification.toString().toFloat()
            textViewTimeCatalog.text = catalogs[position].instructionsTime.toString()

            if (catalog.imageUrl != "") {
                Picasso.with(activity).load(catalog.imageUrl).into(catalogImage)
            }


            rowView.setOnClickListener {

                val intent = Intent(activity, SeeInstructions::class.java)

                intent.putExtra("id_catalogo", catalogs[position].idCatalog.toString())
                intent.putExtra("name_Catalog", catalogs[position].nameCatalog.toString())

                startActivity(intent)

            }

            return rowView
        }
    }
}
