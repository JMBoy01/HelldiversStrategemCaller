package be.spacepex.helldiversstrategemcaller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StratCallIn : AppCompatActivity() {
    private lateinit var upButton: Button
    private lateinit var downButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var backButton: Button

    private lateinit var buttons: MutableList<Button>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strat_call_in)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        ConnectionUtils.currentActivity = this

        upButton = findViewById(R.id.upButton)
        downButton = findViewById(R.id.downButton)
        leftButton = findViewById(R.id.leftButton)
        rightButton = findViewById(R.id.rightButton)

        buttons = mutableListOf(upButton, downButton, leftButton, rightButton)

        backButton = findViewById(R.id.backButton)
        backButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val minimumPressDuration: Long = 300

                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            ConnectionUtils.sendButtonPress(Input.STRATEGEM_MENU_DOWN.toString())
                        }

                        backButton.setBackgroundColor(Color.parseColor("#33FFFFFF"))

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

                        backButton.setBackgroundColor(Color.TRANSPARENT)

                        return true
                    }
                }
                return false
            }
        })

        val slideUpAndFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_and_fade_in)
        for (button in buttons){
            setButtonTouchListener(button)
            button.startAnimation(slideUpAndFadeInAnimation)
        }
        backButton.startAnimation(slideUpAndFadeInAnimation)
    }

    private fun startNewActivity(){
        val slideDownAndFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_and_fade_out)
        for (button in buttons){
            button.startAnimation(slideDownAndFadeOutAnimation)
        }
        backButton.startAnimation(slideDownAndFadeOutAnimation)

        Handler().postDelayed({
            for (button in buttons){
                button.visibility = View.INVISIBLE
            }
            backButton.visibility = View.INVISIBLE

            val intent = Intent(this, StratMenuButton::class.java)
            startActivity(intent)
            finish()
        }, slideDownAndFadeOutAnimation.duration)
    }

    // This is so i can set the background to a different colour when you press the button
    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonTouchListener(button: Button) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    button.setBackgroundColor(Color.parseColor("#33FFFFFF"))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    button.setBackgroundColor(Color.TRANSPARENT)

                    val input = getInput(view)
                    CoroutineScope(Dispatchers.Main).launch {
                        ConnectionUtils.sendButtonPress(input)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun getInput(view: View): String{
        var input: String = ""
        when(view.id){
            R.id.upButton -> {
                input = Input.UP.toString()
            }
            R.id.downButton -> {
                input = Input.DOWN.toString()
            }
            R.id.leftButton -> {
                input = Input.LEFT.toString()
            }
            R.id.rightButton -> {
                input = Input.RIGHT.toString()
            }
        }
        return input
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}