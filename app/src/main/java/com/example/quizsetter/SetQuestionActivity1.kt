package com.example.quizsetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.*
import javax.annotation.Nonnull

class SetQuestionActivity1 : AppCompatActivity() {
    lateinit var question: EditText
    lateinit var option1: EditText
    lateinit var option2: EditText
    lateinit var option3: EditText
    lateinit var option4: EditText
    lateinit var add_button: Button
    lateinit var questioninfo: questioninfo1
    var questionNO: Int=0
    lateinit var quizcode:String
    lateinit var questionlist: ArrayList<String>
    lateinit var answer: AppCompatAutoCompleteTextView
    var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_question1)
        try {
            question = findViewById(R.id.question)
            option1 = findViewById(R.id.option1)
            option2 = findViewById(R.id.option2)
            option3 = findViewById(R.id.option3)
            option4 = findViewById(R.id.option4)
            answer = findViewById(R.id.answer)
            add_button = findViewById(R.id.addquestion)

            answer.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.dropdown_item,
                    resources.getStringArray(R.array.answer)
                )
            )
            quizcode = intent.getStringExtra("Quiz-code").toString()
            databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
                .child(quizcode.toString())

            databaseReference!!.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(@Nonnull snapshot: DataSnapshot) {
                    questionNO=snapshot.childrenCount.minus(3).toInt()
                    for (datasnapshot: DataSnapshot in snapshot.children) {
                        try {
                            if (datasnapshot.exists() &&
                                datasnapshot.key?.equals("enddate") == false &&
                                datasnapshot.key?.equals("startdate") == false &&
                                datasnapshot.key?.equals("time") == false &&
                                        datasnapshot.key?.equals(quizcode)==false
                            ) {
                                if(datasnapshot.key.toString().toInt()==questionNO){
                                    questionNO+1
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                applicationContext,
                                e.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        applicationContext,
                        error.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                questionlist = bundle.getStringArrayList("questionlist") as ArrayList<String>

                question.setText(questionlist[0])
                option1.setText(questionlist[1])
                option2.setText(questionlist[2])
                option3.setText(questionlist[3])
                option4.setText(questionlist[4])
                answer.setText(questionlist[5])
                add_button.isEnabled=false
                add_button.visibility= View.INVISIBLE
                question.isEnabled=false
                option1.isEnabled=false
                option2.isEnabled=false
                option3.isEnabled=false
                option4.isEnabled=false
                answer.isEnabled=false
            }
        }
            catch (e:java.lang.NullPointerException){
                e.toString()
            }
             catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    e.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }

        add_button.setOnClickListener {
            checkValidity(questionNO)
        }
    }
    data class questioninfo1(   var question: String,
                                var option1: String,
                                var option2: String,
                                var option3: String,
                                var option4: String,
                                var answer: String
    )
    private fun checkValidity(questionNO: Int) {
        try {
            when {
                question.text.isNullOrEmpty() -> question.error = "field is empty"
                option1.text.isNullOrEmpty() -> option1.error = "field is empty"
                option2.text.isNullOrEmpty() -> option2.error = "field is empty"
                option3.text.isNullOrEmpty() -> option3.error = "field is empty"
                option4.text.isNullOrEmpty() -> option4.error = "field is empty"
                answer.text.isNullOrEmpty() -> answer.error = "field is empty"
                else -> {
                    questioninfo = questioninfo1(
                        question.text.toString(),
                        option1.text.toString(),
                        option2.text.toString(),
                        option3.text.toString(),
                        option4.text.toString(),
                        answer.text.toString()
                    )
                    addQuestion(questionNO+1)
                }
            }
        }catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        e.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
    private fun addQuestion(questionNo: Int) {
            try {
                databaseReference?.child((questionNo+1).toString())?.setValue(questioninfo)
                    ?.addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (task.isComplete) {
                                Toast.makeText(this,"Question Added Successfully",Toast.LENGTH_LONG).show()
                                val intent = Intent(applicationContext, Questionsetactivty::class.java)
                                intent.putExtra("quizcode", quizcode.toString())
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    task.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    ?.addOnFailureListener(OnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            it.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    })

            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{

                val intent = Intent(applicationContext, AfterLogin0::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onBackPressed() {

        val intent = Intent(applicationContext, AfterLogin0::class.java)
        startActivity(intent)
        finish()
    }

}