package com.example.gridguesser

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.square.view.*

class SpaceAdapter (
    private val context: Context?,
    private val squares: Array<String>
) : BaseAdapter() {
    val squareList = squares

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

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var spaceView = inflator.inflate(R.layout.square, null)
        var spaceTxt = value

        if (value == "3"){
            spaceTxt = "X"
            val colorValue = ContextCompat.getColor(context, R.color.colorAccent)
            spaceView.spaceBtn.setBackgroundColor(colorValue)
        }

        spaceView.spaceBtn.text = spaceTxt
        spaceView.spaceBtn.setOnClickListener {
            Log.d("SpaceAdapter","button pressed")
        }
        //foodView.textView.text = food!!
        R.layout.square
        return spaceView
    }

}
