package com.example.revenuer.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.revenuer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginEmail: EditText;
    private lateinit var mLoginPassword: EditText;
    private lateinit var mLoginRegister: TextView;
    private lateinit var mLoginEnter: Button;

    private val handler =Handler(Looper.getMainLooper()!!)

    //1 - Declare an instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 2 - Initialize Firebase Auth
        auth = Firebase.auth

        mLoginEmail = findViewById(R.id.login_edittext_email)
        mLoginPassword = findViewById(R.id.login_edittext_password)
        mLoginRegister = findViewById(R.id.login_textview_register)
        mLoginRegister.setOnClickListener(this);
        mLoginEnter = findViewById(R.id.login_button_enter)
        mLoginEnter.setOnClickListener(this);
    }

    //When initializing your Activity, check to see if the user is currently signed in.
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null) {
            // TODO: make user logged in
            // if user is already logged in, go to main
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.login_textview_register ->{
                val it = Intent(this, RegisterActivity::class.java);
                startActivity(it)
            }

            R.id.login_button_enter-> {
                val email = mLoginEmail.text.toString()
                val password = mLoginPassword.text.toString()

                var isFormFilled = true

                isFormFilled  =  isFormFilled(email, mLoginEmail) && isFormFilled;
                isFormFilled  =  isFormFilled(password, mLoginPassword) && isFormFilled;

                if(isFormFilled){
                    //Login the user
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                //Pass in the user data to the next activity
                                //Go to the main activity
                                val it = Intent(this, MainActivity::class.java);
                                it.putExtra("user", user)
                                startActivity(it)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun isFormFilled(value: CharSequence, mOfTheValue: EditText): Boolean {
        return if (value.isBlank()) {
            mOfTheValue.error = "Este campo é obrigatório"
            false
        }else {
            true
        }
    }
}