// MyMusicAIDL.aidl
package pop.uz.mymusicplayer;

// Declare any non-default types here with import statements

interface MyMusicAIDL {

                         void open(in long[] list, int position, long sourceId, int type);
                         void play();
                         void stop();
                         long getAudioId();
                         int getCurrentPosition();
                         long[] getSavedIdList();
}
