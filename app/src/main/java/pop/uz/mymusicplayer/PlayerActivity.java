package pop.uz.mymusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

import pop.uz.mymusicplayer.model.Music;

public class PlayerActivity extends AppCompatActivity {

    Button play_list, pause, next, previous;
    SeekBar songSeekBar;
    TextView currentDuration, allDuration, musicName;
    private ArrayList<Music> items = new ArrayList<>();
    private RecyclerView musicList;
    Thread updateSeekBar;
    static MediaPlayer mediaPlayer;
    String sName;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        play_list = findViewById(R.id.play_list);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        songSeekBar = findViewById(R.id.seekBar);
        currentDuration = findViewById(R.id.current_duration);
        allDuration = findViewById(R.id.all_duration);
        musicName = findViewById(R.id.music_name);

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        };

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        assert bundle != null;
        items = (ArrayList) bundle.getParcelableArrayList("songs");

        sName = items.get(position).getTitle().toString();

        String songName = intent.getStringExtra("songname");

        musicName.setText(songName);
        musicName.setSelected(true);

        position = bundle.getInt("pos", 0);

        final Uri uri = Uri.parse(items.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        songSeekBar.setMax(mediaPlayer.getDuration());

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekBar.setMax(mediaPlayer.getDuration());

                if (mediaPlayer.isPlaying()){
                    pause.setBackgroundResource(R.drawable.icon_play);
                    mediaPlayer.pause();
                }
                else {
                    pause.setBackgroundResource(R.drawable.icon_pause);
                    mediaPlayer.start();
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1)%items.size());

                Uri uri = Uri.parse(items.get(position).toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                sName = items.get(position).getTitle();
                musicName.setText(sName);

                mediaPlayer.start();
            }
        });
    }

}
