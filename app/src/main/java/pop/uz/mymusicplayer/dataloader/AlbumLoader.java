package pop.uz.mymusicplayer.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import pop.uz.mymusicplayer.model.Albums;

public class AlbumLoader {

    public List<Albums> getAlbums(Context context, Cursor cursor){
        List<Albums> list = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()){
            do {
                list.add(new Albums(cursor.getLong(0), cursor.getString(1), cursor.getLong(2),
                        cursor.getString(3), cursor.getInt(4), cursor.getInt(5)));
            }while (cursor.moveToNext());
            if (cursor != null){
                cursor.close();
            }
        }
        return list;
    }

    public Albums getAlbum(Context context, long id){
        return album(makeCursor(context, "_id=?", new String[]{String.valueOf(id)}));
    }

    private Albums album(Cursor cursor) {
        Albums albums  = new Albums();
        if (cursor.moveToFirst() && cursor != null) {
                albums = new Albums(cursor.getLong(0), cursor.getString(1), cursor.getLong(2),
                        cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
        }
        if (cursor != null) {
            cursor.close();
        }
        return albums;
    }

    public List<Albums> albumsList(Context context){
        return getAlbums(context, makeCursor(context, null, null));
    }

    public Cursor makeCursor(Context context, String selection, String[] selectionArg){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                "_id",
                "album",
                "artist_id",
                "artist",
                "numsongs",
                "minyear"
        };
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArg, sortOrder);
        return cursor;
    }
}
