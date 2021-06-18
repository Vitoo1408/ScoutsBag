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
    var catalogs : List<Catalog> = arrayListOf()


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
            Backend.getAllCatalogs {
                catalogs = it
            }

            GlobalScope.launch (Dispatchers.Main) {
                adapter.notifyDataSetChanged()
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

            if (catalogs[position].instructionsTime != null)
                textViewTimeCatalog.text = "Tempo: " + convertSecondsToFullTime(catalogs[position].instructionsTime!!)
            else
                textViewTimeCatalog.text = "Tempo Indeterminado"

            if (catalog.imageUrl != "") {
                Picasso.with(activity).load(catalog.imageUrl).into(catalogImage)
            }

            rowView.setOnClickListener {

                val intent = Intent(activity, SeeInstructions::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("id_catalog", catalogs[position].idCatalog)
                intent.putExtra("name_catalog", catalogs[position].nameCatalog.toString())

                startActivity(intent)
            }

            return rowView
        }
    }


    /*
        This function convert seconds to minutes and seconds
        @seconds = the initial amount of seconds
     */
    fun convertSecondsToFullTime(seconds: Int): String {

        val minutes = seconds / 60
        val newSeconds = seconds % 60

        return "$minutes Minutos e $newSeconds Segundos"
    }

}
