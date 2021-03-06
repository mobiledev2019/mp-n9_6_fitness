package com.example.datnguyen.fitness.Activity.Exercises;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.datnguyen.fitness.Database.WorkoutDB;
import com.example.datnguyen.fitness.Others.AlarmNotificationReceiver;
import com.example.datnguyen.fitness.R;

import java.util.Calendar;

public class SettingPage extends AppCompatActivity {

    Button btnSave;
    RadioButton rdiEasy, rdiMedium, rdiHard;
    RadioGroup rdiGroup;
    WorkoutDB workoutDB;
    ToggleButton switchAlarm;
    TimePicker timePicker;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        //Animation
        overridePendingTransition(R.anim.fade_in,R.anim.zoom_out);

        // Init view
        btnSave = findViewById(R.id.btnSave);

        rdiGroup = findViewById(R.id.rdiGroup);
        rdiEasy = findViewById(R.id.rdiEasy);
        rdiMedium = findViewById(R.id.rdiMedium);
        rdiHard = findViewById(R.id.rdiHard);

        switchAlarm = findViewById(R.id.switchAlarm);

        timePicker = findViewById(R.id.timePicker);
        workoutDB = new WorkoutDB(this);

        //get alarm state
        sharedPreferences = getSharedPreferences("alarm",MODE_PRIVATE);
        switchAlarm.setChecked(sharedPreferences.getBoolean("state",false));
        // get data
        int mode = workoutDB.getSettingMode();
        setRadioButton(mode);

        // event handling
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                saveWorkoutMode();
                saveAlarm(switchAlarm.isChecked());
                Toast.makeText(SettingPage.this, "Saved!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveAlarm(boolean checked) {
        if (checked){
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent;
            PendingIntent pendingIntent;
            intent = new Intent(SettingPage.this,AlarmNotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            //Set alarm
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());

            long timeUntilTrigger;
            if (System.currentTimeMillis() > calendar.getTimeInMillis()){
                timeUntilTrigger = calendar.getTimeInMillis() + 86400000;
            }else{
                timeUntilTrigger = calendar.getTimeInMillis();
            }
//            Date toDay = Calendar.getInstance().getTime();
//            calendar.set(toDay.getYear(),toDay.getMonth(),toDay.getDay(),timePicker.getHour(),timePicker.getMinute());

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,timeUntilTrigger,AlarmManager.INTERVAL_DAY,pendingIntent);

        }
        else {
            //Cancel Alarm
            Intent intent = new Intent(SettingPage.this,AlarmNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
            AlarmManager  manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }

        //save switchAlarm state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state",checked);
        editor.commit();
    }

    private void setRadioButton(int mode) {
        if (mode == 0)
        {
            rdiGroup.check(R.id.rdiEasy);
        }
        else if (mode == 1 )
        {
            rdiGroup.check(R.id.rdiMedium);
        }
        else if (mode == 2 )
        {
            rdiGroup.check(R.id.rdiHard);
        }
    }
    private void saveWorkoutMode(){
        int selectedID =  rdiGroup.getCheckedRadioButtonId();
        if (selectedID == rdiEasy.getId())
        {
            workoutDB.saveSettingMode(0);
        }
        else   if (selectedID == rdiMedium.getId())
        {
            workoutDB.saveSettingMode(1);
        }
        else   if (selectedID == rdiHard.getId())
        {
            workoutDB.saveSettingMode(2);
        }

    }
}
