package com.flank.gameloop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val launchIntent = intent
        if (launchIntent.action == "com.google.intent.action.TEST_LOOP") {
            when(val scenario = launchIntent.getIntExtra("scenario", 0)){
                1->scenario1(scenario)
                2->scenario2(scenario*2)
                3->scenario3(scenario*3)
            }
            finish()
        } else {
            Thread.sleep(1000L)
            finish()
        }
    }

    fun scenario1(defaultvalue: Int) {
        println("Scenario 1 triggered with default value: $defaultvalue, will pause for $defaultvalue*10")
        Thread.sleep((defaultvalue * 10).toLong())
    }

    fun scenario2(defaultvalue: Int) {
        println("Scenario 2 triggered with default value: $defaultvalue, will pause for $defaultvalue*20")
        Thread.sleep((defaultvalue * 10).toLong())
    }

    fun scenario3(defaultvalue: Int) {
        println("Scenario 3 triggered with default value: $defaultvalue, will pause for $defaultvalue*30")
        Thread.sleep((defaultvalue * 10).toLong())
    }
}