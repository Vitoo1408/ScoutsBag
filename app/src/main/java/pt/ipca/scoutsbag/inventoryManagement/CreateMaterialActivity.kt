package pt.ipca.scoutsbag.inventoryManagement

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.colonyManagement.ActivityUserRequest
import pt.ipca.scoutsbag.colonyManagement.EditProfileActivity
import pt.ipca.scoutsbag.models.Material

class CreateMaterialActivity : AppCompatActivity() {

    // This function is for return to the previous activity after a operation
    var returnActivity: ()->Unit = {
        val returnIntent = Intent(this, InventoryActivity::class.java)
        startActivity(returnIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_material)

        // Variables in the activity
        val textViewName = findViewById<TextView>(R.id.editTextMaterialName)
        val textViewQuantity = findViewById<TextView>(R.id.editTextMaterialQuantity)
        val buttonCancel = findViewById<TextView>(R.id.button_cancel)
        val buttonConfirm = findViewById<TextView>(R.id.button_confirm)
        val materialImage = findViewById<ImageView>(R.id.materialAddImage)

        // Button Events
        buttonCancel.setOnClickListener { returnActivity }

        buttonConfirm.setOnClickListener {

            // Create the material
            val material = Material(
                null,
                textViewName.text.toString(),
                textViewQuantity.text.toString().toInt(),
                "null",
                "Consumivel"
            )

            // Add the material
            GlobalScope.launch(Dispatchers.IO) {
                Backend.addMaterial(material, returnActivity)
            }
        }

    }


}