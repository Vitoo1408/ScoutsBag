package pt.ipca.scoutsbag.loginAndRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pt.ipca.scoutsbag.MainActivity
import pt.ipca.scoutsbag.R

class LogInOrRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_or_register)

        //check if a user is already logged in or not
        val preferences = getSharedPreferences("userLogin", MODE_PRIVATE)
        val loggedIn = preferences.getString("loggedIn", "")

        if(loggedIn == "true"){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

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