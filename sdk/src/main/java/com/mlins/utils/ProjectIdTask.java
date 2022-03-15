package com.mlins.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.spreo.spreosdk.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


class ProjectIdTask extends AsyncTask<String, Void, String> {
    private ApiKeyResponseListener mListener;

    private Context ctx = null;
    private ProgressDialog dialog = null;
    private int response = ApiKeyResponseListener.NO_RESPONSE;
    ;

    @Override
    protected void onPreExecute() {

        if (ctx != null) {
            dialog = new ProgressDialog(ctx);
            String downloadmessage = ctx.getResources().getString(
                    R.string.downloadmessage);
            dialog.setMessage(downloadmessage);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        String apikey = params[0];
        String servername = PropertyHolder.getInstance().getServerName();
        String url = servername + "apikey?req=0&apik=" + apikey;
        try {
            byte[] bytes = ServerConnection.getInstance().getResourceBytes(url);
            if (bytes.length == 0) {
                response = ApiKeyResponseListener.ERROR_TYPE_CONNECTION;
            } else {
                String res = new String(bytes);
                parse(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = ApiKeyResponseListener.ERROR_TYPE_CONNECTION;
        }

        return "";
    }

    private void parse(String res) {
        try {
            JSONTokener tokener = new JSONTokener(res);
            JSONObject json = (JSONObject) tokener.nextValue();
            Parseproject(res, tokener, json);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Parseproject(String res, JSONTokener tokener, JSONObject json) {
        try {
            String status = json.getString("status");
            if (status.equals("fail")) {
                response = ApiKeyResponseListener.ERROR_TYPE_INVALID_APIKEY;
            } else {
                String projectid = json.getString("pid");
                PropertyHolder.getInstance().setProjectId(projectid);
                response = ApiKeyResponseListener.SDK_ENABLED;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void registerListener(ApiKeyResponseListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (mListener != null) {
            mListener.apiKeyResponse(response);
        }

        if (dialog != null) {
            dialog.dismiss();
        }

    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }
}
