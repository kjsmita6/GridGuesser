package com.example.gridguesser

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.gridguesser.activegames.GameListFragment
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.http.ServerInteractions
import kotlinx.android.synthetic.main.square.view.*

private const val TAG = "GridGuesser"

class SpaceAdapter (
    private val context: Context?,
    private val squares: MutableList<String>,
    private val player: Int,
    private val whichBoard: Int
) : BaseAdapter() {

    interface Callbacks {
        fun onSquareSelected(position: Int)
        fun assignShip(position: Int)
    }

    private var callbacks: Callbacks? = null

    init{
        callbacks = context as Callbacks?
    }

    override fun getCount(): Int {
        return squares.size
    }

    override fun getItem(position: Int): Any {
        return squares[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val value = this.squares[position]

        var gameRepo = GameRepository.get()
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var spaceView = inflator.inflate(R.layout.square, null)
        var spaceTxt = value

        if ((position> 11) && (position % 11 != 0)) { // ignore first row and column
            //create buttons based on value in array
                if (value == "0") {
                    spaceView.spaceBtn.setBackgroundResource(R.drawable.board_button)
                    spaceTxt = ""
                } else if (value == "1") {
                    val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                    spaceTxt = ""
                } else if (value == "2") {
                    spaceView.spaceBtn.setBackgroundResource(R.drawable.board_button)
                    spaceTxt = "X"
                } else if (value == "3") {
                    val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                    spaceTxt = "X"
                }
                else {
                    spaceView.spaceBtn.setBackgroundResource(R.drawable.board_button)
                }

            spaceView.spaceBtn.setOnClickListener {
                Log.d(TAG, "button pressed")
                if ((gameRepo.state == 0 || gameRepo.state == -1) && squares[position] == "0") {
                    Log.d(TAG, "state 0")
                    squares[position] = "1"
                    callbacks?.assignShip(position)
                    gameRepo.remainingShips.value = gameRepo.remainingShips.value?.plus(1)
                    notifyDataSetChanged()
                }
                else if (gameRepo.state == player && whichBoard == 2){
                    Log.d(TAG, "GAME STATE $player")
                    when (squares[position]) {
                        "0" -> {
                            callbacks?.onSquareSelected(position)
                        }
                        "1" -> {
                            callbacks?.onSquareSelected(position)
                        }
                        "2" -> {
                            //Cannot move here!
                        }
                        "3" -> {
                            //Cannot move here!
                        }
                        else -> {
                        }
                    }
                }
            }
        }
        spaceView.spaceBtn.text = spaceTxt

        return spaceView
    }

}
