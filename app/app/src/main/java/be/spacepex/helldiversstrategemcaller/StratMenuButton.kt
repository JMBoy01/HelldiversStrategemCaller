package be.spacepex.helldiversstrategemcaller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StratMenuButton : AppCompatActivity() {
    private lateinit var stratButton: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strat_menu_button)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        ConnectionUtils.currentActivity = this

        stratButton = findViewById(R.id.stratMenuButton)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        stratButton.startAnimation(fadeInAnimation)

        // TODO This is for a long press, but it doest work yet so thats why i comment it out.
        stratButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val minimumPressDuration: Long = 300

                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            ConnectionUtils.sendButtonPress(Input.STRATEGEM_MENU_DOWN.toString())
                        }

                        stratButton.setBackgroundColor(Color.parseColor("#33FFFFFF"))

                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            ConnectionUtils.sendButtonPress(Input.STRATEGEM_MENU_UP.toString())
                        }

                        val pressDuration = event.eventTime - event.downTime
                        if(pressDuration > minimumPressDuration){
                            startNewActivity()
                        }

                        stratButton.setBackgroundColor(Color.TRANSPARENT)

                        return true
                    }
                }
                return false
            }
        })
    }

    private fun startNewActivity(){
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        stratButton.startAnimation(fadeOutAnimation)

        Handler().postDelayed({
            stratButton.visibility = View.INVISIBLE

            val intent = Intent(this, StratCallIn::class.java)
            startActivity(intent)
            finish()
        }, fadeOutAnimation.duration)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}