package pt.ipca.scoutsbag.catalogManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.scoutsteste1.Instruction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import pt.ipca.scoutsbag.R

class SeeInstructions : AppCompatActivity() {

    var listViewInstructions : ListView? = null
    lateinit var adapter : InstructionsAdapter
    var instructions : MutableList<Instruction> = arrayListOf()
    var id = ""


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
                instructions.clear()
                val jsStr: String = response.body!!.string()
                println(jsStr)

                val jsonArrayInstructions = JSONArray(jsStr)

                for (index in 0 until jsonArrayInstructions.length()) {
                    val jsonArticle: JSONObject = jsonArrayInstructions.get(index) as JSONObject
                    val instruction = Instruction.fromJson(jsonArticle)
                    instructions.add(instruction)
                }

                GlobalScope.launch(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }

        bundle?.let{
            id = it.getString("id_catalogo").toString()
        }

        buttonAddInstruction.setOnClickListener {

            val intent = Intent(this@SeeInstructions, AddInstruction::class.java)

            intent.putExtra("id", id)

            startActivity(intent)

        }

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


            val textViewIdInstruction = rowView.findViewById<TextView>(R.id.textViewIdInstruction)
            val textViewInstructionText = rowView.findViewById<TextView>(R.id.textViewInstructionText)
            val textViewImageUrl = rowView.findViewById<TextView>(R.id.textViewImageUrl)
            val textViewIdCatalogo = rowView.findViewById<TextView>(R.id.textViewIdCatalogo)

           for (index in 0 until instructions.size){
                if (instructions[index].idCatalog == id.toInt()){
                    textViewIdInstruction.text = instructions[position].idInstruction.toString()
                    textViewInstructionText.text = instructions[position].instructionText
                    textViewImageUrl.text = instructions[position].imageUrl
                    textViewIdCatalogo.text = instructions[position].idCatalog.toString()
                }
            }

            return rowView
        }
    }
}