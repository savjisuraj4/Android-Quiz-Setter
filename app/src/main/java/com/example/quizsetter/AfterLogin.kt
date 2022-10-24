package com.example.quizsetter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.random.Random
import android.graphics.Color
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AfterLogin : AppCompatActivity() {
    private lateinit var startdatetextview:TextView
    private lateinit var enddatetextview:TextView
    private lateinit var quiztime:TextView
    private lateinit var quizcodetextview:TextView
    private lateinit var startbutton:Button
    private var quiztimeinminutes=100000
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var progressdiaglog: ProgressDialog
    private lateinit var databaseReference: DatabaseReference
    private var randomquizcode:Int=0
    @RequiresApi(Build.VERSION_CODES.N)
    private val cal = Calendar.getInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)
        startdatetextview = findViewById(R.id.fromdatetext)
        enddatetextview=findViewById(R.id.enddate)
        firestore=FirebaseFirestore.getInstance()
        quiztime=findViewById(R.id.quiztime)
        firebaseAuth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
        startbutton=findViewById(R.id.startsetquestion)
        quizcodetextview=findViewById(R.id.quizcodetextview)
        progressdiaglog= ProgressDialog(this)
        progressdiaglog.setCancelable(false)
        progressdiaglog.onBackPressed()
        progressdiaglog.setMessage("Loading...")
        progressdiaglog.show()
        randomnumber()

        try {
            startdatetextview.setOnClickListener {
                afterdatetextviewClicked(startdatetextview)
            }
            enddatetextview.setOnClickListener {
                afterdatetextviewClicked(enddatetextview)
            }
            quiztime.setOnClickListener {
                timepicker()
            }
            startbutton.setOnClickListener{checkvaliddate()}
        } catch (e: RuntimeException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun afterdatetextviewClicked(textView: TextView){
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                updateDateInView(textView)
            }
        DatePickerDialog(
            this,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView(textView: TextView) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textView.text = sdf.format(cal.time)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun timepicker(){
        val timepickerlistner=TimePickerDialog.OnTimeSetListener{view,hourofDay,minute->
            cal.set(Calendar.HOUR_OF_DAY,hourofDay)
            cal.set(Calendar.MINUTE,minute)
            val myFormat = "HH : mm"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            quiztime.text = sdf.format(cal.time)
            quiztimeinminutes=cal.get(Calendar.HOUR_OF_DAY).times(60)+cal.get(Calendar.MINUTE)
        }

        TimePickerDialog(
            this,
            timepickerlistner,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }
    private fun randomnumber() {
        randomquizcode = Random.nextInt(100000, 9999999)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("QM${randomquizcode.toString()}").exists()) {
                    randomnumber()
                }
                else{
                    quizcodetextview.text=Html.fromHtml("<font color=${Color.BLACK}>Quiz-Code: </font><font color=${Color.RED}>QM${randomquizcode.toString()}</font>")
                    progressdiaglog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG)
                    .show()
            }

        })

    }
    @SuppressLint("NewApi", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkvaliddate(){

        try {
        if(startdatetextview.text.toString()=="Set the Date" ||
                enddatetextview.text.toString()=="Set the Date" ||
                quiztime.text.toString()=="Set"){
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_baseline_error_24)
                .setMessage("Enter valid Date or Time")
                .setNeutralButton("Ok",DialogInterface.OnClickListener{dialog, which ->  })
                .create().show()
        }

        val simpleDateFormat=SimpleDateFormat("dd/MM/yyyy")
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val cuurentdate:Date=simpleDateFormat.parse(LocalDate.now().format(formatter))
        val startdate:Date=simpleDateFormat.parse(startdatetextview.text.toString())
        val enddate:Date=simpleDateFormat.parse(enddatetextview.text.toString())
        val difference :Long= (enddate.time-startdate.time)
            val differenceStart :Long= (startdate.time-cuurentdate.time)

        val differenceDates = difference / (24 * 60 * 60 * 1000)
            val differenceDatesStart = differenceStart / (24 * 60 * 60 * 1000)

            if(differenceDates<0){
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_baseline_error_24)
                .setMessage("Check Start and End date of Quiz")
                .setNeutralButton("Ok",DialogInterface.OnClickListener{dialog, which ->  })
                .create().show()
        }
        if (differenceDatesStart<0){
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_baseline_error_24)
                .setMessage("Check Start date of Quiz")
                .setNeutralButton("Ok",DialogInterface.OnClickListener{dialog, which ->  })
                .create().show()
        }
        else{
            savedata()
        }
    }catch (e:Exception){
        Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    inner class quizcodes{
         var quizcode:String
        constructor(quizcode:String){
            this.quizcode=quizcode
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addtofirebase(){
        var quizcodes=quizcodes("QM$randomquizcode")
        firestore.collection("usersSetters").whereArrayContains("quizcodelist",quizcodes)
            .get()
            .addOnCompleteListener(OnCompleteListener {
                firestore.collection("usersSetters")
                    .document(firebaseAuth.currentUser?.email.toString())
                    .update("quizcodelist", FieldValue.arrayUnion(quizcodes))
                    .addOnCompleteListener(OnCompleteListener {
                        Toast.makeText(this, "Quiz Added Successfully", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@AfterLogin, AfterLogin0::class.java))

                    }).addOnFailureListener(OnFailureListener {
                        Toast.makeText(this,it.message.toString(),Toast.LENGTH_LONG).show()
                    })
            })
            .addOnFailureListener(OnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_LONG).show()
            })

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun savedata(){
        try {
            Toast.makeText(this, randomquizcode.toString(), Toast.LENGTH_LONG).show()
            val quizinfo1 = com.example.quizsetter.quizinfo(
                startdatetextview.text.toString(),
                enddatetextview.text.toString(),
                quiztimeinminutes.toString()
            )
            databaseReference.child("QM${randomquizcode.toString()}").setValue(quizinfo1)
                .addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isComplete) {
                            addtofirebase()
                        } else {
                            Toast.makeText(
                                this,
                                task.exception?.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    })
                .addOnFailureListener(OnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()

                })
        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()

        }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }}