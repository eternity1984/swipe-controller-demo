package pl.fanfatal.swipecontrollerdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class PlayersDataAdapter(
        val players: ArrayList<Player>
): RecyclerView.Adapter<PlayersDataAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name);
        val nationality: TextView = view.findViewById(R.id.nationality);
        val club: TextView = view.findViewById(R.id.club);
        val rating: TextView = view.findViewById(R.id.rating);
        val age: TextView = view.findViewById(R.id.age);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_row, parent, false);

        return PlayerViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        players[position].let {
            holder.name.text = it.name;
            holder.nationality.text = it.nationality;
            holder.club.text = it.club;
            holder.rating.text = it.rating.toString();
            holder.age.text = it.age.toString();
        };
    }

    override fun getItemCount(): Int = players.size
}
