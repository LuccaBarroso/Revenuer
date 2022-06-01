package com.example.revenuer.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.revenuer.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginEmail: EditText;
    private lateinit var mLoginPassword: EditText;
    private lateinit var mLoginRegister: TextView;
    private lateinit var mLoginEnter: Button;

    private val handler =Handler(Looper.getMainLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mLoginEmail = findViewById(R.id.login_edittext_email)
        mLoginPassword = findViewById(R.id.login_edittext_password)
        mLoginRegister = findViewById(R.id.login_textview_register)
        mLoginRegister.setOnClickListener(this);
        mLoginEnter = findViewById(R.id.login_button_enter)
        mLoginEnter.setOnClickListener(this);
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

                isFormFilled  =  isEmailFilled(email) && isFormFilled;
                isFormFilled  =  isPasswordFilled(password) && isFormFilled;
            }
        }
    }
    private fun isPasswordFilled(password: CharSequence): Boolean {
        return if (password.isBlank()) {
            mLoginPassword.error = "Este campo é obrigatório"
            false
        }else {
            true
        }
    }

    private fun isEmailFilled(email: CharSequence): Boolean {
        return if (email.isBlank()) {
            mLoginEmail.error = "Este campo é obrigatório"
            false
        }else {
            true
        }
    }
}