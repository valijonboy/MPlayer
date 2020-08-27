package pop.uz.mymusicplayer.music;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import static pop.uz.mymusicplayer.Constants.NOTIFICATION_ID;

public class MediaStyleHelper {

    public static PendingIntent getActionIntent(Context context, String action){
        ComponentName componentName = new ComponentName(context, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(componentName);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    public static NotificationCompat.Builder from(Context context, MediaSessionCompat sessionCompat){
        MediaControllerCompat controllerCompat  = sessionCompat.getController();
        MediaMetadataCompat metadataCompat = controllerCompat.getMetadata();
        MediaDescriptionCompat mediaDescriptionCompat = metadataCompat.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_ID);
        builder.setContentTitle(mediaDescriptionCompat.getTitle())
                .setContentText(mediaDescriptionCompat.getSubtitle())
                .setSubText(mediaDescriptionCompat.getDescription())
                .setLargeIcon(mediaDescriptionCompat.getIconBitmap())
                .setContentIntent(controllerCompat.getSessionActivity())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        return builder;
    }
}
