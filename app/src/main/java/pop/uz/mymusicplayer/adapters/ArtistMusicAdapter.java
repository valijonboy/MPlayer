package pop.uz.mymusicplayer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.model.Music;

public class ArtistMusicAdapter extends RecyclerView.Adapter<ArtistMusicAdapter.ArtistMusicHolder>{

    private Activity context;
    private List<Music> musicList;

    public ArtistMusicAdapter(Activity context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public ArtistMusicAdapter.ArtistMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistMusicHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistMusicAdapter.ArtistMusicHolder holder, int position) {
        Music music = musicList.get(position);

        if (music !=  null){
            holder.artSongTitle.setText(music.title);
            holder.artTxtDetails.setText(music.artistName);
        }
    }

    @Override
    public int getItemCount() {
        return musicList != null? musicList.size():0;
    }

    public static class ArtistMusicHolder extends RecyclerView.ViewHolder {
        private TextView artSongTitle, artTxtDetails;
        public ArtistMusicHolder(@NonNull View itemView) {
            super(itemView);

            artSongTitle = itemView.findViewById(R.id.artist_song_title);
            artTxtDetails = itemView.findViewById(R.id.artist_txt_details);
        }
    }
}
