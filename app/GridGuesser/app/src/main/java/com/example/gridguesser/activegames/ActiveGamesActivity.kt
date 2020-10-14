package com.example.gridguesser.activegames

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.gridguesser.GameActivity
import com.example.gridguesser.R

private const val TAG = "GridGuesser"
class ActiveGamesActivity : AppCompatActivity(), GameListFragment.Callbacks {
    override fun onGameSelected(id: Int) {
        val intent = GameActivity.newIntent(this, id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_games)
        Log.d(TAG, "BasketballCounter instance created")

        var currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_a)
        if (currentFragment == null) {
            val fragment =
                GameListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_a, fragment)
                .commit()
        }
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, ActiveGamesActivity::class.java)
        }
    }
}