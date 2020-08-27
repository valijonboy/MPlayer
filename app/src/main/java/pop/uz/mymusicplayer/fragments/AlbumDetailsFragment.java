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
import pop.uz.mymusicplayer.adapters.AlbumMusicAdapter;
import pop.uz.mymusicplayer.dataloader.AlbumLoader;
import pop.uz.mymusicplayer.dataloader.AlbumMusicLoader;
import pop.uz.mymusicplayer.model.Albums;
import pop.uz.mymusicplayer.model.Music;

import static pop.uz.mymusicplayer.adapters.MusicAdapter.getImage;


public class AlbumDetailsFragment extends Fragment {

    private static final String TAG = "AlbumDetailsFragment";
    CollapsingToolbarLayout collapsingToolbarLayout;
    private long album_id;

    private List<Music> musicList = new ArrayList<>();
    private Albums album;
    private ImageView imageView, albArtImage;
    private TextView albArtistName, albumDetails;
    private RecyclerView recyclerView;
    AlbumMusicAdapter albumMusicAdapter;


    public static AlbumDetailsFragment newInstance(long id) {

        Bundle args = new Bundle();
        args.putLong("_ID", id);
        AlbumDetailsFragment fragment = new AlbumDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        album_id = getArguments().getLong("_ID");
        Log.v(TAG, "" + album_id);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_album_details, container, false);
        albArtistName = rootView.findViewById(R.id.alb_art_name);
        albumDetails = rootView.findViewById(R.id.albumDetails);
        albArtImage = rootView.findViewById(R.id.albart_img);
        imageView = rootView.findViewById(R.id.bigart);
        collapsingToolbarLayout = rootView.findViewById(R.id.collapsinglayout);
        recyclerView = rootView.findViewById(R.id.recycler_album_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        album = new AlbumLoader().getAlbum(getActivity(), album_id);
        setDetails();
        setUpAlbumList();
        return rootView;
    }

    private void setUpAlbumList() {
        musicList  = AlbumMusicLoader.getAllAlbumMusics(getActivity(), album_id);
        albumMusicAdapter = new AlbumMusicAdapter(getActivity(), musicList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(albumMusicAdapter);
    }

    private void setDetails() {
        collapsingToolbarLayout.setTitle(album.albumName);
        albArtistName.setText(album.albumName);
        albumDetails.setText(album.artistName);
        ImageLoader.getInstance().displayImage(getImage(album.id).toString(), imageView,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
                        .resetViewBeforeLoading(true).build());

        ImageLoader.getInstance().displayImage(getImage(album.id).toString(), albArtImage,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.ic_music)
                        .resetViewBeforeLoading(true).build());
    }
}