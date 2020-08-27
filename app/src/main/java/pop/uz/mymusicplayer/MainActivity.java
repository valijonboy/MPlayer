package pop.uz.mymusicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import pop.uz.mymusicplayer.adapters.MusicAdapter;
import pop.uz.mymusicplayer.databinding.ActivityMain1Binding;
import pop.uz.mymusicplayer.fragments.FragmentMain;
import pop.uz.mymusicplayer.model.Music;
import pop.uz.mymusicplayer.music.PlayerServices;

import static pop.uz.mymusicplayer.music.PlayerServices.mRemot;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final int MY_PERMISSION = 1;
    private ActivityMain1Binding binding;
    private MusicAdapter mMusicAdapter;
    private ArrayList<Music> mMusicList = new ArrayList<>();
    Context context;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private SlidingUpPanelLayout sliding;
    private PlayerServices.ServiceToken token;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
            return;
        } else {
            UiInitialization();
        }

        UiInitialization();

        binding.btnToPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UiInitialization();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void UiInitialization() {

        token = PlayerServices.bindToService(this, this);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
       // sliding = findViewById(R.id.sliding_layout);

        Fragment fragment = new FragmentMain();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.commit();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (token != null){
            PlayerServices.unBindToService(token);
            token = null;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mRemot = MyMusicAIDL.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mRemot = null;
    }
}







