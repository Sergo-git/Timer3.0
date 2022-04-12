package com.example.android.timer30;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;




public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {


    private SharedPreferences settings;
    private final String SETTINGS_NAME = "settings";
    private String timeWork;
    private String timeRest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();





        Bundle args = getIntent().getExtras();
        if (args != null) {
            ((EditText) findViewById(R.id.timeWork)).setText(args.getString("timeWork"));
            ((EditText) findViewById(R.id.timeRest)).setText(args.getString("timeRest"));

        }






        findViewById(R.id.save).setOnClickListener(this);
        loadSettings();



    }



    private void saveSettings() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("timeWork", timeWork);
        editor.putString("timeRest", timeRest);
        editor.apply();

    }

    private void loadSettings() {
        settings = getSharedPreferences(SETTINGS_NAME, MODE_PRIVATE);
        timeWork = settings.getString("timeWork", Settings.DEFAULT_TIME_WORK);
        timeRest = settings.getString("timeRest", Settings.DEFAULT_TIME_REST);
    }





    @Override
    public void onClick(View view) {
        String timeWork = ((EditText) findViewById(R.id.timeWork)).getText().toString();
        String timeRest = ((EditText) findViewById(R.id.timeRest)).getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat(Settings.FORMAT_DATE);
        try {
            sdf.parse(timeWork);
            sdf.parse(timeRest);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("timeWork", timeWork);
            intent.putExtra("timeRest", timeRest);
            startActivity(intent);
        } catch (ParseException e) {
            Toast.makeText(this, getResources().getString(R.string.errorFormat) + Settings.FORMAT_DATE, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        saveSettings();
    }
}
