package com.example.android.timer30;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences settings;
    private final String SETTINGS_NAME = "settings";
    private String timeWork;
    private String timeRest;
    private boolean isWork = true;


    TextView time;
    private TextView timer;
    private Button button;
    private ImageView changeType;
    private ImageView reset;
    private ImageView imLamp;
    private Switch mSwitch;
    private View vnight_bd;


    private CurrentTimeTask currentTimeTask;
    private TimerTask timerTask;

    private Vibrator v;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();



        loadSettings();


        timer = findViewById(R.id.timer);

        setTimer();

        button = findViewById(R.id.button);
        button.setBackgroundColor(getColor(R.color.green));
        changeType = findViewById(R.id.changeType);
        reset = findViewById(R.id.reset);
        mSwitch = findViewById(R.id.mSwitch);
        imLamp = findViewById(R.id.imLamp);
        time= findViewById(R.id.time);
        vnight_bd= findViewById(R.id.vnight_bd);


        button.setOnClickListener(this);
        changeType.setOnClickListener(this);
        reset.setOnClickListener(this);
        mSwitch.setOnClickListener(this);



        findViewById(R.id.settings).setOnClickListener(this);









        player = MediaPlayer.create(this, R.raw.music);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlayer();
            }
        });


        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSwitch.isChecked()){

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    imLamp.setImageResource(R.drawable.yellowlampp);
                    vnight_bd.setBackgroundResource(R.drawable.night_bg);
                    time.setTextColor(getColor(R.color.blue50));
                    //Toast.makeText(getApplicationContext(),"Sleep ON",Toast.LENGTH_SHORT).show();
                }else {

                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    vnight_bd.setBackgroundColor(getColor(R.color.black));
                    imLamp.setImageResource(R.drawable.whitelampppp);
                    time.setTextColor(getColor(R.color.blue));

                }
            }
        });
    }

    private void loadSettings() {
        settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
        timeWork = settings.getString("timeWork", Settings.DEFAULT_TIME_WORK);
        timeRest = settings.getString("timeRest", Settings.DEFAULT_TIME_REST);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("timeWork", timeWork);
        editor.putString("timeRest", timeRest);
        editor.apply();

    }

    private void setTimer() {
        TextView type = findViewById(R.id.type);
        if (isWork) {
            type.setText(getResources().getString(R.string.work));
            timer.setText(timeWork);
        }
        else {
            type.setText(getResources().getString(R.string.rest));
            timer.setText(timeRest);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings: {
                Intent intent = new Intent (getApplicationContext(), SettingsActivity.class);
                intent.putExtra("timeWork", timeWork);
                intent.putExtra("timeRest", timeRest);
                stopTimer();
                startActivity(intent);
                break;
            }
            case R.id.button: {
                if (button.getText().equals(getResources().getString(R.string.start))) startTimer();
                else stopTimer();
                break;
            }
            case R.id.changeType: {
                changeType();
                break;
            }
            case R.id.reset: {
                setTimer();
                break;
            }
        }
    }


    private void startTimer() {
        button.setText(getResources().getString(R.string.stop));
        button.setBackgroundColor(getColor(R.color.red));
        changeType.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        if (timerTask != null) timerTask.setStop(true);
        timerTask = new TimerTask(this, timer);
        timerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void stopTimer() {
        button.setText(getResources().getString(R.string.start));
        button.setBackgroundColor(getColor(R.color.green));
        changeType.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        if (timerTask != null) timerTask.setStop(true);
    }

    private void changeType() {
        isWork = !isWork;
        setTimer();
    }

    public void endTimer() {
        if (!timerTask.getStop()) {
            vibrate();
            startPlayer();
            changeType();
            String title = getResources().getString(R.string.endTime);
            String ok = getResources().getString(R.string.ok);
            String message;
            if (isWork) message = getResources().getString(R.string.workTime);
            else message = getResources().getString(R.string.restTime);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(message)
                    .setNeutralButton(ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            v.cancel();
                            stopPlayer();
                            startTimer();
                        }
                    }).create();
            dialog.show();
        }
    }

    private void vibrate() {
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 2000, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern, 1));
        }
        else v.vibrate(pattern, 1);
    }

    private void startPlayer() {
        player.start();
    }

    private void stopPlayer() {
        player.stop();
        try {
            player.prepare();
            player.seekTo(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startCurrentTimeTask() {
        if (currentTimeTask != null) currentTimeTask.setStop(true);
        currentTimeTask = new CurrentTimeTask((TextView) findViewById(R.id.time));
        currentTimeTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentTimeTask.setStop(true);
    }



    @Override
    protected void onResume() {
        super.onResume();
        startCurrentTimeTask();
        Bundle args = getIntent().getExtras();
        if (args != null) {
            timeWork = args.getString("timeWork");
            timeRest = args.getString("timeRest");
            saveSettings();
            setTimer();
        }

    }



}
