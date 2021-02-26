package com.animir.omok.presenter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import com.animir.omok.R
import com.animir.omok.views.MainActivity

class MainPresenter (context : Context): MainConstants.Presenter, MainConstants.Const{
    var context = context
    var dolSize : Int = 0

    var turnFlag = false    // fasle : P1, true : P2
    var mAIFlag = true      // true : AI, false : P2
    var gameFlag = false    // true : GameStart, false : GameOver

    fun init(dolSize: Int){
        this.dolSize = dolSize
    }

    override fun onClicker(gridView: GridView, view : View, position : Int) {
        when(position){
            //ReSet
            ConstReSet -> {
                if(!gameFlag){
                    val arrayMaxPosition = gridView.childCount - 1
                    for(value in 0..arrayMaxPosition){
                        setDol(gridView.getChildAt(value) as LinearLayout, 0)
                    }
                    gameFlag = true

                    (context as MainActivity).setOrder(if(turnFlag) 1 else 2)
                    (context as MainActivity).init()

                    Toast.makeText(context, "게임을 시작합니다.!!", Toast.LENGTH_SHORT).show()
                }else
                    Toast.makeText(context, "게임이 시작중입니다.!!!", Toast.LENGTH_SHORT).show()

            } // when -99 End
            // Player2, AI 변경 버튼
            ConstP2AIChange ->{
                if(!gameFlag){
                    mAIFlag = !mAIFlag
                    (context as MainActivity).player2AIOrder(mAIFlag)


                }
            }// when -98 End
            else -> {
                if(gameFlag) {
                    val tag = (view.tag as MutableMap<String, Int>).get("Tag")

                    // 중복 클릭 방지
                    if(((turnFlag && tag != 0) || (!turnFlag && tag != 0))){
                        return
                    }

                    gameCheck(gridView, view, position)

                    if(mAIFlag){
                        Log.d("#@#", "AI Play!!")

                        // AI 가중치 판단
                        val mutableMap = (gridView.getChildAt(position)?.tag as MutableMap<String, Int>)
                        val tag = mutableMap["Tag"] as Int

                        //Log.d("#@#", "tag : " + tag)

                        //Left
                        setAIWeigth(gridView, position, (position - 1), tag)

                        //Right
                        setAIWeigth(gridView, position, (position + 1), tag)

                        //Top
                        setAIWeigth(gridView, position, (position - 19), tag)

                        //Bottom
                        setAIWeigth(gridView, position, (position + 19), tag)

                        //LeftTop
                        setAIWeigth(gridView, position, (position - 20), tag)

                        //RightTop
                        setAIWeigth(gridView, position, (position - 18), tag)

                        //LeftBottom
                        setAIWeigth(gridView, position, (position + 18), tag)

                        //RightBottom
                        setAIWeigth(gridView, position, (position + 20), tag)

                        var gameBoard = "gameBoard \n"
                        Log.d("#@#", "gridView.childCount : " + gridView.childCount)
                        for(value in 0..(gridView.childCount-1)){
                            val mutableMap = gridView.getChildAt(value).tag as MutableMap<String, Int>
                            val weigth = mutableMap[ConstAI] as Int

                            gameBoard += "$weigth "

                            if(value % 19 == 18)
                                gameBoard += "\n"

                        }
                        Log.d("#@#", gameBoard)

                        // 최적에 포인트 찾기

//                        var weightList = ArrayList<Int>()
//
//                        for(value in 0..(gridView.childCount-1)){
//                            var mutableMap = gridView.getChildAt(value).tag as MutableMap<String, Int>
//                            var weigth = mutableMap.get(ConstTAG) as Int
//                            if(weigth < 0)
//                                weightList.add(weigth)
//                        }
//
//                        var maxWeight : Int
//                        maxWeight = weightList.get(0)
//
//                        for(value in 0..(weightList.size-1)){
//                            if(maxWeight > weightList.get(value)){
//                                maxWeight = weightList.get(value)
//                            }
//                        }

                        // AI 놓기
                        //gameCheck(gridView, gridView.getChildAt(maxWeight), maxWeight)
                    }
                    //Log.d("#@#", "${view.tag}" + " Position : ${position} "+ " Count : " + "${count}")
                }else{
                    Toast.makeText(context, "게임을 시작해주세요.!", Toast.LENGTH_SHORT).show()
                }
            } // when else end
        }// when end
    }

