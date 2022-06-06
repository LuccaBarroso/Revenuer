package com.example.revenuer.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
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
import java.util.*

class OperationActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    // Screen Elements
    private lateinit var mRevenueButton: Button
    private lateinit var mExpenseButton: Button
    private lateinit var mOperationName: EditText
    private lateinit var mValue: EditText
    private lateinit var mDateText: TextView
    private lateinit var mDateButton: Button
    private lateinit var mCancelButton: Button
    private lateinit var mOkButton: Button

    // Action Elements
    private var isRevenuePushed:Boolean = false

    // Date Picker
    private var mYear:Int = 0
    private var mMonth:Int = 0
    private var mDay:Int = 0

    private var mSetYear:Int = 0
    private var mSetMonth:Int = 0
    private var mSetDay:Int = 0

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
        mDateText = findViewById(R.id.operation_textview_date)
        mDateButton = findViewById(R.id.operation_button_date)
        mCancelButton = findViewById(R.id.operation_button_left)
        mOkButton = findViewById(R.id.operation_button_right)

        mRevenueButton.setOnClickListener(this)
        mExpenseButton.setOnClickListener(this)
        mCancelButton.setOnClickListener(this)
        mOkButton.setOnClickListener(this)
        mDateButton.setOnClickListener(this)
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
                getDateCalendar()
                DatePickerDialog(this, this, mYear, mMonth, mDay).show()
            }
            R.id.operation_button_left -> {
                finish()
            }
            R.id.operation_button_right -> {

                val name = mOperationName.text.toString().trim()
                val value = mValue.text.toString().trim()
                val date = mDateButton.text.toString().trim()
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

    private fun getDateCalendar() {
        val calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        mSetYear = year
        mSetMonth = month
        mSetDay = day

        getDateCalendar()
        mDateText.text = "$mSetDay / $mSetMonth / $mSetYear"
    }
}