package pop.uz.mymusicplayer.model;

import pop.uz.mymusicplayer.utils.MPlayerUtils;

public class PlayBackTrack {

    public long mId;
    public long sourceId;
    public MPlayerUtils.IdType mIdTtype;
    public int mCurrentPosition;

    public PlayBackTrack(long mId, long sourceId, MPlayerUtils.IdType mIdTtype, int mCurrentPosition) {
        this.mId = mId;
        this.sourceId = sourceId;
        this.mIdTtype = mIdTtype;
        this.mCurrentPosition = mCurrentPosition;
    }
}
