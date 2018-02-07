package harzz97.github.io.f22prep;

import android.util.Log;

public class UserDetails {
    String ContactName;
    String remindingTime;
    String ContactNumber;
    private String enabled;
    String snoozed;
    String callTime;
    String duration;
    //String profilePath;
    int _id;



    String profilePath;

    public UserDetails() {

    }

    public UserDetails(String ContactName, String number, String date ) {
        this.ContactName = ContactName;
        this.callTime = date;
        this.ContactNumber = number;

    }

    UserDetails(String name,String number,String profile,String date,String duration){
        this.ContactNumber = number;
        this.ContactName = name;
        this.profilePath = profile;
        this.callTime = date;
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    void setDuration(String duration) {
        this.duration = duration;
    }

    void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        this.ContactName = contactName;
    }

    public String getRemindingTime() {
        return remindingTime;
    }

    public void setRemindingTime(String remindingTime) {
        this.remindingTime = remindingTime;
    }

    public String getProfilePath() {
        return profilePath;
    }


    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {this.ContactNumber = contactNumber;}

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getSnoozed() {
        return snoozed;
    }

    public void setSnoozed(String snoozed) {
        this.snoozed = snoozed;
    }
    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
