package pop.uz.mymusicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AudioDb extends SQLiteOpenHelper {

    private static final String DATABASENAME = "songs.db";
    private static final int VERSION = 1;

    public static AudioDb instance = null;
    private final Context context;

    public static AudioDb getInstance(Context context) {
        if (instance == null) {
            instance = new AudioDb(context);
        }
        return instance;
    }

    public AudioDb(@Nullable Context context) {
        super(context, DATABASENAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MusicPlayStatus.getInstance(context).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MusicPlayStatus.getInstance(context).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MusicPlayStatus.getInstance(context).onDowngrade(db, oldVersion, newVersion);
    }
}
