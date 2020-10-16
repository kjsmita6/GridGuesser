package com.example.gridguesser

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import kotlin.system.measureTimeMillis

class ParseBoardTest {
    private lateinit var subject: ParseBoard

    @Before
    fun setUp() {
        subject = ParseBoard()

    }

    @Test
    fun parseBoardTest(){
        var testBoard = "x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2"
        var newBoard = subject.parseBoard(testBoard, true)
        val expectedBoard = mutableListOf(" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "1", "2", "2", "2", "2", "2")
        assertEquals(expectedBoard, newBoard)
    }

    @Test
    fun performParseBoard(){
        var testBoard = "x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2 x: 2, y: 4, value:2"
        var timeBefore = measureTimeMillis {
            subject.parseBoard(testBoard, true)
        }
        assertTrue(timeBefore < 100)
    }
}