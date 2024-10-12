package com.example.srodenas.loginfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth

class ActivityLogin : AppCompatActivity() {
    private lateinit var btnLogin : Button
    private lateinit var btnRegister : Button
    private lateinit var btnRecoverPass : Button
    private lateinit var editUser : EditText
    private lateinit var editPassword : EditText
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()  //referenciamos los componentes.
        start() //comienza todo.
    }


    private fun init(){
        btnLogin = findViewById(R.id.btn_login_in_login)
        btnRegister = findViewById(R.id.btn_register_in_login)
        btnRecoverPass = findViewById(R.id.btn_recover_pass)
        editUser = findViewById(R.id.edit_user_login)
        editPassword = findViewById(R.id.edit_pass_login)
        auth = Firebase.auth  //Creamos el objeto de autentication.
    }


    private fun start() {
        btnLogin.setOnClickListener{
            val user = editUser.text.toString()
            val pass = editPassword.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty())
                startLogin(user, pass){
                    result, msg ->
                        Toast.makeText(this@ActivityLogin, msg, Toast.LENGTH_LONG).show()
                        if (result){
                            intent = Intent(this@ActivityLogin, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
            else
                Toast.makeText(this, "Tienes algún campo vacío", Toast.LENGTH_LONG).show()
        }

        btnRegister.setOnClickListener{
            intent = Intent (this, ActivityRegister::class.java)
            startActivity(intent)
        }

        btnRecoverPass.setOnClickListener{
            val user = editUser.text.toString()
            if (user.isNotEmpty())
                recoverPassword(user){
                    result, msg ->
                        Toast.makeText(this@ActivityLogin, msg, Toast.LENGTH_LONG).show()
                         if (!result)
                             editUser.setText("")
                }
            else
                Toast.makeText(this, "Debes rellenar el campo email", Toast.LENGTH_LONG).show()
        }
    }

    /*
    Cuando queramos la recuperación de password, al mandar un email
    válido, firebase lo saca como satisfactorio exista o no el usuario
    en firebase. Eso lo hace, porque no quiere que compruebe, si un usuario
    existe en su bbdd. Es lógico.
     */
    private fun recoverPassword(email : String, onResult: (Boolean, String)->Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{
                taskResetEmail ->
                    if (taskResetEmail.isSuccessful){
                        onResult (true, "Acabamos de enviarte un email con la nueva password")
                    }else{
                        var msg = ""
                        try{
                            throw taskResetEmail.exception?:Exception("Error de reseteo inesperado")
                        }catch (e : FirebaseAuthInvalidCredentialsException){
                            msg = "El formato del email es incorrecto"
                        }catch (e: Exception){
                            msg = e.message.toString()
                        }
                        onResult(false, msg)


                    }
            }


    }

    private fun startLogin(user: String, pass: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(user, pass)
            .addOnCompleteListener {
                taskAssin ->
                    var msg = ""
                    if (taskAssin.isSuccessful){
                        //debemos comprobar si el usuario ha verificado el email
                        val posibleUser = auth.currentUser
                        if (posibleUser?.isEmailVerified == true){
                            onResult ( true, "Usuario Logueado satisfactoriamente")
                        }else{
                            auth.signOut() //hay que desloguearse, porque no ha verificado.
                            onResult (false, "Debes verificar tu correo antes de loguearte")
                        }
                    }else{
                        try {
                            throw taskAssin.exception?: Exception("Error desconocido")
                        }catch (e: FirebaseAuthInvalidUserException){
                            msg = "El usuario tiene problemas por haberse borrado o desabilitado"
                        }catch (e: FirebaseAuthInvalidCredentialsException){
                            msg = if (e.message?.contains("There is no user record corresponding to this identifier") == true){
                                "El usuario no existe"
                            }else "contraseña incorrecta"

                        }catch (e: Exception){
                            msg = e.message.toString()
                        }

                        onResult (false, msg)  //genérico.
                    }

            }

    }
}