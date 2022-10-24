package com.example.quizsetter

class questioninfo {
    var questionno: String? = null
    var questiontext: String? = null
    var option1: String? = null
    var option2: String? = null
    var option3: String? = null
    var option4: String? = null
    var answer: String? = null

    constructor(     questionno: String,
                     questiontext: String,
                     option1: String,
                     option2: String,
                     option3: String,
                     option4: String,
                     answer: String){
        this.questionno=questionno
        this.questiontext=questiontext
        this.option1=option1
        this.option2=option2
        this.option3=option3
        this.option4=option4
        this.answer=answer
    }
}
