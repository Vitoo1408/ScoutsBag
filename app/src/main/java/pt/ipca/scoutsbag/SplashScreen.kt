package pt.ipca.scoutsbag

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.json.JSONObject
import org.json.JSONTokener
import pt.ipca.scoutsbag.loginAndRegister.LogInOrRegisterActivity
import pt.ipca.scoutsbag.loginAndRegister.UserLoggedIn
import java.util.*

class SplashScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        //Initial Settings
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val imageView: ImageView = findViewById(R.id.imageView_activity)
        Glide.with(this).load(R.drawable.gif_scouts_bag).into(imageView)

        /*
            Wait a few seconds in splash screen before enter the login activity (1000 millis = 1 second)
            @millisInFuture = timer duration
            @countDownInterval = time interval before enter the onTick function
         */
        val timer = object: CountDownTimer(2000, 100) {

            // Every tick do something
            override fun onTick(millisUntilFinished: Long) {
            }

            //In the end of the timer change the activity
            override fun onFinish() {
                val intent = Intent(this@SplashScreen, LogInOrRegisterActivity::class.java)
                startActivity(intent)
            }
        }

        timer.start()

    }


}