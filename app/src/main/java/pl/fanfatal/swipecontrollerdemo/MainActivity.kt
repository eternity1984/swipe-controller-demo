package pl.fanfatal.swipecontrollerdemo;

import android.graphics.Canvas
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class MainActivity: AppCompatActivity() {

    private lateinit var mAdapter: PlayersDataAdapter;
    private lateinit var swipeController: SwipeController;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        setPlayersDataAdapter();
        setupRecyclerView();
    }

    private fun setPlayersDataAdapter() {
        val players = ArrayList<Player>()
        try {
            InputStreamReader(assets.open("players.csv")).use { isr ->
                BufferedReader(isr).use { reader ->
                    reader.readLine();

                    reader.forEachLine { line ->
                        line.split(",").let {
                            players.add(Player(it[0], it[1], it[4], it[9].toInt(), it[14].toInt()));
                        }
                    }
                }
            }
        } catch (e: IOException) {

        }
        mAdapter = PlayersDataAdapter(players);
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false);
            adapter = mAdapter;
        }

        swipeController = SwipeController(object: SwipeControllerActions() {
            override fun onRightClicked(position: Int) {
                mAdapter.players.removeAt(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.itemCount);
            }
        });

        ItemTouchHelper(swipeController).attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c);
            }
        });
    }
}
