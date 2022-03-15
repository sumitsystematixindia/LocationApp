package com.mlins.nav.utils;

import com.mlins.utils.logging.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CostumerEventObject {
    final static private String TAG = "ccom.mlins.nav.utils.CostumerEventObject";

    private String title;
    private String imageFileName;
    private String description;
    private String urlForMoreDetails;
    private Date eventStartDate;
    private Date eventEndingDate;

    public CostumerEventObject() {

    }

    public CostumerEventObject(String str) {
        setTitle(str);
        setImageFileName("default_grand_mall.png");
    }


    //Setters and Getters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlForMoreDetails() {
        return urlForMoreDetails;
    }

    public void setUrlForMoreDetails(String urlForMoreDetails) {
        this.urlForMoreDetails = urlForMoreDetails;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndingDate() {
        return eventEndingDate;
    }

    public void setEventEndingDate(Date eventEndingDate) {
        this.eventEndingDate = eventEndingDate;
    }


    public void Parse(String line) {
        Log.getInstance().debug(TAG, "Enter, Parse()");
        String[] vals = line.split("\t");

        setTitle(vals[0]);
        setImageFileName(vals[1]);
        setDescription(vals[2]);
        setUrlForMoreDetails(vals[3]);

        String start = vals[4];
        SimpleDateFormat startSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date startD;
        try {
            startD = startSimpleDateFormat.parse(start);
            setEventStartDate(startD);
        } catch (ParseException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        String end = vals[5];
        SimpleDateFormat endSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date endD;
        try {
            endD = endSimpleDateFormat.parse(end);
            setEventEndingDate(endD);
        } catch (ParseException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        Log.getInstance().debug(TAG, "Exit, Parse()");
    }

    public boolean isEventRelevant() {
        boolean relevant = false;

        Date currentDate = Calendar.getInstance().getTime();

        try {
            //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            //currentDate
            //Date resultdate = sdf.parse(currentDate.toString());
            Date resultdate = currentDate;

            if (resultdate.compareTo(getEventEndingDate()) > 0) {
                relevant = false;
            } else if (resultdate.compareTo(getEventEndingDate()) < 0) {
                relevant = true;
            }

        } catch (Exception ex) {
            Log.getInstance().error(TAG, ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return relevant;
    }

    public boolean isEventTakingPlaceNow() {
        boolean takePlace = false;

        Date currentDate = Calendar.getInstance().getTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date resultdate = sdf.parse(currentDate.toString());

            if ((resultdate.compareTo(getEventStartDate()) > 0) && (resultdate.compareTo(getEventEndingDate()) < 0)) {
                takePlace = true;
            }

        } catch (Exception ex) {
            Log.getInstance().error(TAG, ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return takePlace;
    }
}
