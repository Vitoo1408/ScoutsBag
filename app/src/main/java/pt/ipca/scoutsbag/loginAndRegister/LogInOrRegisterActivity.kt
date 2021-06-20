package pt.ipca.scoutsbag.loginAndRegister

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import pt.ipca.scoutsbag.ConnectionLiveData
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R
import pt.ipca.scoutsbag.Utils
import kotlin.system.exitProcess

class LogInOrRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_or_register)

        //check if a user is already logged in or not
        val preferences = getSharedPreferences("userLogin", MODE_PRIVATE)
        val loggedIn = preferences.getString("loggedIn", "")

        //no internet dialog
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Sem internet")
        alertDialog.setMessage("A aplicação precisa de acesso à internet para iniciar corretamente... A sair da aplicação...")
        alertDialog.setIcon(R.drawable.ic_no_internet)
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)

        //check if device has internet connection
        var isOnline = Utils.isOnline(this)

        if(loggedIn == "true" && isOnline){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        if(!isOnline) {
            alert.show()

            val timer = object: CountDownTimer(3000, 100) {

                // Every tick do something
                override fun onTick(millisUntilFinished: Long) {
                }

                //In the end of the timer change the activity
                override fun onFinish() {
                    alert.dismiss()
                    moveTaskToBack(true)
                    exitProcess(-1)
                }
            }

            timer.start()
        }


        //check internet connection continuously
        Utils.connectionLiveData(this)

        findViewById<Button>(R.id.bt_Register).setOnClickListener() {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button_LogIn).setOnClickListener() {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}