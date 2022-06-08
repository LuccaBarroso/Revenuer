package com.example.revenuer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.R
import com.example.revenuer.adapter.HistoryAdapter
import com.example.revenuer.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private var mUserKey = ""

    // Screen Elements
    private lateinit var mRevenueRecyclerView: RecyclerView
    private lateinit var mExpenseRecyclerView: RecyclerView
    private lateinit var mHistoryButton:Button

    // Adapter
    private lateinit var mOperationAdapter: HistoryAdapter

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
        mHistoryButton = findViewById(R.id.main_button_history)
        mHistoryButton.setOnClickListener(this)

        mRevenueRecyclerView = findViewById(R.id.main_recyclerview_revenue)
        mRevenueRecyclerView.layoutManager = LinearLayoutManager(this)
        mExpenseRecyclerView = findViewById(R.id.main_recyclerview_expense)
        mExpenseRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        val userRef = mDatabase.getReference("/users")
    }

    override fun onClick(view: View?) {
        val it = Intent(this, HistoryActivity::class.java)
        startActivity(it)
    }
}