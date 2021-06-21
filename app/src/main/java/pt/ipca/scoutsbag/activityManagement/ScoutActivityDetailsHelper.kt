package pt.ipca.scoutsbag.activityManagement

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.scoutsteste1.ScoutActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import pt.ipca.scoutsbag.models.Material
import pt.ipca.scoutsbag.models.Section
import pt.ipca.scoutsbag.models.Team

open class ScoutActivityDetailsHelper: AppCompatActivity() {

    // Global Variables
    lateinit var activity: ScoutActivity
    var materials: List<Material> = arrayListOf()
    var teams: List<Team> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)

        // Get the selected activity
        val activityJsonStr = intent.getStringExtra("activity")
        val activityJson = JSONObject(activityJsonStr!!)
        activity = ScoutActivity.fromJson(activityJson)

        GlobalScope.launch(Dispatchers.IO) {

            // Get all materials requested for this activity
            Backend.getAllActivityMaterial(activity.idActivity!!) {
                materials = it
            }

            // Get all invited teams for this activity
            Backend.getAllInvitedTeams(activity.idActivity!!) {
                teams = it
            }

            // Display all invited sections
            getAllInvitedSections()
        }

    }


    // This function is for return to the previous activity after a operation
    var changeActivity: ()->Unit = {
        val returnIntent = Intent(this, MainActivity::class.java)
        startActivity(returnIntent)
    }


    /*
        This function create the action bar above the activity
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_edit_menu, menu)
        title = activity.nameActivity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        //hide delete and edit icon from activity details
        if(UserLoggedIn.codType == "Esc"){
            return false
        }
        return true
    }


    /*
        This function define the events of the action bar buttons
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId){
            R.id.itemDelete -> {
                deleteActivityDialog()
                return true
            }
            R.id.itemEdit -> {
                val intent = Intent(this, EditActivityActivity::class.java)
                intent.putExtra("activity", activity.toJson().toString())
                startActivity(intent)
                return true
            }
        }

        return false
    }


    /*
        This function shows a pop-up window that alerts the user that the activity will be deleted.
     */
    private fun deleteActivityDialog() {

        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)

        builder.setTitle("Aviso!!")
        builder.setMessage("Tem a certeza que pretende eliminar esta atividade?")
        builder.setPositiveButton("Sim"){dialog , id ->

            GlobalScope.launch(Dispatchers.IO) {
                Backend.removeActivity(activity.idActivity!!) {
                    val intent = Intent(this@ScoutActivityDetailsHelper, MainActivity::class.java)
                    startActivity(intent)
                }
            }

        }
        builder.setNegativeButton("Não"){dialog,id->
            dialog.dismiss()
        }
        builder.show()

    }


    /*
        This function display a dialog window with a list with
        material that can be selected to this activity
     */
    fun openMaterialListDialog() {

        // Variables
        val alertDialog = AlertDialog.Builder(this)
        val row = layoutInflater.inflate(R.layout.dialog_material_selected, null)
        val listView = row.findViewById<ListView>(R.id.listViewMaterials)
        val mAdapter = MaterialsAdapter()

        // Set data
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        // Create dialog
        alertDialog.setAdapter(mAdapter) { _, _ -> }
        alertDialog.setView(row)
        alertDialog.create().show()
    }


    /*
        This function return the invited section list
     */
    private fun getAllInvitedSections() {

        // Get all sections
        val sections: MutableList<Section> = arrayListOf()
        for (i in 0 until 4)
            sections.add(Section(i, false))

        // Verify if the section is already displayed
        var position = 0
        for (i in 0 until teams.size) {

            // Get the team section
            val teamSection = sections[teams[i].idSection!!-1]

            // Display the image
            if (!teamSection.active!!) {
                teamSection.active = true
                GlobalScope.launch(Dispatchers.Main) {
                    getSectionImage(teamSection.idSection!!, position)
                    position++
                }

            }
        }

    }


    /*
        Enable the images of the selected sections
        @section = selected section
        @position = slot in the view
     */
    private fun getSectionImage(section: Int, position: Int) {

        // Get the position of the image in the view
        val imageSlot = when (position) {
            0 -> R.id.imageViewSlot1
            1 -> R.id.imageViewSlot2
            2 -> R.id.imageViewSlot3
            else -> R.id.imageViewSlot4
        }

        // Get the image of the section
        val imageResource = when (section) {
            0 -> R.drawable.icon_lobitos
            1 -> R.drawable.icon_exploradores
            2 -> R.drawable.icon_pioneiros
            else -> R.drawable.icon_caminheiros
        }

        // Add section image in the selected position
        val imageView = findViewById<ImageView>(imageSlot)
        imageView.setImageResource(imageResource)
    }


    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    inner class MaterialsAdapter : BaseAdapter() {

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
            val rowView = layoutInflater.inflate(R.layout.row_activity_material, parent, false)

            // Variables
            val material = materials[position]
            val textViewName     = rowView.findViewById<TextView>(R.id.textViewMaterialName)
            val textViewQuantity = rowView.findViewById<TextView>(R.id.textViewMaterialQuantity)
            val materialImage = rowView.findViewById<ImageView>(R.id.materialImage)

            if (material.imageUrl != "") {
                Picasso.with(this@ScoutActivityDetailsHelper).load(material.imageUrl).into(materialImage)
            }

            // Set data
            textViewName.text = material.nameMaterial
            textViewQuantity.text = "Quantidade: " + material.qntStock.toString()

            return rowView
        }
    }

}