package pop.uz.mymusicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.adapters.MusicAdapter;
import pop.uz.mymusicplayer.databinding.ActivityPlayingNowBinding;
import pop.uz.mymusicplayer.model.Music;

import static pop.uz.mymusicplayer.music.MusicService.mPlayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, MusicAdapter.OnClickMusicListener {

    SeekBar songSeekBar;
    TextView currentDuration, allDuration, musicName, authorName;
    private List<Music> musicList = new ArrayList<>();
    Thread updateSeekBar;
    static MediaPlayer mediaPlayer;
    Music music = new Music();
    private ActivityPlayingNowBinding binding;
    private long currentSongLength;

    private int mPlayPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayingNowBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });


        songSeekBar = findViewById(R.id.seekBar);
        currentDuration = findViewById(R.id.current_duration);
        allDuration = findViewById(R.id.all_duration);
        musicName = findViewById(R.id.music_name);

        binding.playList.setOnClickListener(this);
        binding.pause.setOnClickListener(this);
        binding.next.setOnClickListener(this);
        binding.previous.setOnClickListener(this);


        updateSeekBar = new Thread() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        binding.musicName.setText(music.title);
    }

    @Override
    public void onClick(View v) {
        mediaPlayer = new MediaPlayer();
        switch (v.getId()) {
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    binding.pause.setBackgroundResource(R.drawable.icon_pause);
                    mediaPlayer.pause();
                } else {
                    binding.pause.setBackgroundResource(R.drawable.icon_play);
                    mediaPlayer.start();
                }
                break;
            case R.id.next:
                if (mPlayPosition <= mPlayList.size() && mPlayPosition >= 0) {
                    mPlayPosition++;
                }
                mediaPlayer.stop();
                mediaPlayer.start();
                break;
            case R.id.play_list:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

        }
    }

    @Override
    public void onMusicClick(Music music, int position) {

        prepareMusic(music);
    }

    private void prepareMusic(Music music){

        currentSongLength = music.getDuration();
        binding.musicName.setText(music.title);
        Uri uri = Uri.parse("_id");
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

}
