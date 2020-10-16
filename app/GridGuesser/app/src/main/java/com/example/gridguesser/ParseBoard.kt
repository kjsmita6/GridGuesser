package com.example.gridguesser

class ParseBoard {

    //change server board style to be the gridview style
    fun parseBoard(board: String, isThisPlayer: Boolean): MutableList<String>{
        var toReturn: MutableList<String> = mutableListOf(" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "1")
        val splitBoard = board.split(":")
        var row = 2
        for(i in 1 until splitBoard.size){
            if(i % 3 == 0){
                var value = splitBoard[i][0].toString()
                if(!isThisPlayer && value=="1"){ //don't show opponents ships
                    value = "0"
                }
                toReturn.add(value)
            }

            if(i % 30 == 0 && row < 11){
                toReturn.add(row.toString())
                row++
            }
        }
        return toReturn
    }
}