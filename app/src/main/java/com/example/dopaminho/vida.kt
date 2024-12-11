package com.example.dopaminho

object BarraDeVida { //Objeto barra de vida, não armazenou na memória
    var vidaAtual = 100.00

    fun perdeVida(dano:Long){
        vidaAtual -= dano
        if(vidaAtual<0 ) vidaAtual= 0.0
    }

    fun ganhaVida(vida:Long){
        vidaAtual += vida
        if(vidaAtual>100 ) vidaAtual= 100.0
    }
}

