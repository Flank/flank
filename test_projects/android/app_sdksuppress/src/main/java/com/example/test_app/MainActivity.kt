package com.example.test_app

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button1).setOnClickListener {
            Toast.makeText(this, "toast", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Alert Dialog")
                .setPositiveButton("OK") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .show()
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            throw Exception()
        }
    }
}
