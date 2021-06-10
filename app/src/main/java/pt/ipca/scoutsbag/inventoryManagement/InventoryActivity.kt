package pt.ipca.scoutsbag.inventoryManagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.Material


class InventoryActivity : AppCompatActivity(){

    // Global Variables
    var materials: List<Material> = arrayListOf()
    lateinit var adapter: MaterialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        // Create the list view
        val listViewMaterial1 = findViewById<ListView>(R.id.listViewMaterials)
        adapter = MaterialAdapter()
        listViewMaterial1.adapter = adapter

        // Get the materials from the data base
        GlobalScope.launch(Dispatchers.IO) {
            Backend.getAllMaterials {
                materials = it
            }

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }

        // Create material button event
        findViewById<FloatingActionButton>(R.id.buttonAddMaterial).setOnClickListener {
            val intent = Intent(this, CreateMaterialActivity::class.java)
            startActivity(intent)
        }

    }

    inner class MaterialAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return materials.size
        }

        override fun getItem(position: Int): Any {
            return materials[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_material, parent, false)

            // Get current material
            val material = materials[position]

            rowView.findViewById<TextView>(R.id.materialName).text = material.nameMaterial
            rowView.findViewById<Button>(R.id.materialQuantity).text = material.qntStock.toString()
            val imageMaterial = rowView.findViewById<ImageView>(R.id.materialImage)

            // Load material image
            if (material.imageUrl != ""){
                Picasso.with(this@InventoryActivity).load(material.imageUrl).into(imageMaterial)
            }

            if (material.qntStock == 0)
                rowView.findViewById<Button>(R.id.materialQuantity).setBackgroundResource(R.drawable.circle_shape_orange)

            rowView.setOnClickListener {
                val materialJsonString = material.toJson().toString()
                MaterialDetailsDialog.newInstance(materialJsonString).show(this@InventoryActivity.supportFragmentManager, MaterialDetailsDialog.TAG)
            }

            return rowView
        }
    }
}