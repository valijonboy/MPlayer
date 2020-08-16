package pop.uz.mymusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.adapters.MusicAdapter;
import pop.uz.mymusicplayer.databinding.ActivityMain1Binding;
import pop.uz.mymusicplayer.fragments.AlbumFragment;
import pop.uz.mymusicplayer.fragments.ArtistFragment;
import pop.uz.mymusicplayer.fragments.SongFragment;
import pop.uz.mymusicplayer.fragments.SoundCloud;
import pop.uz.mymusicplayer.model.Music;

public class MainActivity extends AppCompatActivity  {

    private static final int MY_PERMISSION = 1;
    private ActivityMain1Binding binding;
    private MusicAdapter mMusicAdapter;
    private ArrayList<Music> mMusicList = new ArrayList<>();
    Context context;
    MediaPlayer mediaPlayer = new MediaPlayer();



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
            return;
        }else {
            UiInitialization();
        }

       UiInitialization();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    UiInitialization();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void UiInitialization(){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        setSupportActionBar(binding.toolbar);
        setUpViewPager(binding.viewPager);
        binding.tablayout.setupWithViewPager(binding.viewPager);
    }

  private void setUpViewPager(ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.AddFragments(new SongFragment(), "Songs");
        adapter.AddFragments(new AlbumFragment(), "Albums");
        adapter.AddFragments(new ArtistFragment(), "Artists");
        adapter.AddFragments(new SoundCloud(), "Sound Cloud");
        viewPager.setAdapter(adapter);
  }

    private class FragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titleList = new ArrayList<>();

        public FragmentAdapter(@NonNull FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }


        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void AddFragments(Fragment fragment, String title){
            fragmentList.add(fragment);
            titleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}







