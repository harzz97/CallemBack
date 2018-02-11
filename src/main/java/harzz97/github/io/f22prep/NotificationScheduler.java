package harzz97.github.io.f22prep;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.joda.time.DateTime;

class NotificationScheduler {


    private static  int REQUEST_CODE = 100;
    private static AlarmManager alarmManager ;
    private static  PendingIntent pendingIntent;

    static void setReminder(Context c, Class<?> cls, long schedule){

        Intent intent =new Intent(c,cls);
        intent.setAction("Alarm triggered");
        intent.putExtra("schedule",schedule);
        pendingIntent = PendingIntent.getBroadcast(c,REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences preferences = c.getSharedPreferences("AlarmPreferences",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("schedule",schedule);
        editor.apply();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,schedule,pendingIntent);
    }

    static void cancelReminder(Context c, Class<?> cls){
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

    static void showNotification(Context context, Class<?> cls, String[] values){

        SharedPreferences preferences = context.getSharedPreferences("AlarmPreferences",0);

        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String title = String.format("Call %s back !!",values[1]);
        String callTime = new DateTime().withMillis(Long.parseLong(values[4])).toString("HH:mm");
        String content = String.format("You tried calling %s at %s ",values[1],callTime);

        Intent callIntent = new Intent(context,AlarmReceiver.class);
        callIntent.setAction("call");
        callIntent.putExtra("schedule",preferences.getLong("schedule",0));
        //context.startActivity(callIntent);

        PendingIntent piCall = PendingIntent.getBroadcast(context,REQUEST_CODE,callIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(context,AlarmReceiver.class);
        snoozeIntent.setAction("snooze");
        snoozeIntent.putExtra("schedule",preferences.getLong("schedule",0));
        PendingIntent piSnooze = PendingIntent.getBroadcast(context,REQUEST_CODE,snoozeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(context,AlarmReceiver.class);
        deleteIntent.setAction("delete");
        deleteIntent.putExtra("schedule",preferences.getLong("schedule",0));
        PendingIntent piDelete = PendingIntent.getBroadcast(context,REQUEST_CODE,deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent contentIntent = new Intent(context,MainActivity.class);
        PendingIntent piContentIntent = PendingIntent.getActivity(context,REQUEST_CODE,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent notificationIntent = new Intent(context,cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(REQUEST_CODE,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(title)
                .setTicker("Call em Back")
                .setContentText(content)
                .setSound(ringtone)
                .setStyle(new NotificationCompat.BigTextStyle(builder))
                .setContentIntent(piContentIntent)
                .addAction(R.drawable.ic_call_black_24dp,"Call",piCall)
                .addAction(R.drawable.ic_snooze_black_24dp,"Snooze",piSnooze)
                .addAction(R.drawable.ic_delete_black_24dp,"Delete",piDelete)
                .setSmallIcon(R.drawable.callemback)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(REQUEST_CODE, notification);

    }
}