    fun gameCheck(gridView: GridView, view : View, position : Int){
        turnFlag = !turnFlag

        (context as MainActivity).setOrder(if(turnFlag) 1 else 2)

        // 돌 을 놓는다.
        setDol(view as LinearLayout, if(turnFlag) 1 else 2)

        val tag = (gridView.getChildAt(position)?.tag as MutableMap<String, Int>).get(ConstTAG) as Int

        // 판정
        var count = 0

        //LeftRight
        count = countLeftRight(position, gridView, tag)
        if(count >= 5){
            gameOver(tag)
            return
        }

        //TopBottom
        count = countTopBottom(position, gridView, tag)
        if(count >= 5){
            gameOver(tag)
            return
        }

        //LeftTop RightBottom
        count = countLeftTopRightBottom(position, gridView, tag)
        if(count >= 5){
            gameOver(tag)
            return
        }

        //LeftBottom RightTop
        count = conuntLeftBottomRightTop(position, gridView, tag)
        if(count >= 5){
            gameOver(tag)
            return
        }
    }

    /**
     * 왼쪽 오른쪽 게임판정
     */
    private fun countLeftRight(position : Int, gridView : GridView, tag: Int) : Int{
        var count = 0
        val posCenter = position
        val posRight = posCenter + 4
        val posLeft = posCenter - 4

        //Left
        for(value in posCenter downTo posLeft){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        //Right
        for(value in posCenter..posRight){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        count -= 1

        return count
    }

    /**
     * 위 아래 게임 판정
     */
    private fun countTopBottom(position : Int, gridView: GridView, tag: Int) : Int{
        var count = 0
        val posCenter = position
        val posBottom = posCenter * 4
        val posTop = (posCenter - (posCenter * 4))
        val stapVal = 19

        //Top
        for(value in posCenter downTo posTop step stapVal){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        //Bottom
        for(value in posCenter..posBottom step stapVal){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        count -= 1

        return count
    }

    /**
     * 왼쪽위 오른쪽 아래 게임 판단
     */
    private fun countLeftTopRightBottom(position : Int, gridView: GridView, tag: Int) : Int{
        var count = 0
        val posCenter = position
        val posRightBottom = (posCenter+1) * 4
        val posLeftTop = (posCenter - ((posCenter+1) * 4))
        val stapVal = 20

        //LeftTop
        for(value in posCenter downTo posLeftTop step stapVal){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        //RightBottom
        for(value in posCenter..posRightBottom step stapVal){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        count -= 1

        return count
    }

    /**
     * 왼쪽아래 오른쪽 위 게임 판단
     */
    private fun conuntLeftBottomRightTop(position : Int, gridView: GridView, tag: Int) : Int{
        var count = 0
        val posCenter = position
        val posLeftBottom = (posCenter-1) * 4
        val posRightTop = (posCenter - ((posCenter-1) * 4))
        val stapVal = 18

        //RightTop
        for(value in posCenter downTo posRightTop step stapVal){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        //LeftBottom
        for(value in posCenter..posLeftBottom step stapVal){
            val valTag = getTag(gridView, value)
            if(tag == valTag) {
                count += 1
            }
            else
                break
        }

        count -= 1

        return count
    }

    private fun getTag(gridView : GridView, position : Int) : Int{
        var tag = -1
        if(position <= (gridView.childCount-1) && position >= 0)
            tag = (gridView.getChildAt(position)?.tag as MutableMap<String, Int>)?.get(ConstTAG) as Int

        return tag
    }

    fun setAIWeigth(gridView : GridView, orignalPosition : Int,  position: Int, tag : Int){

        if(position <= (gridView.childCount-1) && position >= 0){
            if (orignalPosition == 0){
                // 왼쪽위
                // 왼쪽아래, 왼쪽, 왼쪽위, 위, 오른쪽위
                if((orignalPosition +18 == position || orignalPosition -1 == position || orignalPosition -20 == position || orignalPosition -19 == position || orignalPosition -18 == position)){
                    Log.d("#@#", "왼쪽 위")
                    return
                }
            }else if (orignalPosition == 18){
                // 오른쪽 위
                // 오른쪽아래, 오른쪽, 오른쪽위, 위, 왼쪽위
                if((orignalPosition +20 == position || orignalPosition +1 == position || orignalPosition -18 == position || orignalPosition -19 == position || orignalPosition -20 == position)){
                    Log.d("#@#", "오른쪽 위")
                    return
                }
            }else if (orignalPosition == 342){
                // 왼쪽 아래
                // 왼쪽위, 왼쪽, 왼쪽아래, 아래, 오른쪽아래
                if((orignalPosition -20 == position || orignalPosition -19 == position || orignalPosition -20 == position || orignalPosition -18 == position || orignalPosition +20 == position)){
                    Log.d("#@#", "왼쪽 아래")
                    return
                }
            }else if (orignalPosition == 360){
                // 오른쪽 아래
                // 오른쪽위, 오른쪽, 오른쪽아래, 아래, 왼쪽아래
                if((orignalPosition -18 == position || orignalPosition +1 == position || orignalPosition +20 == position || orignalPosition +19 == position || orignalPosition +18 == position)){
                    Log.d("#@#", "오른쪽 아래")
                    return
                }
            }else if((orignalPosition % 19) == 0){
                // 왼쪽벽
                // 왼쪽위, 왼쪽, 왼쪽아래
                if((orignalPosition -20 == position || orignalPosition -1 == position || orignalPosition +18 == position)){
                    Log.d("#@#", "왼쪽 벽")
                    return
                }
            }else if((orignalPosition % 19) == 18){
                // 오른쪽 벽
                // 오른쪽위, 오른쪽, 오른쪽아래
                if((orignalPosition -18 == position || orignalPosition +1 == position || orignalPosition +20 == position)){
                    Log.d("#@#", "오른쪽 벽")
                    return
                }
            }else if (orignalPosition > 0 || orignalPosition < 18) {
                // 위쪽 벽
                // 왼쪽위, 위, 오른쪽위
                if((orignalPosition -20 == position || orignalPosition -19 == position || orignalPosition -18 == position)){
                    Log.d("#@#", "위쪽 벽")
                    return
                }
            }else if (orignalPosition > (342) || orignalPosition < 361){
                // 아래쪽 벽
                // 왼쪽아래, 아래, 오른쪽아래
                if((orignalPosition +18 == position || orignalPosition +19 == position || orignalPosition +20 == position)){
                    Log.d("#@#", "아래쪽 벽")
                    return
                }
            }

            val mutableMapLeft = (gridView.getChildAt(position)?.tag as MutableMap<String, Int>)
            var aiWeigth = mutableMapLeft.get(ConstAI) as Int

            Log.d("#@#", "mutableMapLeft.get(ConstTAG) : " + mutableMapLeft[ConstTAG])

            if(mutableMapLeft[ConstTAG] as Int == 0){
                if(tag == 2 && aiWeigth > 0) {
                    aiWeigth -= 1
                } else if(tag == 1){
                    aiWeigth += 1
                }
            }else{
                aiWeigth = 0
            }

            mutableMapLeft[ConstAI] = aiWeigth

            gridView.getChildAt(position)?.tag = mutableMapLeft
        }
    }

    fun gameOver(tag : Int){
        turnFlag = false
        gameFlag = false
        (context as MainActivity).setOrder(0)
        Toast.makeText(context, "Win!!  " + (if(tag == 1) "White" else "Black"), Toast.LENGTH_SHORT).show()
    }

    fun setDol(layout : LinearLayout, tag : Int){
        layout.removeAllViews()
        val imagV = ImageView(this.context)
        val params = LinearLayout.LayoutParams(dolSize-10, dolSize-10)
        imagV.layoutParams = params

        when(tag){
            1 -> imagV.setBackgroundResource(R.drawable.dol1_shape)
            2 -> imagV.setBackgroundResource(R.drawable.dol2_shape)
            else -> imagV.setBackgroundResource(R.drawable.non_shape)
        }

        val hashMap = layout.tag as MutableMap<String, Int>
        hashMap[ConstTAG] = tag
        hashMap[ConstAI] = 0
        layout.tag = hashMap
        layout.addView(imagV)
    }
}