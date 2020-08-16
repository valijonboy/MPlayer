package pop.uz.mymusicplayer.model;

public class Albums {
    public final long id;
    public final String albumName;
    public final long artistId;
    public final String artistName;
    public final int numSongs;
    public final int minyear;

    public Albums() {
        id = -1;
        albumName = "";
        artistId = -1;
        artistName = "";
        numSongs = -1;
        minyear = -1;
    }

    public Albums(long id, String albumName, long artistId, String artistName, int numSongs, int minyear) {
        this.id = id;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
        this.numSongs = numSongs;
        this.minyear = minyear;
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

    public int getNumSongs() {
        return numSongs;
    }

    public int getMinyear() {
        return minyear;
    }
}
