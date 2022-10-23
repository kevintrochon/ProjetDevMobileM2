package nc.unc.ktrochon.festivalnotification.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import nc.unc.ktrochon.festivalnotification.DescriptionDuConcertActivity;
import nc.unc.ktrochon.festivalnotification.MainActivity;
import nc.unc.ktrochon.festivalnotification.R;
import nc.unc.ktrochon.festivalnotification.entity.DetailsDuConcert;

public class ConcertNotification extends ContextWrapper {

    private NotificationManager notificationManager;

    private static final String CHANNEL_HIGH_ID = "nc.unc.ktrochon.festivalnotification.HIGH_CHANNEL";
    private static final String CHANNEL_HIGH_NAME = "High Channel";

    private static final String CHANNEL_DEFAULT_ID = "nc.unc.ktrochon.festivalnotification.DEFAULT_CHANNEL";
    private static final String CHANNEL_DEFAULT_NAME = "Default Channel";

    public ConcertNotification(Context base) {
        super(base);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long [] swPattern = new long[] { 0, 500, 110, 500, 110, 450, 110, 200, 110,
                170, 40, 450, 110, 200, 110, 170, 40, 500 };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannelHigh = new NotificationChannel(
                    CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, notificationManager.IMPORTANCE_HIGH );
            notificationChannelHigh.enableLights( true );
            notificationChannelHigh.setLightColor( Color.RED );
            notificationChannelHigh.setShowBadge( true );
            notificationChannelHigh.enableVibration( true );
            notificationChannelHigh.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC );
            notificationManager.createNotificationChannel( notificationChannelHigh );
            NotificationChannel notificationChannelDefault = new NotificationChannel(
                    CHANNEL_DEFAULT_ID, CHANNEL_DEFAULT_NAME, notificationManager.IMPORTANCE_DEFAULT );
            notificationChannelDefault.enableLights( true );
            notificationChannelDefault.setLightColor( Color.WHITE );
            notificationChannelDefault.enableVibration( true );
            notificationChannelDefault.setShowBadge( false );
            notificationManager.createNotificationChannel( notificationChannelDefault );
        }
    }

    public void notify( int id, boolean prioritary, String title, String message ) {
        String channelId = prioritary ? CHANNEL_HIGH_ID : CHANNEL_DEFAULT_ID;
        Resources res = getApplicationContext().getResources();
        Context context = getApplicationContext();

        /* Lien avec l'activité à ouvrir : ici DescriptionDuConcertActivity */
        /*Intent notificationIntent = new Intent(context, DescriptionDuConcertActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), 456, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);*/

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder( getApplicationContext(), channelId )
                    //.setContentIntent( contentIntent )      // On injecte le contentIntent
                    .setContentTitle( title )
                    .setContentText( message )
                    .setSmallIcon( R.drawable.ic_launcher_foreground )
                    .setLargeIcon( BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground) )
                    .setAutoCancel( true )
                    .build();
        }

        notificationManager.notify( id, notification );
    }

    public void cancelNotification( int id ) {
        notificationManager.cancel( id );
    }
}