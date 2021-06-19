package pt.ipca.scoutsbag.inventoryManagement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Material


class InventoryActivity : AppCompatActivity(){

    // Global Variables
    private var recyclerViewMaterial: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    var materials: List<Material> = arrayListOf()
    private var adapter: MaterialAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)
        //set actionbar title
        supportActionBar?.title = "Inventário"

        // Create the list view
        recyclerViewMaterial = findViewById<RecyclerView>(R.id.listViewMaterials)
        gridLayoutManager = GridLayoutManager(applicationContext, 2, LinearLayoutManager.VERTICAL, false)
        recyclerViewMaterial?.layoutManager = gridLayoutManager

        recyclerViewMaterial?.setHasFixedSize(true)
        adapter = MaterialAdapter()
        recyclerViewMaterial?.adapter = adapter

        // Get the materials from the data base
        GlobalScope.launch(Dispatchers.IO) {
            Backend.getAllMaterials {
                materials = it
            }

            // Refresh the listView
            GlobalScope.launch(Dispatchers.Main) {
                adapter!!.notifyDataSetChanged()
            }
        }

        // Create material button event
        findViewById<FloatingActionButton>(R.id.buttonAddMaterial).setOnClickListener {
            val intent = Intent(this, CreateMaterialActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    inner class MaterialAdapter : RecyclerView.Adapter<MaterialAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val viewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_material, parent, false)
            return ItemHolder(viewHolder)
        }

        override fun getItemCount(): Int {
            return materials.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {

            val inventoryItem: Material = materials[position]

            holder.materialName.text = inventoryItem.nameMaterial
            holder.materialQuantity.text = inventoryItem.qntStock.toString()

            if (inventoryItem.imageUrl != "") {
                Picasso.with(this@InventoryActivity).load(inventoryItem.imageUrl).into(holder.materialImage)
            }

            if(inventoryItem.qntStock == 0)
                holder.materialQuantity.setBackgroundResource(R.drawable.circle_shape_orange)

            holder.materialImage.setOnClickListener {
                val materialJsonString = inventoryItem.toJson().toString()
                MaterialDetailsDialog.newInstance(materialJsonString).show(this@InventoryActivity.supportFragmentManager, MaterialDetailsDialog.TAG)
            }

        }

        inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var materialName: TextView = itemView.findViewById(R.id.materialName)
            var materialImage: ImageView = itemView.findViewById(R.id.materialImage)
            var materialQuantity: Button = itemView.findViewById(R.id.materialQuantity)
        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

