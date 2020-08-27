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

public class AlbumMusicAdapter extends RecyclerView.Adapter<AlbumMusicAdapter.AlbumMusicHolder> {

    private Activity context;
    private List<Music> musicList;

    public AlbumMusicAdapter(Activity context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public AlbumMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumMusicHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumMusicHolder holder, int position) {
        Music music = musicList.get(position);

        if (music != null) {
            holder.songTitle.setText(music.title);
            holder.txtDetails.setText(music.artistName);
        }
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public static class AlbumMusicHolder extends RecyclerView.ViewHolder {
        private TextView songTitle, txtDetails;

        public AlbumMusicHolder(@NonNull View itemView) {
            super(itemView);

            songTitle = itemView.findViewById(R.id.song_title);
            txtDetails = itemView.findViewById(R.id.txt_details);

        }
    }
}
