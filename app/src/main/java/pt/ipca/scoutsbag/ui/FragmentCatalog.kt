package pt.ipca.scoutsbag.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Activity
import pt.ipca.scoutsbag.models.Catalog

class FragmentCatalog : Fragment() {

    // Global Variables
    lateinit var listView : ListView
    lateinit var adapter : CatalogAdapter
    var catalogs : MutableList<Catalog> = arrayListOf()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
/*
        // Start Corroutine
        GlobalScope.launch(Dispatchers.IO) {

            // OkHttp possibilita o envio de pedidos http e a leitura das respostas
            val client = OkHttpClient()

            // criação do pedido http á api do .NET
            val request = Request.Builder().url("http://" + MainActivity.IP + ":" + MainActivity.PORT + "/api/Catalog").build()

            // fazer a chamada com o pedido http e analisar a resposta
            client.newCall(request).execute().use { response ->

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
*/
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
            // val textViewCod = rowView.findViewById<TextView>(R.id.textViewCodCavalo)
            // val textViewNomeCavalo = rowView.findViewById<TextView>(R.id.textViewNomeCavalo)

            // Set data
            // textViewCod.text = activities[position].codCavalo.toString()
            // textViewNomeCavalo.text = activities[position].nomeCavalo

            return rowView
        }
    }

}
