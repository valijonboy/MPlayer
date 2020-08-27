package pop.uz.mymusicplayer.music;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Arrays;
import java.util.WeakHashMap;

import pop.uz.mymusicplayer.MyMusicAIDL;
import pop.uz.mymusicplayer.utils.MPlayerUtils;

public class PlayerServices {
    public static MyMusicAIDL mRemot = null;
    private static final WeakHashMap<Context, ServiceBinder> mHashMap;
    private static long[] emptyList = null;

    static {
        mHashMap = new WeakHashMap<>();
    }

    public static final ServiceToken bindToService(Context context, ServiceConnection serviceConnection){
        Activity realActivity = ((Activity)context).getParent();
        if (realActivity == null){
            realActivity = (Activity) context;
        }
        ContextWrapper mWrapper = new ContextWrapper(realActivity);
        mWrapper.startService(new Intent(mWrapper, MusicService.class));
        ServiceBinder binder = new ServiceBinder(serviceConnection, mWrapper.getApplicationContext());

        if (mWrapper.bindService(new Intent().setClass(mWrapper, MusicService.class), binder, 0)){
            mHashMap.put(mWrapper, binder);
            return new ServiceToken(mWrapper);
        }
        return null;
    }

    public static void unBindToService(ServiceToken token){
        if (token ==  null){
            return;
        }
        ContextWrapper mWrapper = token.contextWrapper;
        ServiceBinder binder = mHashMap.remove(mWrapper);
        if (binder == null){
            return;
        }
        mWrapper.unbindService(binder);
        if (mHashMap.isEmpty()){
            binder = null;
        }
    }

    //..........All method.............

    public static void playAll(long[] list, int position, long sourceId, MPlayerUtils.IdType type) throws RemoteException {

        if (list.length == 0 && list == null && mRemot == null){
            return;
        }

        long audioId  = getAudioId();
        int currentPosition = getCurrentPosition();

        if (position == currentPosition && audioId == list[position] && position != -1){
            long[]idList = getSaveIdList();
            if (Arrays.equals(idList, list)){
                play();
                return;
            }
        }
        if (position < 0){
            position = 0;
        }
        mRemot.open(list, position, sourceId, type.mId);
        play();
    }

    private static long[] getSaveIdList() throws RemoteException {
        if (mRemot != null){
            mRemot.getSavedIdList();
        }
        return emptyList;
    }

    private static void play() throws RemoteException {
        if (mRemot != null){
            mRemot.play();
        }
    }



    private static int getCurrentPosition() throws RemoteException {
        if (mRemot != null){
            return mRemot.getCurrentPosition();
        }
        return -1;
    }

    private static long getAudioId() throws RemoteException {
        if (mRemot != null){
            return mRemot.getAudioId();
        }
        return -1;
    }
    //..........All method.............end...............


    public static class ServiceToken{
        private ContextWrapper contextWrapper;

        public ServiceToken(ContextWrapper contextWrapper) {
            this.contextWrapper = contextWrapper;
        }
    }
    public static final class ServiceBinder implements ServiceConnection{

        private ServiceConnection mService;
        private Context context;

        public ServiceBinder(ServiceConnection mService, Context context) {
            this.mService = mService;
            this.context = context;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemot = MyMusicAIDL.Stub.asInterface(service);
            if (mService != null){
                mService.onServiceConnected(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mService != null) {
                mService.onServiceDisconnected(name);
            }
            mRemot = null;
        }
    }
}
