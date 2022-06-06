package com.example.revenuer.activity

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.service.autofill.CustomDescription
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.R
import com.example.revenuer.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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

        // Screen Elements
        mOperationAdd = findViewById(R.id.history_button_add)
        mOperationAdd.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        val userRef = mDatabase.getReference("/users")
        userRef
            .orderByChild("email")
            .equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(children in snapshot.children){
                        val  user = children.getValue(User::class.java)
                        user?.operations?.values?.toList()
                            //todas as tasks são passadas aqui
                            //todo passar o adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun onClick(view: View?) {
        val it = Intent(this, OperationActivity::class.java)
        it.putExtra("isNew", true)
        startActivity(it)
    }
}