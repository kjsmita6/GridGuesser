package com.example.gridguesser

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.gridguesser.database.GameRepository
import kotlinx.android.synthetic.main.square.view.*

class SpaceAdapter (
    private val context: Context?,
    private val squares: MutableList<String>
) : BaseAdapter() {
    private val squareList = squares

    override fun getCount(): Int {
        return squareList.size
    }

    override fun getItem(position: Int): Any {
        return squareList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val value = this.squareList[position]

        var gameRepo = GameRepository.get()


        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var spaceView = inflator.inflate(R.layout.square, null)
        var spaceTxt = value

        if ((position> 11) && (position % 11 != 0)) { // ignore first row and column
            //create buttons based on value in array
            if (value == "0") {
                spaceTxt = ""
                val colorValue = ContextCompat.getColor(context, R.color.colorDeepBlue)
                spaceView.spaceBtn.setBackgroundColor(colorValue)
            } else if (value == "1") {
                spaceTxt = ""
                val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
                spaceView.spaceBtn.setBackgroundColor(colorValue)
            } else if (value == "2") {
                spaceTxt = "X"
                val colorValue = ContextCompat.getColor(context, R.color.colorDeepBlue)
                spaceView.spaceBtn.setBackgroundColor(colorValue)
            } else if (value == "3") {
                spaceTxt = "X"
                "@drawable/main_menu_button"
                val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
               // val colorValue = ContextCompat.getDrawable(context, drawable.)
                spaceView.spaceBtn.setBackgroundColor(colorValue)
                //spaceView.spaceBtn.setBackgroundResource(R.drawable.main_menu_button)
            }
            else {
                val colorValue = ContextCompat.getColor(context, R.color.colorDeepBlue)
                //spaceView.spaceBtn.setBackgroundColor(colorValue)
                spaceView.spaceBtn.setBackgroundResource(R.drawable.board_button)

            }

            spaceView.spaceBtn.setOnClickListener {
                Log.d("SpaceAdapter", "button pressed")
                if (gameRepo.state == 0) {
                    squares[position] = "1"
                    gameRepo.remainingShips.value = gameRepo.remainingShips.value?.plus(1)
                    notifyDataSetChanged()

                }
                else if (gameRepo.state == 1){
                    //TODO: check if there is a ship on player two's board
                    //TODO: send player one's move to server
                }
                else {
                    //TODO: check if there is a ship on player one's board
                    //TODO: send player two's move to server
                }
            }
        }
        spaceView.spaceBtn.text = spaceTxt


        return spaceView
    }

}
