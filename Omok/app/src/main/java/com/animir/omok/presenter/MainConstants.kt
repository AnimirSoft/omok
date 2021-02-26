package com.animir.omok.presenter

import android.view.View
import android.widget.GridView

interface MainConstants {
    interface Presenter {
        fun onClicker(gridView: GridView, view : View, position : Int)
    }

    interface Const{
        val ConstTAG : String get() = "Tag"
        val ConstAI : String get() = "Ai"

        val ConstPlayer1 : String get() = "P1"
        val ConstPlayer2 : String get() = "P2"
        val ConstPlayerAI : String get() = "AI"

        val ConstReSet : Int get() = -99
        val ConstP2AIChange : Int get() = -98
    }
}