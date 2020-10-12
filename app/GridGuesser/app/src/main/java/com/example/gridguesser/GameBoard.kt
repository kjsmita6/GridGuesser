package com.example.gridguesser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import androidx.fragment.app.Fragment

private const val TAG = "GameBoardFragment"


class GameBoard : Fragment() {
    private lateinit var gridView: GridView
    private var playerNames = arrayOf(
        " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "1", "", "", "3", "", "", "", "", "", "", "",
        "2", "", "", "3", "", "", "", "", "", "", "",
        "3", "", "", "3", "", "", "", "", "", "", "",
        "4", "", "", "3", "", "", "", "", "", "", "",
        "5", "", "", "3", "", "", "", "", "", "", "",
        "6", "", "", "3", "", "", "", "", "", "", "",
        "7", "", "", "3", "", "", "", "", "", "", "",
        "8", "", "", "3", "", "", "", "", "", "", "",
        "9", "", "", "3", "", "", "", "", "", "", "",
        "10", "", "", "3", "", "", "", "", "", "", ""

    )

    private var buttons = arrayOf<Button>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        playerNames.forEach {
            var newBtn = Button(context)
            newBtn.text = it
            buttons.plus(newBtn)
        }

        gridView = view.findViewById(R.id.gridview) as GridView
        //val adapter = SpaceAdapter(context, playerNames)
        //gridView.adapter = adapter
        //adapter.notifyDataSetChanged()
        //gridView.invalidateViews()
        return view
    }


}