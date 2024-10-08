package com.example.srodenas.loginfirebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class ActivityRegister : AppCompatActivity() {

    private lateinit var btnRegister : Button
    private lateinit var btnLastRegister : Button
    private lateinit var editUser : EditText
    private lateinit var editPassword : EditText
    private lateinit var editRepeatPassword : EditText
    private lateinit var auth : FirebaseAuth  //para autenticarme en firebase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()  //referenciamos los componentes.
        start() //comienza todo.
    }

    private fun init(){
        btnRegister = findViewById(R.id.btn_register_in_register)
        btnLastRegister = findViewById(R.id.btn_last_register)
        editUser = findViewById(R.id.edit_user_register)
        editPassword = findViewById(R.id.edit_pass_register)
        editRepeatPassword = findViewById(R.id.pass_register_repeat_in_register)

        auth = Firebase.auth
    }


    private fun start() {
        btnRegister.setOnClickListener{

        }

        btnLastRegister.setOnClickListener{

        }
    }
}