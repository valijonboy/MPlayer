package pop.uz.mymusicplayer.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.model.Music;

public class AlbumMusicLoader {

    public static List<Music> getAllAlbumMusics(Context context, long _id) {
        List<Music> albumMusicList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] project = new String[]{
                "_id",  //0
                "title", //1
                "album_id", //2
                "album", //3
                "artist_id", //4
                "artist", //5
                "duration" //6

        };
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        String selection = "is_music=1 and title !='' and album_id =" + _id;
        Cursor cursor = context.getContentResolver().query(uri, project, selection, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                albumMusicList.add(new Music(cursor.getLong(0), cursor.getString(1), cursor.getLong(2), cursor.getString(3),
                        cursor.getLong(4), cursor.getString(5), cursor.getInt(6)));

            } while (cursor.moveToNext());

            if (cursor != null) {
                cursor.close();
            }
        }
        return albumMusicList;
    }
}
