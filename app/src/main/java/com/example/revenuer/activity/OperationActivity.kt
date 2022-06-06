package com.example.revenuer.activity

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.revenuer.R
import com.example.revenuer.entity.Operation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class OperationActivity : AppCompatActivity(), View.OnClickListener {
    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    // Screen Elements
    private lateinit var mRevenueButton: Button
    private lateinit var mExpenseButton: Button
    private lateinit var mOperationName: EditText
    private lateinit var mValue: EditText
    private lateinit var mDate: Button
    private lateinit var mCancelButton: Button
    private lateinit var mOkButton: Button

    // Action Elements
    private var isRevenuePushed:Boolean = false
    private lateinit var mDatePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation)

        // Firebase
        mAuth = Firebase.auth
        mDatabase = Firebase.database

        // Screen Elements
        mRevenueButton = findViewById(R.id.operation_button_revenue)
        mExpenseButton = findViewById(R.id.operation_button_expense)
        mOperationName = findViewById(R.id.operation_edittext_name)
        mValue = findViewById(R.id.operation_edittext_value)
        mDate = findViewById(R.id.operation_button_date)
        mCancelButton = findViewById(R.id.operation_button_cancel)
        mOkButton = findViewById(R.id.operation_button_ok)

        mRevenueButton.setOnClickListener(this)
        mExpenseButton.setOnClickListener(this)
        mCancelButton.setOnClickListener(this)
        mOkButton.setOnClickListener(this)
        mDate.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.operation_button_revenue -> {
                isRevenuePushed = true
                mRevenueButton.backgroundTintList = getColorStateList(R.color.green)
                mExpenseButton.backgroundTintList = getColorStateList(R.color.blue)
            }
            R.id.operation_button_expense -> {
                isRevenuePushed = false
                mRevenueButton.backgroundTintList = getColorStateList(R.color.blue)
                mExpenseButton.backgroundTintList = getColorStateList(R.color.red)
            }
            R.id.operation_button_date -> {
                // TODO: fazer date picker !
            }
            R.id.operation_button_cancel -> {
                finish()
            }
            R.id.operation_button_ok -> {

                val name = mOperationName.text.toString().trim()
                val value = mValue.text.toString().trim()
                val date = mDate.text.toString().trim()
                val operationType = isRevenuePushed // true = receita (revenue), false = despesa (expense)

                // TODO: erros de entrada - lucca

                val usersRef = mDatabase.getReference("/users");
                usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email).addChildEventListener(object:
                    ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val operationRef = usersRef
                            .child(snapshot.key!!)
                            .child("/operations")

                        val operationId = operationRef
                            .push()
                            .key?:""

                        val operation = Operation(
                            id = operationId,
                            name = name,
                            value = value,
                            date = date,
                            type = operationType
                        )

                        operationRef
                            .child(operationId)
                            .setValue(operation)

                        Toast.makeText(baseContext, "Operação $name cadastrada com sucesso!",
                            Toast.LENGTH_SHORT).show()
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onChildRemoved(snapshot: DataSnapshot) {}

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }
}