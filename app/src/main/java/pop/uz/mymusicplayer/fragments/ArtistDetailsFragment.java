package pop.uz.mymusicplayer.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.adapters.ArtistMusicAdapter;
import pop.uz.mymusicplayer.dataloader.ArtistLoader;
import pop.uz.mymusicplayer.dataloader.ArtistMusicLoader;
import pop.uz.mymusicplayer.model.Artist;
import pop.uz.mymusicplayer.model.Music;

import static pop.uz.mymusicplayer.adapters.MusicAdapter.getImage;

public class ArtistDetailsFragment extends Fragment {

    private static final String TAG = "ArtistDetailsFragment";
    private long artist_id;
    private List<Music> musicList = new ArrayList<>();
    private Artist artist;
    private ImageView bigArtist, artistArtImg;
    private TextView artistArtName, artistADetails;
    private RecyclerView recyclerView;
    CollapsingToolbarLayout toolbarLayout;
    ArtistMusicAdapter artistMusicAdapter;

    public static ArtistDetailsFragment newInstance(long id) {

        Bundle args = new Bundle();
        args.putLong("_ID", id);
        ArtistDetailsFragment fragment = new ArtistDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        artist_id = getArguments().getLong("_ID");
        Log.v(TAG, "" + artist_id);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);

      artistArtName = rootView.findViewById(R.id.artist_art_name);
      artistADetails = rootView.findViewById(R.id.artistDetails);
      bigArtist = rootView.findViewById(R.id.bigartist);
      artistArtImg = rootView.findViewById(R.id.artist_art_img);
      recyclerView = rootView.findViewById(R.id.recycler_artist_music);
      toolbarLayout = rootView.findViewById(R.id.artist_collapsinglayout);
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

      artist = new ArtistLoader().getArtist(getActivity(), artist_id);
        setArtistDetails();
        setUpArtistList();
        return rootView;
    }

    private void setUpArtistList() {
        musicList = ArtistMusicLoader.getAllArtistMusics(getActivity(), artist_id);
        artistMusicAdapter = new ArtistMusicAdapter(getActivity(), musicList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(artistMusicAdapter);
    }

    private void setArtistDetails() {

        toolbarLayout.setTitle(artist.artistName);
        artistArtName.setText(artist.artistName);
        artistADetails.setText(artist.artistName);
        ImageLoader.getInstance().displayImage(getImage(artist.id).toString(), bigArtist,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
                        .resetViewBeforeLoading(true).build());

        ImageLoader.getInstance().displayImage(getImage(artist.id).toString(), artistArtImg,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
                        .resetViewBeforeLoading(true).build());
    }
}