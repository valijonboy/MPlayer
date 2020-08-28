package pop.uz.mymusicplayer.adapters;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentTransaction;
        import androidx.recyclerview.widget.RecyclerView;

        import com.nostra13.universalimageloader.core.DisplayImageOptions;
        import com.nostra13.universalimageloader.core.ImageLoader;

        import java.util.List;

        import pop.uz.mymusicplayer.R;
        import pop.uz.mymusicplayer.fragments.AlbumDetailsFragment;
        import pop.uz.mymusicplayer.model.Albums;

        import static pop.uz.mymusicplayer.adapters.MusicAdapter.getImage;

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
            ImageLoader.getInstance().displayImage(getImage(albumsList.get(position).id).toString(), holder.imageAlbum,
                    new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.icon_music)
                            .resetViewBeforeLoading(true).build());
        }
    }

    @Override
    public int getItemCount() {
        return albumsList != null ? albumsList.size(): 0;
    }

    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageAlbum;
        private TextView albumName, artistName;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);

            imageAlbum = itemView.findViewById(R.id.image_album);
            albumName = itemView.findViewById(R.id.album_name);
            artistName = itemView.findViewById(R.id.artist_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            long albumId = albumsList.get(getAdapterPosition()).id;

            FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment;

            transaction.setCustomAnimations(R.anim.layout_fad_in, R.anim.layout_fad_out,
                    R.anim.layout_fad_in, R.anim.layout_fad_out);

            fragment = AlbumDetailsFragment.newInstance(albumId);
           transaction.hide(((AppCompatActivity)context).getSupportFragmentManager()
                    .findFragmentById(R.id.main_container));

            transaction.add(R.id.main_container, fragment);
            transaction.addToBackStack(null).commit();
        }
    }
}
