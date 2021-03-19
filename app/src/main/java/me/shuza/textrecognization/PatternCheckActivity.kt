package me.shuza.textrecognization

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pattern_check.*
import java.util.regex.Pattern

class PatternCheckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern_check)
    }

    override fun onResume() {
        super.onResume()
        btnParse.setOnClickListener {
            val readString = edParse.text.toString()
            println("Hiii Praser ${Pattern.matches("([0-9]{3})*-([0-9]{4})*-([0-9]{7})*-([0-9])",readString)}")
        }
    }
}