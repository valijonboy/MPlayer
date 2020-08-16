package pop.uz.mymusicplayer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.adapters.AlbumAdapter;
import pop.uz.mymusicplayer.adapters.GridSpacingItemDecoration;
import pop.uz.mymusicplayer.adapters.MusicAdapter;
import pop.uz.mymusicplayer.dataloader.AlbumLoader;
import pop.uz.mymusicplayer.dataloader.SongLoader;

public class AlbumFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    int spanCount = 2;
    int spacing = 20;
    boolean includeEdge = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.album_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));

        new LoadData().execute("");
        return view;
    }

    public class LoadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (getActivity() != null) {
                albumAdapter = new AlbumAdapter(getActivity(), new AlbumLoader().albumsList(getActivity()));
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(albumAdapter);
            if (getActivity() != null) {
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
