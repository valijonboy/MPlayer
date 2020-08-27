package pop.uz.mymusicplayer.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.model.Albums;
import pop.uz.mymusicplayer.model.Artist;

public class ArtistLoader {

    public List<Artist> getArtist(Cursor cursor){
        List<Artist> list = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()){
            do {
                list.add(new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2),
                        cursor.getInt(3)));
            }while (cursor.moveToNext());
            if (cursor != null){
                cursor.close();
            }
        }
        return list;
    }

    public Artist getArtist(Context context, long id){
        return artist(makeCursor(context, "_id=?", new String[]{String.valueOf(id)}));
    }

    private Artist artist(Cursor cursor) {
       Artist artist = new Artist();
        if (cursor.moveToFirst() && cursor != null) {
            artist = new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3));
        }
        if (cursor != null) {
            cursor.close();
        }
        return artist;
    }

    public List<Artist> artistList(Context context){
        return getArtist(makeCursor(context, null, null));
    }

    public Cursor makeCursor(Context context, String selection, String[] selectionArg){
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                "_id",
                "artist",
                "number_of_albums",
                "number_of_tracks"
        };
        String sortOrder = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArg, sortOrder);
        return cursor;
    }
}
