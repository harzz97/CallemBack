package harzz97.github.io.f22prep;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;


public class RecycleInflater extends RecyclerView.Adapter<RecycleInflater.ViewHolder>  {


    private List<UserDetails> details;
    private Context mContext;
    private DatabaseHandler db;
    private DateTime dateTime;
    private Boolean hasPastActivityStarted=false;
    private Boolean hasFutureActivityStarted=true;

    RecycleInflater(List<UserDetails> details, Context c){

        this.details = details;
        this.mContext = c;
        db = new DatabaseHandler(mContext);
        dateTime = new DateTime();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_row_layout,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final UserDetails userDetails = details.get(position);
        //if the reminder time is past current system time
        //set the title visible and change its value
        if(Long.parseLong(userDetails.getDuration())<= System.currentTimeMillis()){
            if(!hasPastActivityStarted){
                holder.itemTitle.setVisibility(View.VISIBLE);
                holder.itemTitle.setText(R.string.pastReminder);
                hasPastActivityStarted=true;
            }else{
                holder.itemTitle.setVisibility(View.GONE);
            }
            holder.expand.setVisibility(View.GONE);
            holder.deleteSide.setVisibility(View.GONE);
            holder.pastText.setText( new DateTime().withMillis(Long.parseLong(userDetails.getDuration())).toString("d MMM YY HH:mm"));
            holder.userName.setTextColor(Color.rgb(119,136,153));
            holder.mobileNumber.setTextColor(Color.rgb(119,136,153));
        }else{
            if(hasFutureActivityStarted) {
                holder.itemTitle.setText(R.string.upComingReminder);
                hasFutureActivityStarted=false;
                holder.itemTitle.setVisibility(View.VISIBLE);
            }else{
                holder.itemTitle.setVisibility(View.GONE);
            }
            holder.remindingText.setText(String.format("Reminding at: %s", new DateTime().withMillis(Long.parseLong(userDetails.getDuration())).toString("HH:mm")));
        }

        holder.userName.setText(userDetails.getContactName());
        holder.mobileNumber.setText(userDetails.getContactNumber());


        if(userDetails.getContactName().isEmpty()){
            holder.profilePicture.setImageResource(R.drawable.defaultcallpic);
            holder.mobileNumber.setTypeface(holder.mobileNumber.getTypeface(),Typeface.BOLD);
            holder.mobileNumber.setTextSize(16.0f);
            holder.userName.setVisibility(View.GONE);
        }else{
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .useFont(Typeface.SANS_SERIF)
                    .bold()
                    .endConfig()
                    .buildRound(userDetails.getContactName().substring(0,1), generateColor(userDetails.getContactName()));
            holder.profilePicture.setImageDrawable(drawable);
        }


        holder.snoozeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                dateTime = dateTime.withHourOfDay(i).withMinuteOfHour(i1).withSecondOfMinute(0);
                                userDetails.duration = String.valueOf(dateTime.getMillis());
                                NotificationScheduler.setReminder(mContext,AlarmReceiver.class,dateTime.getMillis());
                                db.updateEntry(userDetails);
                                details.clear();
                                details = db.getEntries();
                                notifyDataSetChanged();
                                notifyItemChanged(position);
                                hasFutureActivityStarted = true;
                                hasPastActivityStarted = false;
                            }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        false);

                timePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                timePickerDialog.show();


            }
        });

        holder.deleteSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(userDetails,holder.getAdapterPosition());
                hasFutureActivityStarted = true;
                hasPastActivityStarted = false;
            }
        });

        holder.callText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:"+userDetails.getContactNumber()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName,mobileNumber,remindingText,snoozeText,itemTitle,callText,pastText;
        ImageView profilePicture,deleteSide;

        LinearLayout expand;
        CardView itemLayout;
        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            mobileNumber = itemView.findViewById(R.id.user_number);
            profilePicture = itemView.findViewById(R.id.user_profile);
            itemLayout = itemView.findViewById(R.id.simple_row);
            expand = itemView.findViewById(R.id.expandable_layout);
            remindingText = itemView.findViewById(R.id.remindingText);
            snoozeText = itemView.findViewById(R.id.snoozeText);
            deleteSide = itemView.findViewById(R.id.deleteSide);
            callText = itemView.findViewById(R.id.callText);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            pastText = itemView.findViewById(R.id.pastText);
        }

    }
    int generateColor(String name){

        ColorGenerator generator = ColorGenerator.MATERIAL;
        return generator.getColor(name);
    }
    private void showDeleteDialog(final UserDetails userDetails,final int position){
        final AlertDialog.Builder builder= new AlertDialog.Builder(mContext);

        builder.setTitle("Delete Reminder")
                .setMessage("Are you sure ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteEntry(userDetails);
                        details.remove(position);
                        notifyItemRemoved(position);
                        details.clear();
                        details = db.getEntries();
                        notifyDataSetChanged();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setCancelable(true);
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.show();
    }
}
