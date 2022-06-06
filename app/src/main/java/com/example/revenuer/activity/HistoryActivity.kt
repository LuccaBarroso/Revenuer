package com.example.revenuer.activity

import android.content.Intent
import android.os.Bundle
import android.service.autofill.CustomDescription
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryActivity : AppCompatActivity(), View.OnClickListener {
    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    // Screen Elements
    private lateinit var mOperationList: RecyclerView
    private lateinit var mOperationAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Firebase
        mAuth = Firebase.auth
        mDatabase = Firebase.database

        val usersRef = mDatabase.getReference("/users");
        usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email).addChildEventListener(object:
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.key?.let { Log.i("App", it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        // Screen Elements
        mOperationAdd = findViewById(R.id.history_button_add)
        mOperationAdd.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val it = Intent(this, OperationActivity::class.java)
        it.putExtra("isNew", true)
        startActivity(it)
    }
}