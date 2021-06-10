package pt.ipca.scoutsbag.colonyManagement

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.models.User

class ProfileActivity : AppCompatActivity() {

    lateinit var user: User
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        checkConnectivity()

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Perfil"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        // Variables
        val textName = findViewById<TextView>(R.id.scoutName)
        val textSection = findViewById<TextView>(R.id.scoutSection)
        val textTeam = findViewById<TextView>(R.id.scoutTeam)
        val textNIN = findViewById<TextView>(R.id.textNIN)
        val textGender = findViewById<TextView>(R.id.textGender)
        val textPhone = findViewById<TextView>(R.id.textPhone)
        val textMail = findViewById<TextView>(R.id.textMail)
        val textBirthDate = findViewById<TextView>(R.id.textBirthDate)
        val textAddress = findViewById<TextView>(R.id.textAddress)
        val textPostalCode = findViewById<TextView>(R.id.textPostalCode)
        val butEdit = findViewById<Button>(R.id.editProfile)
        val textStats = findViewById<TextView>(R.id.textStats)


        //println("->>" + textName.text.toString())

        // Set data
        textName.text = user.userName
        //textSection.text = user.sec
        //textTeam.text = user.idTeam
        textNIN.text = user.nin
        textGender.text = user.gender
        textPhone.text = user.contact
        textMail.text = user.email
        textBirthDate.text = user.birthDate
        textAddress.text = user.address
        textPostalCode.text = user.postalCode


        butEdit.setOnClickListener() {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkConnectivity() {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo

        if (null == activeNetwork) {
            val dialogBuilder = AlertDialog.Builder(this)
            // set message of alert dialog
            dialogBuilder.setMessage("Tenha a certeza que o WI-FI ou os dados móveis estão ligados.")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(
                    "Tentar novamente",
                    DialogInterface.OnClickListener { dialog, id ->
                        recreate()
                    })
                // negative button text and action
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Sem conexão à internet")
            alert.setIcon(R.mipmap.ic_launcher)
            // show alert dialog
            alert.show()
        }
    }

    //when the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}