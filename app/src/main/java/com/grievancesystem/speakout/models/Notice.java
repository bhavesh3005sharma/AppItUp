package com.grievancesystem.speakout.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.grievancesystem.speakout.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import static java.text.DateFormat.getDateTimeInstance;

public class Notice {
    String title;

    String message;

    Map timeStampMap;

    String issuedBy;

    String photoUrl;

    public Notice(String title, String message, Map timeStampMap, String issuedBy) {
        this.title = title;
        this.message = message;
        this.timeStampMap = timeStampMap;
        this.issuedBy = issuedBy;
    }

    public Notice(String title, String message, Map timeStampMap, String issuedBy, String photoUrl) {
        this.title = title;
        this.message = message;
        this.timeStampMap = timeStampMap;
        this.issuedBy = issuedBy;
        this.photoUrl = photoUrl;
    }

    @BindingAdapter("android:loadImage")
    public static void loadImage(ImageView imageView, String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty())
            Glide.with(imageView).load(photoUrl).placeholder(R.drawable.placeholder_image).into(imageView);
        else imageView.setVisibility(View.GONE);
    }

    @BindingAdapter("android:setTime")
    public static void setTime(TextView textView, Map map) {
        String time = "N/A";
        if (map != null && map.containsKey("timeStamp")) {
            long timeStamp = (long) map.get("timeStamp");
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timeStamp));
            time = dateFormat.format(netDate);
        }

        textView.setText(time);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map getTimeStampMap() {
        return timeStampMap;
    }

    public void setTimeStampMap(Map timeStampMap) {
        this.timeStampMap = timeStampMap;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", timeStampMap=" + timeStampMap +
                ", issuedBy='" + issuedBy + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
