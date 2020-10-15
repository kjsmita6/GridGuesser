package com.example.gridguesser

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.http.ServerInteractions
import kotlinx.android.synthetic.main.square.view.*
import java.util.Observer

private const val TAG = "GridGuesser"

class SpaceAdapter (
    private val context: Context?,
    private val squares: MutableList<String>,
    private val player: Int
) : BaseAdapter() {

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
                    val colorValue = ContextCompat.getColor(context, R.color.colorDeepBlue)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                    spaceTxt = ""
                } else if (value == "1") {
                    val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                    spaceTxt = ""
                } else if (value == "2") {
                    val colorValue = ContextCompat.getColor(context, R.color.colorDeepBlue)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                    spaceTxt = "X"
                } else if (value == "3") {
                    val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                    spaceTxt = "X"
                }
                else {
                    val colorValue = ContextCompat.getColor(context, R.color.colorDeepBlue)
                    spaceView.spaceBtn.setBackgroundColor(colorValue)
                }

            spaceView.spaceBtn.setOnClickListener {
                Log.d("SpaceAdapter", "button pressed")
                if (gameRepo.state == 0) {
                    Log.d(TAG, "state 0")
                    squares[position] = "1"
                    gameRepo.remainingShips.value = gameRepo.remainingShips.value?.plus(1)
                    notifyDataSetChanged()
                }
                else if (gameRepo.state == 1){
                    Log.d(TAG, "state not 0")
                    for (i in squares){
                        if (i == "0"){
                            //MISS! send move and change color to miss
                            Log.d(TAG, "MISS!")
                            //Somehow call game activity move?
                        } else if (i == "1") {
                            //HIT! send move and change color to ship hit
                            Log.d(TAG, "HIT!")
                            //somehow call game activity move?
                        } else if (i == "2") {
                            //Cannot move here!
                        } else if (i == "3") {
                            //Cannot move here!
                        } else {
                        }
                    }
                    //TODO: check if there is a ship on player two's board
                    //TODO: send player one's move to server
                }
                else {
                    for (i in squares){

                    }
                    Log.d(TAG, "not 1")
                    //TODO: check if there is a ship on player one's board
                    //TODO: send player two's move to server
                }
            }
        }
        spaceView.spaceBtn.text = spaceTxt

        return spaceView
    }

}
