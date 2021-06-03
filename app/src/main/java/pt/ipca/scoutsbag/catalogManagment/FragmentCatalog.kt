package pt.ipca.scoutsbag.catalogManagment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.scoutsteste1.Catalog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.activityManagement.CreateActivityActivity

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

        listViewCatalog = rootView.findViewById<ListView>(R.id.listViewCatalog)
        adapter = CatalogAdapter()
        listViewCatalog?.adapter = adapter

        val buttonAddCatalog = rootView.findViewById<FloatingActionButton>(R.id.buttonAddCatalog)

        buttonAddCatalog.setOnClickListener {
            val intent = Intent(activity, AddCatalog::class.java)
            startActivity(intent)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Start Corroutine
        GlobalScope.launch(Dispatchers.IO) {

            // OkHttp possibilita o envio de pedidos http e a leitura das respostas
            val client = OkHttpClient()

            // criação do pedido http á api do .NET
            val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/v1/catalogs").build()

            // fazer a chamada com o pedido http e analisar a resposta
            client.newCall(request).execute().use { response ->

                catalogs.clear()

                // resposta do pedido http retornada em string
                val str : String = response.body!!.string()

                // converter a str em um array de json, e esse json é de cavalos
                val jsonArrayActivity = JSONArray(str)

                // add the horses to the list
                for (index in 0 until jsonArrayActivity.length()) {
                    val jsonArticle = jsonArrayActivity.get(index) as JSONObject
                    val catalog = Catalog.fromJson(jsonArticle)
                    catalogs.add(catalog)
                }

                // Refresh the list adapter
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
            val textViewNameCatalog = rowView.findViewById<TextView>(R.id.textViewNameCatalog)
            val textViewDescriptionCatalog = rowView.findViewById<TextView>(R.id.textViewDescriptionCatalog)
            val textViewClassificationCatalog = rowView.findViewById<TextView>(R.id.textViewClassificationCatalog)
            val textViewTimeCatalog = rowView.findViewById<TextView>(R.id.textViewTimeCatalog)
            val buttonEditCatalog = rowView.findViewById<Button>(R.id.buttonEditCatalog)

            // Set data
            textViewNameCatalog.text = catalogs[position].nameCatalog
            textViewDescriptionCatalog.text = catalogs[position].catalogDescription
            textViewClassificationCatalog.text = catalogs[position].classification.toString()
            textViewTimeCatalog.text = catalogs[position].instructionsTime

            rowView.setOnClickListener {

                val intent = Intent(activity, SeeInstructions::class.java)

                intent.putExtra("id_catalogo", catalogs[position].idCatalog.toString())

                startActivity(intent)

            }

            buttonEditCatalog.setOnClickListener {
                val intent = Intent(activity, ActivityEditCatalog::class.java)

                intent.putExtra("id_catalog", catalogs[position].idCatalog.toString())

                startActivity(intent)
            }



            return rowView
        }
    }

}
