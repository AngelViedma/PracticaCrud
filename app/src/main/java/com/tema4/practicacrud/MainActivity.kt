package com.tema4.practicacrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {

    private lateinit var crear: Button
    private lateinit var ver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        crear = findViewById(R.id.crear)
        ver = findViewById(R.id.ver)

        ver.setOnClickListener {
            val activity = Intent(applicationContext, VerPersona::class.java)
            startActivity(activity)
        }

        crear.setOnClickListener {
            val activity = Intent(applicationContext, CrearPersona::class.java)
            startActivity(activity)
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val intent: Intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}