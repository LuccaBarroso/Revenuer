package com.example.revenuer.activity

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.revenuer.R
import com.example.revenuer.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mRegisterName: EditText;
    private lateinit var mRegisterPhone: EditText;
    private lateinit var mRegisterEmail: EditText;
    private lateinit var mRegisterPassword: EditText;
    private lateinit var mRegisterConfirmPassword: EditText;
    private lateinit var mRegisterButton: Button;

    private val handler = Handler(Looper.getMainLooper()!!)

    //1 - Declare an instance of FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 2 - Initialize Firebase Auth
        mAuth = Firebase.auth
        // Initialize Realtime database
        mDatabase = Firebase.database

        mRegisterName = findViewById(R.id.register_edittext_name);
        mRegisterPhone = findViewById(R.id.register_edittext_phone);
        mRegisterEmail = findViewById(R.id.register_edittext_email);
        mRegisterPassword = findViewById(R.id.register_edittext_password);
        mRegisterConfirmPassword = findViewById(R.id.register_edittext_confirmpassword);
        mRegisterButton = findViewById(R.id.register_button_enter);
        mRegisterButton.setOnClickListener(this);
    }

    override fun onClick(view: View?) {
        //one btn on the scree therefore no need for ifs ahead
        val name = mRegisterName.text.toString();
        val phone = mRegisterPhone.text.toString();
        val email = mRegisterEmail.text.toString();
        val password = mRegisterPassword.text.toString();
        val confirmPassword = mRegisterConfirmPassword.text.toString();

        var isFormFilled = true;

        isFormFilled = isFormFilled(name, mRegisterName) && isFormFilled;
        isFormFilled = isFormFilled(phone, mRegisterPhone) && isFormFilled;
        isFormFilled = isFormFilled(email, mRegisterEmail) && isFormFilled;
        isFormFilled = isFormFilled(password, mRegisterPassword) && isFormFilled;
        isFormFilled = isFormFilled(confirmPassword, mRegisterConfirmPassword) && isFormFilled;
        isFormFilled = isFormBigEnough(password, mRegisterPassword) && isFormFilled;
        isFormFilled = isFormBigEnough(confirmPassword, mRegisterConfirmPassword) && isFormFilled;
        isFormFilled = areFormsEquals(password, confirmPassword, mRegisterPassword) && isFormFilled;

        if(isFormFilled){

            val usersRef = mDatabase.getReference("/users")
            val key = usersRef.push().key ?: ""
            val newUser = User(id = key, name = name, phone = phone, email = email);
            usersRef.child(key).setValue(newUser)
            Log.i("TAG", newUser.email);

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = mAuth.currentUser
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Falha ao registrar " + task.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun isFormBigEnough(value: CharSequence, mOfTheValue: EditText): Boolean {
        return if (value.length < 6) {
            mOfTheValue.error = "Este campo precisa ter no mínimo 6 caracteres"
            false
        }else {
            true
        }
    }

    private fun areFormsEquals(value: CharSequence, secondValue: CharSequence, mOfTheValue: EditText): Boolean {
        return if (value != secondValue) {
            mOfTheValue.error = "As senhas precisam ser iguais"
            false
        }else {
            true
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
