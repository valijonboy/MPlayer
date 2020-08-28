package pop.uz.mymusicplayer.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import pop.uz.mymusicplayer.MyMusicAIDL;
import pop.uz.mymusicplayer.R;
import pop.uz.mymusicplayer.database.MusicPlayStatus;
import pop.uz.mymusicplayer.model.PlayBackTrack;
import pop.uz.mymusicplayer.utils.MPlayerUtils;


import static pop.uz.mymusicplayer.Constants.FADE_DOWN;
import static pop.uz.mymusicplayer.Constants.FADE_UP;
import static pop.uz.mymusicplayer.Constants.FOCUSE_CHANGE;
import static pop.uz.mymusicplayer.Constants.GO_TO_NEXT_TRACK;
import static pop.uz.mymusicplayer.Constants.NEXT_ACTION;
import static pop.uz.mymusicplayer.Constants.NOTIFICATION_ID;
import static pop.uz.mymusicplayer.Constants.NOTIFICATION_MODE_NON;
import static pop.uz.mymusicplayer.Constants.PAUSE_ACTION;
import static pop.uz.mymusicplayer.Constants.PLAY_ACTION;
import static pop.uz.mymusicplayer.Constants.PREVIOUS_ACTION;
import static pop.uz.mymusicplayer.Constants.SERVER_DIED;
import static pop.uz.mymusicplayer.Constants.STOP_ACTION;
import static pop.uz.mymusicplayer.Constants.TOGGLEPAUSE_ACTION;
import static pop.uz.mymusicplayer.adapters.MusicAdapter.musicList;
import static pop.uz.mymusicplayer.music.MediaStyleHelper.getActionIntent;


public class MusicService extends Service {

    public static final String TAG = "MusicService";
    public final IBinder I_BINDER = new MusicService.SubStub(this);
    private MusicPlayStatus mMusicPlayStatus;
    private int mPlayPosition = -1;
    private SharedPreferences preferences;
    private MyMedia mPlayer;
    private int mNotify = NOTIFICATION_MODE_NON;

