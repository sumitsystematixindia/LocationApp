package com.mlins.nav.utils;

import android.os.AsyncTask;

import com.mlins.utils.logging.Log;

public class EventsToServerProvider {

    private final static String TAG = "ccom.mlins.nav.utils.EmailUtil";
    private final String SERVERADDRESS = "";
    private String event_title = null;
    private String event_description = null;
    private String event_start = null;
    private String event_end = null;
    private String event_picture_path = null;
    private String event_picture_file = null;
    private String email_address = null;
    private String serverRespondResult = "";
    private String url = SERVERADDRESS;

    public EventsToServerProvider(CostumerEventObject ceo, String mailAddress) {
        super();
        this.event_title = ceo.getTitle();
        this.event_description = ceo.getDescription();
        this.event_start = ceo.getEventStartDate().toString();
        this.event_end = ceo.getEventEndingDate().toString();
        this.event_picture_path = ceo.getUrlForMoreDetails();
        this.event_picture_file = ceo.getImageFileName();
        this.email_address = mailAddress;
    }


    public String getEmail_address() {
        return email_address;
    }


    public String getEvent_title() {
        return event_title;
    }


    public String getEvent_description() {
        return event_description;
    }


    public String getEvent_start() {
        return event_start;
    }


    public String getEvent_end() {
        return event_end;
    }


    public String getEvent_picture_path() {
        return event_picture_path;
    }


    public String getEvent_picture_file() {
        return event_picture_file;
    }


    public boolean postEventsDetails() {
        Log.getInstance().debug(TAG, "Enter, postEventsDetails()");

        AsyncTask<String, Void, String> rltasker = new EventsPostMassegesTask(EventsToServerProvider.this)
                .execute(url);

        try {
            this.serverRespondResult = rltasker.get();

            return true;
        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.serverRespondResult = "";
        } finally {
            if (rltasker != null && !rltasker.isCancelled()) {
                rltasker.cancel(true);
            }
        }
        Log.getInstance().debug(TAG, "Exit, postEventsDetails()");
        return false;
    }

}

	

