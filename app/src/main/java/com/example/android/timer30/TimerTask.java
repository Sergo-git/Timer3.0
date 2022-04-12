package com.example.android.timer30;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimerTask extends AsyncTask<Void, String, Void> {

    private MainActivity context;
    private TextView timer;
    private boolean stop = false;
    private long startTime;
    private String allTime;
    private long offset;

    public TimerTask(MainActivity context, TextView timer) {
        this.context = context;
        this.timer = timer;
        Calendar calendar = new GregorianCalendar();
        offset = calendar.getTimeZone().getRawOffset();
    }

    public boolean getStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
        allTime = timer.getText().toString();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            SimpleDateFormat sdf = new SimpleDateFormat(Settings.FORMAT_DATE);
            try {
                Date date = sdf.parse(allTime);
                date.setTime(date.getTime() - (System.currentTimeMillis() - startTime));
                if (date.getTime() + offset <= 0) break;
                SystemClock.sleep(1000);
                if (stop) break;
                publishProgress(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        timer.setText(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context.endTimer();
    }
}
