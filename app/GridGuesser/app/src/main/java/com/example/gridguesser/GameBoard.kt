package com.example.gridguesser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment

private const val TAG = "GridGuesser"


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        gridView = view.findViewById(R.id.gridview) as GridView
        gridView.adapter = SpaceAdapter(context, playerNames)

        return view
    }


}