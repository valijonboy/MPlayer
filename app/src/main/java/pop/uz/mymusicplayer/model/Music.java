package pop.uz.mymusicplayer.model;

public class Music {
    public final long id;
    public final long albumId;
    public final long artistId;
    public final String title;
    public final String artistName;
    public final String albumName;
    public  final int duration;

    public Music(){
        id = -1;
        title = "";
        albumId = -1;
        albumName = "";
        artistId = -1;
        artistName = "";
        duration = -1;
    }


    public Music(long id, String title, long albumId, String albumName, long artistId, String artistName,
                  int duration) {
        this.title = title;
        this.artistName = artistName;
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.albumName = albumName;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public int getDuration() {
        return duration;
    }
}
