package pop.uz.mymusicplayer.adapters;

import android.content.ContentUris;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.model.Music;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList = new ArrayList<>();
    public OnClickMusicListener mOnClickMusicListener;

    public MusicAdapter(List<Music> musicList) {
        this.musicList = musicList;
      // this.mOnClickMusicListener = onClickMusicListener;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView musicNameItem, artistNameItem;
        Button btnContextMenu;
        ImageView imageView;
        OnClickMusicListener onClickMusicListener;

        public MusicViewHolder(@NonNull View itemView, OnClickMusicListener onClickMusicListener) {

            super(itemView);
            musicNameItem = itemView.findViewById(R.id.music_name_item);
            artistNameItem = itemView.findViewById(R.id.artist_name_item);
            btnContextMenu = itemView.findViewById(R.id.btn_context_menu);
            imageView = itemView.findViewById(R.id.image_playlist_item);
            this.onClickMusicListener = onClickMusicListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickMusicListener.onMusicClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_item, parent, false);
        return new MusicViewHolder(view, mOnClickMusicListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MusicViewHolder holder, int position) {

        holder.musicNameItem.setText(musicList.get(position).getTitle());
        holder.artistNameItem.setText(musicList.get(position).getArtistName());
        ImageLoader.getInstance().displayImage(getImage(musicList.get(position).albumId).toString(), holder.imageView,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
        .resetViewBeforeLoading(true).build());
    }

    private Uri getImage(long albumId){
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    @Override
    public int getItemCount() {
        return musicList != null? musicList.size(): 0;
    }

    public interface OnClickMusicListener {
        void onMusicClick(int position);
    }
}



