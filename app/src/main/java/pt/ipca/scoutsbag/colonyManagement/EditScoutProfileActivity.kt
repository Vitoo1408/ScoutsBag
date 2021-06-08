package pt.ipca.scoutsbag.colonyManagement

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.scoutsteste1.ScoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.ipca.scoutsbag.Backend
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import pt.ipca.scoutsbag.activityManagement.EditActivityActivity
import pt.ipca.scoutsbag.models.Team
import pt.ipca.scoutsbag.models.User as User


class EditScoutProfileActivity : AppCompatActivity() {

    private var name: TextView? = null
    private var editNIN: EditText? = null
    private var butSave: Button? = null
    private lateinit var activity: ScoutActivity
    var teams: MutableList<Team> = arrayListOf()
    var selectedTeams: MutableList<Team> = arrayListOf()
    private var activityTypesImages: MutableList<ImageView> = arrayListOf()
    private lateinit var listViewTeams: ListView
    lateinit var adapter: EditActivityActivity.TeamsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_scout_profile)

        checkConnectivity()

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Editar perfil escuteiro"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)


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
                .setPositiveButton("Tentar novamente", DialogInterface.OnClickListener { dialog, id ->
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