    public static ArrayList<PlayBackTrack> mPlayList = new ArrayList<>(100);
    private boolean isSupposedToPlaying = false;
    private boolean mPausedByTransientLossOfFocus = false;
    private AudioManager mAudioManager;
    private MyPlayerHandler myPlayerHandler;
    private HandlerThread mHandlerThread;
    private int notifId;
    private MediaSessionCompat mSession;
    private NotificationManagerCompat mNotificationManager;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            commandHandler(intent);
        }
    };

    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            myPlayerHandler.obtainMessage(FOCUSE_CHANGE, focusChange, 0).sendToTarget();
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        mMusicPlayStatus.saveSongInDb(mPlayList);
        if (isSupposedToPlaying || mPausedByTransientLossOfFocus) {
            return true;
        }
        stopSelf();
        return true;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel();
        }

        mMusicPlayStatus = MusicPlayStatus.getInstance(this);
        mPlayList = mMusicPlayStatus.getMusicToDb();
        preferences = getSharedPreferences("musicservice", 0);

        mPlayPosition = preferences.getInt("pos", 0);
        mHandlerThread = new HandlerThread("MyPlayerHandler", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        myPlayerHandler = new MyPlayerHandler(mHandlerThread.getLooper(), this);

        mPlayer = new MyMedia(this);
        mPlayer.setHandler(myPlayerHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TOGGLEPAUSE_ACTION);
        filter.addAction(PLAY_ACTION);
        filter.addAction(PAUSE_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(PREVIOUS_ACTION);
        filter.addAction(STOP_ACTION);
        registerReceiver(receiver, filter);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setupMediaSession();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return I_BINDER;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            commandHandler(intent);
        }
        return START_NOT_STICKY;
    }

    private boolean isPlaying() {
        return isSupposedToPlaying;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mPlayer = null;
    }


    ////All method///

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, "MPlayer", NotificationManager.IMPORTANCE_LOW);
        manager.createNotificationChannel(channel);
    }

    private void commandHandler(Intent intent) {
        String action = intent.getAction();
        if (TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pause();
                mPausedByTransientLossOfFocus = false;
                mNotificationManager.notify(notifId, createNotification());
            } else {
                play();
            }
        } else if (PLAY_ACTION.equals(action)) {
            play();
        } else if (PAUSE_ACTION.equals(action)) {
            pause();
            mPausedByTransientLossOfFocus = false;
        } else if (NEXT_ACTION.equals(action)) {
            goToNext();
        } else if (PREVIOUS_ACTION.equals(action)) {
            previousTrack();
        }

    }

    private void setupMediaSession() {
        mSession = new MediaSessionCompat(this, NOTIFICATION_ID);

        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                play();
            }

            @Override
            public void onPause() {
                super.onPause();
                pause();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                goToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                previousTrack();
            }

            @Override
            public void onStop() {
                super.onStop();
                stop();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                mPlayer.seek(pos);
            }
        });
    }

    private void updateMediaSession() {
        int playPauseState = isSupposedToPlaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        mSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicList.get(mPlayPosition).title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicList.get(mPlayPosition).artistName)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getBitmap(this, musicList.get(mPlayPosition).albumId))
                .build());

        mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(playPauseState, position(), 1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .build());
    }

    private Notification createNotification() {
        int playPauseButton = isPlaying() ? R.drawable.icon_pause : R.drawable.icon_play;

        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mSession);

        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2, 3)
                .setMediaSession(mSession.getSessionToken()));

        builder.setSmallIcon(R.drawable.icon_music)
                .setColor(getResources().getColor(R.color.colorPrimary));

        builder.addAction(R.drawable.icon_previous, getString(R.string.previous), getActionIntent(this, PREVIOUS_ACTION))
                .addAction(playPauseButton, getString(R.string.previous), getActionIntent(this, TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.icon_next, getString(R.string.previous), getActionIntent(this, NEXT_ACTION));

        Notification notification = builder.build();
        return notification;
    }

    private void previousTrack() {
    }

    public void goToNext() {
        if (mPlayPosition <= mPlayList.size() && mPlayPosition >= 0) {
            mPlayPosition++;
        }
        stop();
        play();
    }

    private long position() {
        if (mPlayer.mIsInitializied) {
            return mPlayer.position();
        }
        return -1;
    }

    private void open(long[] list, int position, long sourceId, MPlayerUtils.IdType type) {

        synchronized (this) {
            int mLenght = list.length;
            boolean newList = true;
            if (mLenght == mPlayList.size()) {
                newList = false;
                Log.v(TAG, "open" + mPlayList.size());
                for (int i = 0; i < mLenght; i++) {
                    if (list[i] != mPlayList.get(i).mId) {
                        newList = true;
                        break;
                    }
                }
            }
            if (newList) {
                addToPlayList(list, -1, sourceId, type);
                mMusicPlayStatus.saveSongInDb(mPlayList);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("pos", position);
            }
            if (position >= 0) {
                mPlayPosition = position;
            }
        }
    }

    private void addToPlayList(long[] list, int position, long sourceId, MPlayerUtils.IdType type) {
        int addLenght = list.length;
        if (position < 0) {
            mPlayList.clear();
            position = 0;
        }
        mPlayList.ensureCapacity(mPlayList.size() + addLenght);
        if (position > mPlayList.size()) {
            position = mPlayList.size();
        }
        ArrayList<PlayBackTrack> mlist = new ArrayList<>(addLenght);

        for (int j = 0; j < addLenght; j++) {
            mlist.add(new PlayBackTrack(list[j], sourceId, type, j));
        }
        mPlayList.addAll(position, mlist);

    }

    private long getAudioId() {
        PlayBackTrack track = getCurrentTrack();
        if (track != null) {
            return track.mId;
        }
        return -1;
    }

    private PlayBackTrack getCurrentTrack() {
        return getTrack(mPlayPosition);
    }

    public synchronized PlayBackTrack getTrack(int index) {
        if (index != -1 && index < mPlayList.size()) {
            return mPlayList.get(index);
        }
        return null;
    }

    private int getQueuePosition() {
        synchronized (this) {
            return mPlayPosition;
        }
    }


    public long[] getSavedIdList() {
        synchronized (this) {
            int lenght = mPlayList.size();
            long[] idList = new long[lenght];
            for (int i = 0; i < lenght; i++) {
                idList[i] = mPlayList.get(i).mId;
            }
            return idList;
        }

    }

    public void pause() {
        if (isSupposedToPlaying) {
            mPlayer.pause();
            isSupposedToPlaying = false;
        }
    }

    public void play() {
        mPlayer.setDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + mPlayList.get(mPlayPosition).mId);
        int status = mAudioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }

        mSession.setActive(true);
        mPlayer.start();
        myPlayerHandler.removeMessages(FADE_DOWN);
        myPlayerHandler.sendEmptyMessage(FADE_UP);
        isSupposedToPlaying = true;
        mPausedByTransientLossOfFocus = true;

        updateMediaSession();
        notifId = hashCode();
        startForeground(notifId, createNotification());
    }

    private int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }

    public void stop() {
        if (mPlayer.mIsInitializied) {
            mPlayer.stop();
        }
    }

    public void release() {
        stop();
        mPlayer.release();
    }

    private Bitmap getBitmap(Context context, long id) {
        Bitmap albumArt = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id);
            ParcelFileDescriptor fileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (fileDescriptor != null) {
                FileDescriptor descriptor = fileDescriptor.getFileDescriptor();
                albumArt = BitmapFactory.decodeFileDescriptor(descriptor, null, options);
                fileDescriptor = null;
                descriptor = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (albumArt != null) {
            return albumArt;
        } else {
            return BitmapFactory.decodeResource(getResources(), R.drawable.icon_music);
        }
    }
    ////All method..........

    /*
           MediaPlayer functions...
     */

    public class MyMedia implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

        private WeakReference<MusicService> mService;
        private MediaPlayer mMediaPlayer = new MediaPlayer();
        private boolean mIsInitializied = false;
        private Handler mHandler;
        private float mVolume;

        public MyMedia(MusicService service) {
            this.mService = new WeakReference<>(service);
        }

        public void setDataSource(String path) {
            mIsInitializied = setDataPath(mMediaPlayer, path);
        }

        private boolean setDataPath(MediaPlayer mMediaPlayer, String path) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    mMediaPlayer.setDataSource(mService.get(), Uri.parse(path));
                } else {
                    mMediaPlayer.setDataSource(path);
                }
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnCompletionListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        public boolean mInitialized() {
            return mIsInitializied;
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        public void start() {
            mMediaPlayer.start();
        }

        public void stop() {
            mMediaPlayer.stop();
            mIsInitializied = false;
        }

        public void pause() {
            mMediaPlayer.pause();
        }

        public void release() {
            stop();
            mMediaPlayer.release();
        }

        public long duration() {
            if (mMediaPlayer != null && mInitialized()) {
                return mMediaPlayer.getDuration();
            }
            return -1;
        }

        public long position() {
            if (mMediaPlayer != null && mInitialized()) {
                return mMediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        public void setVolume(float vol) {
            mMediaPlayer.setVolume(vol, vol);
            mVolume = vol;
        }

        public long seek(long whereto) {
            mMediaPlayer.seekTo((int) whereto);
            return whereto;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mp == mMediaPlayer) {
                mHandler.sendEmptyMessage(GO_TO_NEXT_TRACK);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    mIsInitializied = false;
                    mp.release();
                    mp = new MediaPlayer();

                    Message message = mHandler.obtainMessage(SERVER_DIED);
                    mHandler.sendMessageDelayed(message, 2000);
                    break;
                default:
                    break;
            }
            return false;
        }

        public int getAudioSessionId() {
            return 0;
        }
    }

    /////////MediaPlayer////////////

    //**************** PlayerHandler*****************//

    public class MyPlayerHandler extends Handler {
        private WeakReference<MusicService> mService;
        private float mVolume = 1.0f;

        public MyPlayerHandler(@NonNull Looper looper, MusicService service) {
            super(looper);
            this.mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MusicService service = mService.get();
            if (service == null) {
                return;
            }
            synchronized (service) {
                switch (msg.what) {
                    case FADE_UP:
                        mVolume += 0.1f;
                        if (mVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADE_UP, 10);
                        } else {
                            mVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mVolume);
                        break;
                    case FADE_DOWN:
                        mVolume -= 0.5f;
                        if (mVolume < 0.2f) {
                            sendEmptyMessageDelayed(FADE_DOWN, 10);
                        } else {
                            mVolume = 0.2f;
                        }
                        service.mPlayer.setVolume(mVolume);
                        break;
                    case GO_TO_NEXT_TRACK:
                        goToNext();
                        break;
                    case FOCUSE_CHANGE:
                        switch (msg.arg1) {

                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                removeMessages(FADE_UP);
                                sendEmptyMessage(FADE_DOWN);
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                if (service.isSupposedToPlaying) {
                                    service.mPausedByTransientLossOfFocus = false;
                                }
                                service.pause();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (service.isSupposedToPlaying) {
                                    service.mPausedByTransientLossOfFocus = true;
                                }
                                service.pause();
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (!service.isSupposedToPlaying && service.mPausedByTransientLossOfFocus) {
                                    service.mPausedByTransientLossOfFocus = false;
                                    mVolume = 0.0f;
                                    service.mPlayer.setVolume(mVolume);
                                    service.play();
                                } else {
                                    removeMessages(FADE_DOWN);
                                    sendEmptyMessage(FADE_UP);
                                }
                                break;
                        }
                        break;
                }

            }
            super.handleMessage(msg);
        }
    }


    //>>>>>>>>>>>>>>>>PlayerHandler<<<<<<<<<<<<<<<<<<//

    private static final class SubStub extends MyMusicAIDL.Stub {
        private WeakReference<MusicService> mService;

        public SubStub(MusicService service) {
            this.mService = new WeakReference<>(service);
        }

        @Override
        public void open(long[] list, int position, long sourceId, int type) throws RemoteException {
            mService.get().open(list, position, sourceId, MPlayerUtils.IdType.getInstance(type));
        }

        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void stop() throws RemoteException {
            mService.get().stop();
        }

        @Override
        public long getAudioId() throws RemoteException {
            return mService.get().getAudioId();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mService.get().getQueuePosition();
        }

        @Override
        public long[] getSavedIdList() throws RemoteException {
            return mService.get().getSavedIdList();
        }


    }


}
