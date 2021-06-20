package pt.ipca.scoutsbag.catalogManagement

import android.app.usage.UsageEvents
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.scoutsteste1.Instruction
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
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.activityManagement.EditActivityActivity
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn

class SeeInstructions : AppCompatActivity() {

    var listViewInstructions : ListView? = null
    lateinit var adapter : InstructionsAdapter
    var instructions : MutableList<Instruction> = arrayListOf()
    var idCatalogSelected: Int? = null
    var nameCatalog = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_instructions)

        //check internet connection
        Utils.connectionLiveData(this)

        val buttonAddInstruction = findViewById<FloatingActionButton>(R.id.buttonAddInstruction)
        val bundle = intent.extras

        listViewInstructions = findViewById<ListView>(R.id.listViewInstructions)
        adapter = InstructionsAdapter()
        listViewInstructions?.adapter = adapter

        bundle?.let{
            idCatalogSelected = it.getInt("id_catalog")
            nameCatalog = it.getString("name_catalog").toString()
        }

        GlobalScope.launch(Dispatchers.IO) {

            Backend.getAllInstructions(idCatalogSelected!!) {
                instructions.addAll(it)
            }

            GlobalScope.launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }

        }

        //hide button catalog if user logged in is a scout
        if (UserLoggedIn.codType == "Esc") {
            buttonAddInstruction.visibility = View.GONE
        }
        else {
            buttonAddInstruction.setOnClickListener {

                val intent = Intent(this@SeeInstructions, AddInstruction::class.java)

                intent.putExtra("id_catalog", idCatalogSelected)
                intent.putExtra("name_catalog", nameCatalog)

                startActivity(intent)

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_edit_menu, menu)
        title = nameCatalog
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        return UserLoggedIn.codType != "Esc"


    }

    inner class InstructionsAdapter : BaseAdapter(){

        override fun getCount(): Int {

            return instructions.size
        }

        override fun getItem(position: Int): Any {
            return  instructions[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = layoutInflater.inflate(R.layout.row_instruction, parent, false)

            val instruction = instructions[position]
            var instructionSelected = 0
            val textViewInstructionText =
                rowView.findViewById<TextView>(R.id.textViewInstructionText)
            val buttonEditInstruction = rowView.findViewById<Button>(R.id.buttonEditInstruction)
            val buttonDeleteInstruction = rowView.findViewById<Button>(R.id.buttonDeleteInstruction)
            val textViewSteps = rowView.findViewById<TextView>(R.id.textViewSteps)
            val instructionImage = rowView.findViewById<ImageView>(R.id.instructionImage)

            if (instruction.imageUrl != "") {
                Picasso.with(this@SeeInstructions).load(instruction.imageUrl).into(instructionImage)
            } else {
                instructionImage.visibility = View.GONE
            }

            textViewSteps.text = "Passo ${position + 1}"
            textViewInstructionText.text = instructions[position].instructionText

            if (UserLoggedIn.codType != "Esc") {

                buttonEditInstruction.setOnClickListener {
                    val intent = Intent(this@SeeInstructions, EditInstruction::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("instruction", instruction.toJson().toString())
                    intent.putExtra("nameCatalog", nameCatalog)
                    startActivity(intent)
                }

                buttonDeleteInstruction.setOnClickListener {
                    instructionSelected = instructions[position].idInstruction.toString().toInt()
                    deleteInstructionDialog(instructionSelected)
                }
            }
            else {
                buttonEditInstruction.visibility = View.GONE
                buttonDeleteInstruction.visibility = View.GONE
            }

            return rowView
        }
    }

    private fun deleteInstructionDialog(position: Int){


        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)

        builder.setTitle("Aviso!!")
        builder.setMessage("Tem a certeza que pretende eliminar esta instrução?")
        builder.setPositiveButton("Sim"){dialog , id ->

            Backend.removeInstruction(position)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
        builder.setNegativeButton("Não"){dialog,id->
            dialog.dismiss()
        }
        builder.show()
    }



    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}