package com.example.stepview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customStepView = findViewById<CustomStepView>(R.id.custom_step_id)
        val btnNext = findViewById<Button>(R.id.btn)
        btnNext.setOnClickListener {
            customStepView.stepNextCurrent()
        }
    }
}