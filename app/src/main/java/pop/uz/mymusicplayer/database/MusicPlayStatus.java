package pop.uz.mymusicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import pop.uz.mymusicplayer.model.PlayBackTrack;
import pop.uz.mymusicplayer.utils.MPlayerUtils;

public class MusicPlayStatus {

    // private static Object MusicPlayStatus;

    public static MusicPlayStatus instance = null;
    private AudioDb audioDb = null;
    private Context context;

    public MusicPlayStatus(Context context) {
        audioDb = AudioDb.getInstance(context);
    }

    public static MusicPlayStatus getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayStatus(context);
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(SongColumn.NAME);
        builder.append("(");
        builder.append(SongColumn.TRACK_ID);
        builder.append(" LONG NOT NULL,");
        builder.append(SongColumn.SOURCE_ID);
        builder.append(" LONG NOT NULL,");
        builder.append(SongColumn.SOURCE_TYPE);
        builder.append(" INT NOT NULL,");
        builder.append(SongColumn.SOURCE_POSITION);
        builder.append(" INT NOT NULL)");
        db.execSQL(builder.toString());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && newVersion >= 2) {
            onCreate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SongColumn.NAME);
    }

public void saveSongInDb(ArrayList<PlayBackTrack> list){
        SQLiteDatabase sqLiteDatabase = audioDb.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(SongColumn.NAME, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        }finally {
            sqLiteDatabase.endTransaction();
        }

        int PROCESS_NUM = 20;
        int position = 0;
        while (position < list.size()){

            sqLiteDatabase.beginTransaction();
            try {
                for (int i = position; i < list.size() && i < position + PROCESS_NUM; i++) {
                    PlayBackTrack track = list.get(i);
                    ContentValues values = new ContentValues(4);
                    values.put(SongColumn.TRACK_ID, track.mId);
                    values.put(SongColumn.SOURCE_ID, track.sourceId);
                    values.put(SongColumn.SOURCE_TYPE, track.mIdTtype.mId);
                    values.put(SongColumn.SOURCE_POSITION, track.mCurrentPosition);
                    sqLiteDatabase.insert(SongColumn.NAME, null, values);
            }
          sqLiteDatabase.setTransactionSuccessful();
            }finally {
                sqLiteDatabase.endTransaction();
                position += PROCESS_NUM;
            }
        }
    }

    public ArrayList<PlayBackTrack> getMusicToDb(){
        ArrayList<PlayBackTrack> result = new ArrayList<>();
        Cursor cursor = audioDb.getReadableDatabase().query(SongColumn.NAME, null, null,
                null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()){
                result.ensureCapacity(cursor.getCount());
            do {
                result.add(new PlayBackTrack(cursor.getLong(0), cursor.getLong(1),
                        MPlayerUtils.IdType.getInstance(cursor.getInt(2)), cursor.getInt(3)));
            }while (cursor.moveToNext());
            }
            return result;
        }finally {
            if (cursor != null){
                cursor.close();
                cursor = null;
            }
        }
    }
    private static class SongColumn {
        public static String NAME = "playbacktrack";
        public static String TRACK_ID = "trackid";
        public static String SOURCE_ID = "sourceid";
        public static String SOURCE_TYPE = "sourcetype";
        public static String SOURCE_POSITION = "sourceposition";
    }
}
