package pop.uz.mymusicplayer.model;

public class Albums {
    public final long id;
    public final String albumName;
    public final long artistId;
    public final String artistName;

    public Albums() {
        id = -1;
        albumName = "";
        artistId = -1;
        artistName = "";
    }

    public Albums(long id, String albumName, long artistId, String artistName) {
        this.id = id;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public long getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }
}
