package com.animir.omok.views

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.animir.omok.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.*
import com.animir.omok.R
import com.animir.omok.presenter.MainConstants

class MainActivity : AppCompatActivity(), View.OnClickListener, MainConstants.Const{
    val mainPresenter = MainPresenter(this)

    var gameBoardX : Int = 19
    var gameBoardY : Int = 19
    var dolSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        player2AIOrder(false)
    }

    fun init(){

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        dolSize = (screenWidth / gameBoardX)

        mainPresenter.init(dolSize)

        val layoutChild = ArrayList<LinearLayout>()

        for(boardXItem in 1..(gameBoardX * gameBoardY)){
            val layout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(dolSize, dolSize)
            layout.layoutParams = params
            layout.setBackgroundResource(R.drawable.non_shape)
            val hashMap = mutableMapOf<String, Int>()
            hashMap[ConstTAG] = 0
            hashMap[ConstAI] = 0
            layout.tag = hashMap
            layoutChild.add(layout)
        }

        gamelayout.adapter = GameBoardAdapter(this, dolSize, layoutChild)

        val params : FrameLayout.LayoutParams = FrameLayout.LayoutParams(screenWidth, screenWidth)
        params.gravity = Gravity.CENTER
        game_bg.layoutParams = params

        gamelayout.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            if (view != null) {
                mainPresenter.onClicker(gamelayout, view, position)
            }
        }

        btn_reset.setOnClickListener(this)
        tv_player2_layout.setOnClickListener(this)

    }
    override fun onClick(view: View) {
        var postion = 0
        when(view.id){
            R.id.btn_reset -> postion = ConstReSet
            R.id.tv_player2_layout -> postion = ConstP2AIChange
        }
        mainPresenter.onClicker(gamelayout, view, postion)
    }

    // 플레이어 순서 표시
    fun setOrder(turn : Int){
        when(turn){
            1 -> {
                tv_player1_layout.isEnabled = true
                tv_player2_layout.isEnabled = false
            }
            2 -> {
                tv_player1_layout.isEnabled = false
                tv_player2_layout.isEnabled = true
            }
            else -> {
                tv_player1_layout.isEnabled = true
                tv_player2_layout.isEnabled = true
            }
        }
    }

    fun player2AIOrder(turn : Boolean){
        if(turn)
            tv_player2.text = ConstPlayerAI
        else
            tv_player2.text = ConstPlayer2
    }

    class GameBoardAdapter : BaseAdapter {
        var layoutList = ArrayList<LinearLayout>()
        var context: Context? = null
        var dolSize : Int = 0

        constructor(context: Context, dolSize : Int, layoutList: ArrayList<LinearLayout>) : super() {
            this.context = context
            this.dolSize = dolSize
            this.layoutList = layoutList
        }

        override fun getCount(): Int {
            return layoutList.size
        }

        override fun getItem(position: Int): Any {
            return layoutList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val layout = this.layoutList[position]
            val tag = layout.tag

            val imagV = ImageView(this.context)
            val params = LinearLayout.LayoutParams(dolSize-10, dolSize-10)
            imagV.layoutParams = params

            when(tag){
                1 -> imagV.setBackgroundResource(R.drawable.dol1_shape)
                2 -> imagV.setBackgroundResource(R.drawable.dol2_shape)
                else -> imagV.setBackgroundResource(R.drawable.non_shape)
            }

            layout.addView(imagV)
            return layout
        }
    }
}

