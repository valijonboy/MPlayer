package pop.uz.mymusicplayer.adapters;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.model.Music;
import pop.uz.mymusicplayer.utils.MPlayerUtils;

import static pop.uz.mymusicplayer.R.color.selectedColor;
import static pop.uz.mymusicplayer.music.PlayerServices.playAll;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    public static List<Music> musicList = new ArrayList<>();
    public OnClickMusicListener listener;
    private long[] mIds;
    private int selectedPosition;
    Context context;

    public MusicAdapter(List<Music> musicList, OnClickMusicListener listener) {
        this.musicList = musicList;
        mIds = getIds();
//        OnClickMusicListener onClickMusicListener = null;
        this.listener = listener;
    }

    private long[] getIds() {
        long[] result = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            result[i] = musicList.get(i).id;
        }
        return result;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView musicNameItem, artistNameItem;
        Button btnContextMenu;
        ImageView imageView;
//        OnClickMusicListener onClickMusicListener;

        public MusicViewHolder(@NonNull View itemView) {

            super(itemView);
            musicNameItem = itemView.findViewById(R.id.music_name_item);
            artistNameItem = itemView.findViewById(R.id.artist_name_item);
            btnContextMenu = itemView.findViewById(R.id.btn_context_menu);
            imageView = itemView.findViewById(R.id.image_playlist_item);
          //  this.onClickMusicListener = onClickMusicListener;

            itemView.setOnClickListener(this);
        }

        public void bind(final Music music, final OnClickMusicListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMusicClick(music, getLayoutPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        playAll(mIds, getAdapterPosition(), musicList.get(getAdapterPosition()).id, MPlayerUtils.IdType.NA);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 100);
          //  onClickMusicListener.onMusicClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_item, parent, false);
        return new MusicViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MusicViewHolder holder, int position) {
        Music music = musicList.get(position);

        if (selectedPosition == position){
            holder.itemView.setBackgroundColor( android.R.color.holo_orange_light);
        }
        holder.musicNameItem.setText(music.title);
        holder.artistNameItem.setText(music.artistName);
        ImageLoader.getInstance().displayImage(getImage(musicList.get(position).albumId).toString(), holder.imageView,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
        .resetViewBeforeLoading(true).build());

        holder.bind(music, listener);
    }

    public static Uri getImage(long albumId){
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    @Override
    public int getItemCount() {
        return musicList != null? musicList.size(): 0;
    }

    public interface OnClickMusicListener {
        void onMusicClick(Music music, int position);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}



