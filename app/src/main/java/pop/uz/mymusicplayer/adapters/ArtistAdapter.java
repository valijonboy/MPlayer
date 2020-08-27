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
import pop.uz.mymusicplayer.fragments.ArtistDetailsFragment;
import pop.uz.mymusicplayer.model.Artist;

import static pop.uz.mymusicplayer.adapters.MusicAdapter.getImage;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    private Context context;
    private List<Artist> artistList;

    public ArtistAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {

        Artist artist = artistList.get(position);
        if (artist != null){
            holder.artistName.setText(artist.artistName);
            ImageLoader.getInstance().displayImage(getImage(artistList.get(position).id).toString(), holder.artistImage,
                    new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
                            .resetViewBeforeLoading(true).build());
        }
    }

    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size(): 0;
    }

    public class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView artistImage;
        private TextView artistName;
        public ArtistHolder(@NonNull View itemView) {
            super(itemView);

            artistImage = itemView.findViewById(R.id.artthum);
            artistName = itemView.findViewById(R.id.artist_name_list);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            long artistId = artistList.get(getAdapterPosition()).id;

            FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment;

            transaction.setCustomAnimations(R.anim.layout_fad_in, R.anim.layout_fad_out,
                    R.anim.layout_fad_in, R.anim.layout_fad_out);

            fragment = ArtistDetailsFragment.newInstance(artistId);
            transaction.hide(((AppCompatActivity)context).getSupportFragmentManager()
                    .findFragmentById(R.id.main_container));

            transaction.add(R.id.main_container, fragment);
            transaction.addToBackStack(null).commit();
        }
    }
}
