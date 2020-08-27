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
import pop.uz.mymusicplayer.adapters.ArtistAdapter;
import pop.uz.mymusicplayer.adapters.GridSpacingItemDecoration;
import pop.uz.mymusicplayer.dataloader.ArtistLoader;

public class ArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArtistAdapter adapter;
    int spanCount = 2;
    int spacing = 20;
    boolean includeEdge = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.artist_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));

        new LoadData().execute("");
        return view;
    }

    public class LoadData extends AsyncTask<String , Void, String >{

        @Override
        protected String doInBackground(String... strings) {
            if (getActivity() != null){
                adapter = new ArtistAdapter(getActivity(), new ArtistLoader().artistList(getActivity()));
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(adapter);
            if (getActivity() != null){
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount,spacing, includeEdge));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
