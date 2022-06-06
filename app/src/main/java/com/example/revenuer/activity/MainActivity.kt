package com.example.revenuer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.revenuer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    // Screen Elements
    private lateinit var historyButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase
        mAuth = Firebase.auth
        mDatabase = Firebase.database

        val usersRef = mDatabase.getReference("/users");
        usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email).addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //Ei ZAMUEL, o snapshot tem acesso aos dados do usuario que ta logado, já dxei td certinho pra vc
                snapshot.key?.let { Log.i("App", it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        // Screen Elements:
        historyButton = findViewById(R.id.main_button_history)
        historyButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val it = Intent(this, HistoryActivity::class.java)
        startActivity(it)
        finish()
    }
}