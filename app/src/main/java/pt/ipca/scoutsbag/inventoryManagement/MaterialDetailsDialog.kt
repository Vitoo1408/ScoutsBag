package pt.ipca.scoutsbag.inventoryManagement

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.widget.doAfterTextChanged
import com.squareup.picasso.Picasso
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.Material

class MaterialDetailsDialog : AppCompatDialogFragment() {

    // Global Variables
    private lateinit var material: Material
    private var materialChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val materialJsonString = it.getString(ARG_MESSAGE)
            val materialJson = Material.fromJson(JSONObject(materialJsonString))
            material = materialJson
        }

        setStyle(STYLE_NO_TITLE, 0)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.dialog_material_details, container, false)

        //
        rootView.findViewById<TextView>(R.id.textViewNameMaterial).text = material.nameMaterial
        rootView.findViewById<TextView>(R.id.textViewMaterialType).text = material.materialType
        val materialImage = rootView.findViewById<ImageView>(R.id.imageViewMaterial)
        val editTextQuantity = rootView.findViewById<TextView>(R.id.editTextMaterialQuantity)
        val deleteButton = rootView.findViewById<ImageView>(R.id.itemDelete)

        editTextQuantity.text = material.qntStock.toString()

        if (material.imageUrl != "") {
            Picasso.with(this.context).load(material.imageUrl).into(materialImage)
        }

        editTextQuantity.doAfterTextChanged {
            val text = it.toString()
            if (text != "")
                material.qntStock = text.toInt()
            else
                material.qntStock = 0

            materialChanged = true
        }

        deleteButton.setOnClickListener {
            Backend.removeMaterial(material.idMaterial!!) {
                val intent = Intent(this.context, InventoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }

        return rootView
    }

    override fun onCancel(dialog: DialogInterface) {

        // Edit the material
        Backend.editMaterial(material)

        // Refresh the activity
        if (materialChanged) {
            val intent = Intent(this.context, InventoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        super.onCancel(dialog)
    }

    companion object {
        const val TAG = "myDialog"
        private const val ARG_MESSAGE = "argMessage"

        fun newInstance(message: String) = MaterialDetailsDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
        }
    }
}