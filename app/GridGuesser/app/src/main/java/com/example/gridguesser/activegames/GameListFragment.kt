package com.example.gridguesser.activegames

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gridguesser.R
import com.example.gridguesser.database.Game

private const val TAG = "GridGuesser"

class GameListFragment : Fragment() {

    private lateinit var gameRecyclerView: RecyclerView
    private var adapter: GameAdapter? = GameAdapter(emptyList())

    private val teamListViewModel: GameListViewModel by lazy {
        ViewModelProviders.of(this).get(GameListViewModel::class.java)
    }

    interface Callbacks {
        fun onGameSelected(id: Int)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        Log.d(TAG, "GLF: Attached and Callback set")
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        Log.d(TAG, "GLF: Detached and Callback unset")
    }

    private inner class GameHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener{
        val gameTitle: TextView = itemView.findViewById(R.id.game_title)
        val teamATitle: TextView = itemView.findViewById(R.id.team_a)
        val teamAPTS: TextView = itemView.findViewById(R.id.team_a_pts)
        val teamBTitle: TextView = itemView.findViewById(R.id.team_b)
        val teamBPTS: TextView = itemView.findViewById(R.id.team_b_pts)
        val gameChangeIndicator: ImageView = itemView.findViewById(R.id.game_change_indicator)
        var thisGame: Game? = null;

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            thisGame?.let { callbacks?.onGameSelected(it.game_id) }
        }
    }

    private inner class GameAdapter(var games: List<Game>)
        : RecyclerView.Adapter<GameHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : GameHolder {
            val view = layoutInflater.inflate(R.layout.list_item_game, parent, false)
            return GameHolder(view)
        }
        override fun getItemCount() = games.size

        override fun onBindViewHolder(holder: GameHolder, position: Int) {
            val game = games[position]
            holder.apply {
                gameTitle.text = game.title
                teamATitle.text = game.red_team
                teamAPTS.text = "${game.red_hits}"
                teamBTitle.text = game.blue_team
                teamBPTS.text = "${game.blue_hits}"
                thisGame = game
                if(game.hasChanged == 0){
                    gameChangeIndicator.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_active_games, container, false)
        gameRecyclerView =
            view.findViewById(R.id.game_recycler_view) as RecyclerView
        gameRecyclerView.layoutManager = LinearLayoutManager(context)
        gameRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        teamListViewModel.gameListLiveData.observe(
            viewLifecycleOwner,
            Observer { games ->
                games?.let {
                    Log.i(TAG, "Got games ${games.size}")
                    updateUI(games)
                }
            })
    }

    private fun updateUI(games: List<Game>) {
        adapter = GameAdapter(games)
        gameRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): GameListFragment {
            return GameListFragment()
        }
    }
}