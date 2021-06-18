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

        val buttonAddInstruction = findViewById<FloatingActionButton>(R.id.buttonAddInstruction)
        val bundle = intent.extras

        listViewInstructions = findViewById<ListView>(R.id.listViewInstructions)
        adapter = InstructionsAdapter()
        listViewInstructions?.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {


            val client = OkHttpClient()
            val request = Request.Builder().url("http://3.8.19.24:60000/api/v1/instructions").build()

            client.newCall(request).execute().use { response ->
                val jsStr: String = response.body!!.string()
                val jsonArrayInstructions = JSONArray(jsStr)

                for (index in 0 until jsonArrayInstructions.length()) {
                    val jsonArticle: JSONObject = jsonArrayInstructions.get(index) as JSONObject
                    val instruction = Instruction.fromJson(jsonArticle)
                    if (instruction.idCatalog == idCatalogSelected)
                    instructions.add(instruction)
                }

                GlobalScope.launch(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }

        }

        bundle?.let{
            idCatalogSelected = it.getString("id_catalog").toString().toInt()
            nameCatalog = it.getString("name_catalog").toString()
        }

        buttonAddInstruction.setOnClickListener {

            val intent = Intent(this@SeeInstructions, AddInstruction::class.java)

            intent.putExtra("id", idCatalogSelected)
            
            startActivity(intent)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.delete_edit_menu, menu)
        title = nameCatalog
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //set back icon on action bar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_green_arrow_back_24)

        //hide delete and edit icon from activity details
        if(UserLoggedIn.codType == "Esc"){
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId){
            R.id.itemDelete -> {

                deleteCatalogDialog(idCatalogSelected!!)
                for(index in 0 until instructions.size){
                    if(instructions[index].idCatalog == idCatalogSelected){
                        Backend.removeInstruction(instructions[index].idInstruction.toString().toInt())
                    }
                }


                return true
            }
            R.id.itemEdit -> {
                val intent = Intent(this, ActivityEditCatalog::class.java)

                intent.putExtra("id_catalog", idCatalogSelected)

                startActivity(intent)
                return true
            }
        }

        return false
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
            val textViewInstructionText = rowView.findViewById<TextView>(R.id.textViewInstructionText)
            val buttonEditInstruction = rowView.findViewById<Button>(R.id.buttonEditInstruction)
            val buttonDeleteInstruction = rowView.findViewById<Button>(R.id.buttonDeleteInstruction)
            val textViewSteps = rowView.findViewById<TextView>(R.id.textViewSteps)
            val instructionImage = rowView.findViewById<ImageView>(R.id.instructionImage)

            if (instruction.imageUrl != "") {
                Picasso.with(this@SeeInstructions).load(instruction.imageUrl).into(instructionImage)
            }
            else {
                instructionImage.visibility = View.GONE
            }

            textViewSteps.text = "Passo ${position + 1}"
            textViewInstructionText.text = instructions[position].instructionText

            buttonEditInstruction.setOnClickListener {

                val intent = Intent(this@SeeInstructions, EditInstruction::class.java)

                intent.putExtra("instruction", instruction.toJson().toString())
                intent.putExtra("nameCatalog", nameCatalog)

                startActivity(intent)
            }

            buttonDeleteInstruction.setOnClickListener {

                instructionSelected = instructions[position].idInstruction.toString().toInt()
                deleteInstructionDialog(instructionSelected)
            }



            return rowView
        }
    }

    private fun deleteInstructionDialog(position: Int){


        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)

        builder.setTitle("Aviso!!")
        builder.setMessage("Tem a certeza que pretende eliminar esta instrução?" + position)
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

    private fun deleteCatalogDialog(catalogSelected: Int): Boolean{


        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)

        builder.setTitle("Aviso!!")
        builder.setMessage("Tem a certeza que pretende eliminar este catalogo?")
        builder.setPositiveButton("Sim"){dialog , id ->

            Backend.removeCatalog(catalogSelected)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
        builder.setNegativeButton("Não"){dialog,id->
            dialog.dismiss()
        }
        builder.show()

        return true
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}