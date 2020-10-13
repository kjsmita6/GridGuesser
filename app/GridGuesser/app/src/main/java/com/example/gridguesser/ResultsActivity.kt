package com.example.gridguesser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

private const val TAG = "GridGuesser"
private const val WINNER = "IS_WINNER"
class ResultsActivity : AppCompatActivity() {
    private lateinit var bigResults: TextView
    private lateinit var littleResults: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        findViewById<Button>(R.id.to_mm).setOnClickListener {
            toMainMenu()
        }

        bigResults = findViewById(R.id.big_results)
        littleResults = findViewById(R.id.little_results)

        val intent = intent
        val isWinner = intent.getBooleanExtra(WINNER, false)
        if(isWinner){
            bigResults.text = getString(R.string.congratulations)
            littleResults.text = getString(R.string.winner)
        } else {
            bigResults.text = getString(R.string.sorry)
            littleResults.text = getString(R.string.loser)
        }
    }

    private fun toMainMenu(){
        val intent = MainActivity.newIntent(this@ResultsActivity)
        startActivity(intent)
    }

    companion object{
        fun newIntent(packageContext: Context, isWinner: Boolean): Intent {
            return Intent(packageContext, ResultsActivity::class.java).apply {
                putExtra(WINNER, isWinner)
            }
        }
    }
}