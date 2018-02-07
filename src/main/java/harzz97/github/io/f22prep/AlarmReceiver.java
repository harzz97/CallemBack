package harzz97.github.io.f22prep;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.joda.time.DateTime;


public class AlarmReceiver extends BroadcastReceiver {

    long schedule;

    public AlarmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() != null && context != null){

            if(intent.getAction().equals("Alarm triggered")){
                //get epoch time stamp from intent extra
                schedule = intent.getLongExtra("schedule",0);
                NotificationScheduler.setReminder(context,AlarmReceiver.class,intent.getLongExtra("schedule",0));

                DatabaseHandler db = new DatabaseHandler(context);

                String[] details = db.getNotificationDetail(schedule);

                db.disableNotification(schedule);

                //Trigger the notification
                NotificationScheduler.showNotification(context, MainActivity.class,
                        details);

                NotificationScheduler.cancelReminder(context,MainActivity.class);
                db.close();
            }
            if(intent.getAction().equals("snooze")){
                long schedule = intent.getLongExtra("schedule",0);

                DatabaseHandler db = new DatabaseHandler(context);
                DateTime dateTime = new DateTime().plusMinutes(15).withSecondOfMinute(0);
                db.snoozeNotification(schedule,dateTime.getMillis());
                NotificationScheduler.setReminder(context,AlarmReceiver.class,new DateTime().plusMinutes(15).getMillis());
                Toast t = Toast.makeText(context,"Snoozed for 15 Minutes",Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                t.show();
                db.close();

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(100);


            }

            if(intent.getAction().equals("delete")){

                long schedule = intent.getLongExtra("schedule",0);
                DatabaseHandler db = new DatabaseHandler(context);
                db.deleteNotification(schedule);
                db.close();
                Toast t = Toast.makeText(context,"Reminder Deleted",Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                t.show();
                db.close();

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(100);

            }
            if(intent.getAction().equals("call")){

                long schedule = intent.getLongExtra("schedule",0);

                DatabaseHandler db = new DatabaseHandler(context);
                String[] details = db.getNotificationDetail(schedule);
                db.close();

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(100);
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:"+details[2].trim()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        }




    }
}
