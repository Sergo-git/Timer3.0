package com.example.android.timer30;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeTask extends AsyncTask<Void, String, Void> {

    private TextView time;
    private boolean stop = false;

    public CurrentTimeTask(TextView time) {
        this.time = time;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!stop) {
            SimpleDateFormat sdf = new SimpleDateFormat(Settings.FORMAT_DATE);
            publishProgress(sdf.format(new Date()));
            SystemClock.sleep(1000);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        time.setText(values[0]);
    }

}
