package com.example.quizsetter

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import javax.annotation.Nonnull


class ViewHolder(@Nonnull mView: View) : RecyclerView.ViewHolder(mView) {
    var questionno: TextView?=null
    var questiontext: TextView?=null
    var sr_no:TextView?=null
    var quizcode:TextView?=null
    var startdate:TextView?=null
    var enddate:TextView?=null
    var mClickListener: ClickListener? = null
    var deletequizbutton:Button?=null
    var deletequestionButton:ImageButton?=null
     init {
        sr_no=itemView.findViewById(R.id.sr_no)
        quizcode=itemView.findViewById(R.id.quizcoderecycleviewtextview)
        questionno=itemView.findViewById(R.id.questionno)
        questiontext=itemView.findViewById(R.id.questiontext)
        startdate=itemView.findViewById(R.id.startdate)
         deletequizbutton=itemView.findViewById(R.id.deletequizcode)
         enddate=itemView.findViewById(R.id.enddate)
         deletequestionButton=itemView.findViewById(R.id.deletequestion)
         itemView.setOnClickListener { view -> mClickListener!!.onItemClick(view, adapterPosition) }
     }

    interface ClickListener {
        fun onItemClick(view: View, position: Int)

    }

    fun setOnClickListener(clickListener:ClickListener) {
        mClickListener = clickListener
    }
}
