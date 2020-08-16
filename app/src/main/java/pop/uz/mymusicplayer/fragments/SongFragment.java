package pop.uz.mymusicplayer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pop.uz.mymusicplayer.adapters.MusicAdapter;
import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.dataloader.SongLoader;

public class SongFragment extends Fragment {
    private MusicAdapter musicAdapter;
    RecyclerView recyclerView;


    public SongFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        new LoadData().execute("");
        return view;
    }

    public class LoadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (getActivity() != null) {
                musicAdapter = new MusicAdapter(new SongLoader().getAllMusics(getActivity()));
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(musicAdapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
