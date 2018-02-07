package harzz97.github.io.f22prep;


import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import org.joda.time.DateTime;

import java.util.Calendar;

public class PhoneStateReceiver extends BroadcastReceiver {

    Context mContext;
    DatabaseHandler db;
    DateTime  dateTime= new DateTime();


    @Override
    public void onReceive(Context context, Intent intent) {

        //set Context
        mContext = context;

        //define bundle store intents
        Bundle bundle;
        if(intent!=null && intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            Log.e("Out going call","");
        }else{

            // create an instance of DatabaseHandler
            db= new DatabaseHandler(mContext.getApplicationContext());
            bundle = intent.getExtras();
            assert bundle != null;
            //get the outgoing number from the intent
            final String number = bundle.getString("incoming_number");
            //once the state becomed idle start a handler after 4 milliseconds
            // giving enough time for system to write call history
            if((bundle.get("state").equals("IDLE"))){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         @params[0] is call duration
                         @params[1] is timestamp which represents the time of call
                         **/
                        String params[];
                        //read call logs for the outgoing number
                        params = readCallLogs(number);
                        //find the difference between current time and time at which call was made
                        long difference = ((System.currentTimeMillis()/1000)-(Long.parseLong(params[1])/1000));
                        //if call duration is zero and the difference is greater than 5 second but less than a minute
                        //display the prompt
                        //maximum duration before the call gets cancelled automatically is 60 seconds
                        if ((params[0].equals("0")) &&((difference>=5)&&(difference<65))){
                            //display prompt for that number
                            displayPrompt(number);

                        }

                    }
                },400);

            }
        }
        }


    /***
     * @param number is the outgoing number
     *               function return
     *               duration of call and time of call
     * */
    private String[] readCallLogs(String number){

        //projection for thr query
        String[] projection = new String[]{Calls._ID,
                Calls.NUMBER,
                Calls.DATE,
                Calls.DURATION,
                Calls.TYPE};

        //create a cursor to get the details for the selected contact

        //selection params is used to select numbers that are outgoing and match the passed number
        //sort the result in descending so we get the recent call first
        //outgoing calls values are 2

        @SuppressLint("MissingPermission")
        Cursor cursor = mContext.getContentResolver().query(Calls.CONTENT_URI,
                projection,
                Calls.NUMBER +" =? "+ " AND "+ Calls.TYPE + " =? ",
                new String[]{number,"2"},
                Calls.DATE + " DESC");


        //when cursor has values
        if(cursor.getCount()>0){
            //move cursor to first
            cursor.moveToFirst();
            //add duration and timestamp(time of call) to a new String array
            String[] callLog = new String[]{
                    cursor.getString(cursor.getColumnIndex(Calls.DURATION)),
                    cursor.getString(cursor.getColumnIndex(Calls.DATE))
            };
            //close tha cursor
            cursor.close();
            //return the string array
            return callLog;
        }//if cursor is empty return dummy values
        else{
            return new String[]{"1","0"};
        }

    }


    /***
     * @param number is the outgoing number
     *               function returns a string array containing display name
     *               phone number and
     *               uri to profile photo
     * */

    private String[] getContactDetails(String number){


        //project only the name number and profile uri
        String[] projection = new String[]{Phone.DISPLAY_NAME,Phone.PHOTO_URI, Phone.NUMBER};

        //return values that match the number
        Cursor cursor = mContext.getContentResolver().query(Phone.CONTENT_URI,
                projection,
                Phone.NUMBER+ " =? ",
                new String[]{number},null);

        cursor.moveToFirst();

        if(cursor.getCount()>0){

            //store values to an string array
            String[] values =  new String[]{
                    cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(Phone.NUMBER)),
                    cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI))
            };
            cursor.close();
            return values;//return the string array
        }
        //if no details found or contacts doesn't exits
        return new String[]{"",number,"0"};
    }

    /**
     *
     * the params we pass to add a new entry to database are
     * ContactName,ContactNumber,contactImage path,call Time,reminder duration
     *
     * call Time and reminder duration are sent as timestamp values
     *
     * **/
    @SuppressLint("ClickableViewAccessibility")
    private void displayPrompt(final String number){

        //create objects
        final WindowManager windowManager;
        final LinearLayout linearLayout;
        WindowManager.LayoutParams layoutParams;
        //get the contactDetails
        final String[] contactDetails = getContactDetails(number);

        //initialise the window manager
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        //create new LayoutParams
        layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //set width, height, x and y co-ordinates
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.format = PixelFormat.TRANSPARENT;


        linearLayout = new LinearLayout(mContext);
        //set orientation for the created linear layout
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //get the inflater from system
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate the popup view to the layout created
        View popUpView = inflater.inflate(R.layout.popup, linearLayout,false);

        //initialise the  contents of the layout
        final TextView contactName = popUpView.findViewById(R.id.contactName);
        final TextView contactNumber = popUpView.findViewById(R.id.contactNumber);
        ImageView closeButton = popUpView.findViewById(R.id.closeButton);
        ImageView profilePhoto = popUpView.findViewById(R.id.contactPhoto);
        TextView fifteen = popUpView.findViewById(R.id.buttonFifteen);
        TextView thirty = popUpView.findViewById(R.id.buttonThirty);
        TextView custom = popUpView.findViewById(R.id.buttonCustom);
        CardView prompt = popUpView.findViewById(R.id.remindPrompt);

        Log.e("Contact Name",contactDetails[0]);
        if(contactDetails[0].isEmpty()){
            profilePhoto.setImageResource(R.drawable.defaultcallpic);
            //set the contact name
            contactName.setText(number);
            //contactNumber.setText(number);//set contact number

        }else{
            //set the contact name
            contactName.setText(contactDetails[0]);
            contactNumber.setText(number);//set contact number
            TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(contactDetails[0].charAt(0)), Color.RED);
            profilePhoto.setImageDrawable(drawable);
        }

        //if no contact thumbnail set default
        /*if((contactDetails[2]!=null)&&(!(Objects.equals(contactDetails[2], "0")))){
            //Glide.with(mContext).load(contactDetails[2]).apply(RequestOptions.circleCropTransform()).into(profilePhoto);
            profilePhoto.setImageURI(Uri.parse(contactDetails[2]));
        }else{
            //Glide.with(mContext).load(R.drawable.defaultcallpic).apply(RequestOptions.circleCropTransform()).into(profilePhoto);
            profilePhoto.setImageResource(R.drawable.defaultcallpic);
        }*/

        //add the view to the layout
       linearLayout.addView(popUpView);

        //add layout to window
        windowManager.addView(linearLayout,layoutParams);




        //set onclick listener for 15 min button
        fifteen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //the dateTime object used here from joda library
                //create a new dateTime object and add 15 minutes to it
                dateTime = new DateTime().plusMinutes(15);
                //set reminder for fifteen minutes from current time
                NotificationScheduler.setReminder(mContext, AlarmReceiver.class,dateTime.getMillis());
                //display toast message
                Toast.makeText(mContext,"Notification set for 15 Min",Toast.LENGTH_LONG).show();
                //create new entry to the database
                if(contactDetails[0].isEmpty()){
                    db.addEntry(new UserDetails("",
                           number,
                            contactDetails[2],
                            String.valueOf(System.currentTimeMillis()),
                            String.valueOf(dateTime.getMillis())));
                }else{

                    db.addEntry(new UserDetails(contactName.getText().toString(),
                            contactNumber.getText().toString(),
                            contactDetails[2],
                            String.valueOf(System.currentTimeMillis()),
                            String.valueOf(dateTime.getMillis())));
                }
                //remove layout from window
                windowManager.removeViewImmediate(linearLayout);
            }
        });

        //set onClick Listener for 30 min button
        thirty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //create a new dateTime object and add 30 minutes to it
                dateTime = new DateTime().plusMinutes(30);
                //set reminder for thirty minutes
                NotificationScheduler.setReminder(mContext,AlarmReceiver.class,dateTime.getMillis());
                //create new entry in the database
                db.addEntry(new UserDetails(contactName.getText().toString(),
                        contactNumber.getText().toString(),
                        contactDetails[2],
                        String.valueOf(System.currentTimeMillis()),
                        String.valueOf(dateTime.getMillis())));
                //remove layout from window
                windowManager.removeViewImmediate(linearLayout);
            }
        });

        //set on Click listener for custom time button
        custom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //create a new Timepicker dialog and set false for 24HourView
                //pass the current hour and current minute
                TimePickerDialog pickerDialog = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hr, int min) {
                                //create a DateTime object for the time selected from the picker
                                dateTime = new DateTime().withHourOfDay(hr).withMinuteOfHour(min).withSecondOfMinute(0);
                                //set Reminder
                                NotificationScheduler.setReminder(mContext, AlarmReceiver.class, dateTime.getMillis());
                                //add contactName,contactNumber,profilePicture path,Current Time,reminder time
                                db.addEntry(new UserDetails(contactName.getText().toString(),
                                        contactNumber.getText().toString(),
                                        contactDetails[2],
                                        System.currentTimeMillis()+"",
                                        String.valueOf(dateTime.getMillis())));
                                Toast.makeText(mContext,"Notification Set",Toast.LENGTH_LONG).show();

                            }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        false);

                //set LayoutParams type as TYPE_PHONE
                pickerDialog.getWindow().setType(LayoutParams.TYPE_PHONE);
                //show picker
                pickerDialog.show();
            }
        });
        //set onClick Listener for closeText
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(linearLayout);
            }
        });
    }

}
