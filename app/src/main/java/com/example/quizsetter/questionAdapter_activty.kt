package com.example.quizsetter

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import javax.annotation.Nonnull


class questionAdapter_activty(questionsetactivty: Questionsetactivty, QuestionList: ArrayList<questioninfo>)
    :RecyclerView.Adapter<ViewHolder>(){
    private var QuestionList: ArrayList<questioninfo>
    private lateinit var viewHolder1:ViewHolder
    private var questionsetactivty: Questionsetactivty
    init {
        this.QuestionList=QuestionList
        this.questionsetactivty=questionsetactivty
    }
    override fun onCreateViewHolder(@Nonnull parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionrecycleview, parent, false)
        viewHolder1 = ViewHolder(view)
        viewHolder1.setOnClickListener(object : ViewHolder.ClickListener {
                override fun onItemClick(view: View, position: Int) {
                    try {
                        questionsetactivty.questioninfofun(position)

                    } catch (e: Exception) {
                        Toast.makeText(
                            questionsetactivty.applicationContext,
                            e.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })

        return viewHolder1

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.questionno?.text=
            Html.fromHtml("<font color=${Color.RED}>Q.</font>"+QuestionList[position].questionno.toString())

        holder.questiontext?.text=QuestionList[position].questiontext.toString()
        holder.deletequestionButton?.setOnClickListener{
            v->
            if(v!=null){
                questionsetactivty.deletequestion(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return QuestionList.size.minus(3)
    }


}
