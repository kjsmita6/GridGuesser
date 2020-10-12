package com.example.gridguesser.activegames

import androidx.lifecycle.ViewModel
import com.example.gridguesser.database.GameRepository

class GameListViewModel: ViewModel() {
    private val gameRepository = GameRepository.get()
    val gameListLiveData = gameRepository.getGames()
}