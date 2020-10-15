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
    private val player: Int
) : BaseAdapter() {

    interface Callbacks {
        fun onSquareSelected(position: Int)
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
                Log.d("SpaceAdapter", "button pressed")
                if (gameRepo.state == 0 || gameRepo.state == -1) {
                    Log.d(TAG, "state 0")
                    squares[position] = "1"
                    gameRepo.remainingShips.value = gameRepo.remainingShips.value?.plus(1)
                    notifyDataSetChanged()
                }
                else if (gameRepo.state == player){
                    Log.d(TAG, "state not 0")
                    val i = squares[position]
                    when (i) {
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

                    //TODO: check if there is a ship on player two's board
                    //TODO: send player one's move to server
                }
            }
        }
        spaceView.spaceBtn.text = spaceTxt

        return spaceView
    }

}
