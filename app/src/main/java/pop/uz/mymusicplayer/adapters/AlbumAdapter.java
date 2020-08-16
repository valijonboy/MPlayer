package pop.uz.mymusicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.model.Albums;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    private Context context;
    private List<Albums> albumsList;

    public AlbumAdapter(Context context, List<Albums> albumsList) {
        this.context = context;
        this.albumsList = albumsList;
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_grid, parent, false);
        return new AlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {

        Albums albums = albumsList.get(position);

        if (albums != null){
            holder.albumName.setText(albums.albumName);
            holder.artistName.setText(albums.artistName);
        }
    }

    @Override
    public int getItemCount() {
        return albumsList != null ? albumsList.size(): 0;
    }

    public class AlbumHolder extends RecyclerView.ViewHolder {
        private ImageView imageAlbum;
        private TextView albumName, artistName;
        public AlbumHolder(@NonNull View itemView) {
            super(itemView);

            imageAlbum = itemView.findViewById(R.id.image_album);
            albumName = itemView.findViewById(R.id.album_name);
            artistName = itemView.findViewById(R.id.artist_name);
        }
    }
}
