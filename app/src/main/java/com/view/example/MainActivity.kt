package com.view.example

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.view.circulartimerview.CircularTimerListener
import com.view.circulartimerview.CircularTimerView
import com.view.circulartimerview.TimeFormatEnum
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.ceil

class MainActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress_circular.progress = 0f
        progress_circular.setCircularTimerListener(object : CircularTimerListener {
            override fun updateDataOnTick(remainingTimeInMs: Long): String {
                return ceil((remainingTimeInMs / 1000f).toDouble()).toString()
            }
            override fun onTimerFinished() {
                Toast.makeText(this@MainActivity, "FINISHED", Toast.LENGTH_SHORT).show()
                progress_circular.setPrefix("")
                progress_circular.setSuffix("")
                progress_circular.setText("FINISHED THANKS!")
            }
        }, 10, TimeFormatEnum.SECONDS, 10)
         btnRestart.setOnClickListener(View.OnClickListener {
            progress_circular.startTimer()
        })
         btnStop.setOnClickListener {
             progress_circular.stopTimer()
         }
    }
}