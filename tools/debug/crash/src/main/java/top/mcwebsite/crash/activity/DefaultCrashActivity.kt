package top.mcwebsite.crash.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import top.mcwebsite.crash.CrashHandlerCore
import top.mcwebsite.crash.R

class DefaultCrashActivity : AppCompatActivity() {

    private lateinit var restartButton: TextView
    private lateinit var closeButton: TextView
    private lateinit var stackTraceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_crash)
        initView()
        initData()
    }


    private fun initView() {
        restartButton = findViewById(R.id.restart)
        closeButton = findViewById(R.id.close)
        stackTraceTextView = findViewById(R.id.stack_trace)
    }

    private fun initData() {
        stackTraceTextView.text = CrashHandlerCore.getAllErrorDetailFromIntent(this, intent).apply {
            Log.e("DefaultCrashActivity", this)
        }
    }
}
