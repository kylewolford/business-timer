package com.example.businesstimernew;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    /**
     * Timer notes:
     *
     * bunker 100, 120, 140 (twice as long if both)
     *
     * nightclub safe 336 to fill
     *
     * nightclub popularity 480 (28800), 960 (57600) if upgraded (1 upgrade)
     *
     *
     * no upgrades:
     * bunker 100 (6000)
     * herbs 480 (28800)
     * rock candy 600 (36000)
     * party sugar 498 (29880)
     * mints 480 (28800)
     * invitations 300 (18000)
     *
     * one upgrade:
     * bunker 120 (7200)
     * herbs 400 (24000)
     * rock candy 480 (28800)
     * party sugar 400 (24000)
     * mints 400 (24000)
     * invitations 480 (28800)
     *
     * two upgrades:
     * bunker 140 (8400)
     * herbs 318 (19080)
     * rock candy 360 (21600)
     * party sugar 300 (18000)
     * mints 318 (19080)
     * invitations 180 (10800)
     *
     */

    public final int numTimers = 9;

    // arbitrarily declared to avoid errors
    public static final String CHANNEL_ID = "123";

    public String[] timerNames = {
            "bunker cargo",
            "bunker research",
            "herbs",
            "rock candy",
            "party sugar",
            "mints",
            "invitations",
            "nightclub popularity",
            "nightclub safe"
    };

    // textViews that display current times
    public int[] timeDisplays = {R.id.textView2, R.id.textView4, R.id.textView6, R.id.textView8,
            R.id.textView10, R.id.textView12, R.id.textView14, R.id.textView16, R.id.textView18};

    // checkBoxes that activate/deactivate timers
    public int[] activeChecks = {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4,
            R.id.checkBox5, R.id.checkBox6, R.id.checkBox7, R.id.checkBox8, R.id.checkBox9};

    // checkBoxes that toggle upgrades
    public int[] upgradeChecks = {R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13,
            R.id.checkBox14, R.id.checkBox15, R.id.checkBox16, R.id.checkBox17, R.id.checkBox18,
            R.id.checkBox19, R.id.checkBox20, R.id.checkBox21, R.id.checkBox22, R.id.checkBox23,
            R.id.checkBox24};



    // defaults assuming no upgrades (times are in seconds)
    public int[] times = {6000, 6000, 28800, 36000, 29880, 28800, 28800, 20160, 20160};

    // assume no upgrades to start (values can be 0-2 for the first seven, 0-1 for the eighth, and just 0 for the ninth)
    public int[] upgradeLevels = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    // assume timers are inactive by default
    public boolean[] timesActive = {false, false, false, false, false, false, false, false, false};

    public boolean running = false;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    NotificationCompat.Builder notifications[] = new NotificationCompat.Builder[numTimers];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Timer masterTimer = new Timer();
        masterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 1000);


        for (int i = 0; i < numTimers; i++)

        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, (CHANNEL_ID + i))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(timerNames[i] + " is done")
                    .setContentText("Your timer " + timerNames[i] + " has finished running.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            notifications[i] = mBuilder;
        }


    }

    private void toggle(int index) {
        timesActive[index] = !timesActive[index];
        updateTimers();
    }

    public void toggle1(View v) {
        toggle(0);
    }
    public void toggle2(View v) {
        toggle(1);
    }
    public void toggle3(View v) {
        toggle(2);
    }
    public void toggle4(View v) {
        toggle(3);
    }
    public void toggle5(View v) {
        toggle(4);
    }
    public void toggle6(View v) {
        toggle(5);
    }
    public void toggle7(View v) {
        toggle(6);
    }
    public void toggle8(View v) {
        toggle(7);
    }
    public void toggle9(View v) {
        toggle(8);
    }


    private void checkUpgrades(int index) {
        // 9 businesses, but 15 upgrades

        if (index == 7) { // only one upgrade (no upgrades for index 9)
            CheckBox curCheck = findViewById(upgradeChecks[14]);
            if (curCheck.isChecked()) {
                upgradeLevels[7] = 1;
            }
            else {
                upgradeLevels[7] = 0;
            }
        }

        else if (0 <= index && index < 7) { // normal operation
            CheckBox upgrade1 = findViewById(upgradeChecks[index*2]);
            CheckBox upgrade2 = findViewById(upgradeChecks[index*2 + 1]);
            int curUpgrades = 0;

            if (upgrade1.isChecked()) curUpgrades++;
            if (upgrade2.isChecked()) curUpgrades++;

            upgradeLevels[index] = curUpgrades;

        }
        updateTimers();
        return;
    }

    public void checkUpgrades1(View v) {
        checkUpgrades(0);
    }
    public void checkUpgrades2(View v) {
        checkUpgrades(1);
    }
    public void checkUpgrades3(View v) {
        checkUpgrades(2);
    }
    public void checkUpgrades4(View v) {
        checkUpgrades(3);
    }
    public void checkUpgrades5(View v) {
        checkUpgrades(4);
    }
    public void checkUpgrades6(View v) {
        checkUpgrades(5);
    }
    public void checkUpgrades7(View v) {
        checkUpgrades(6);
    }
    public void checkUpgrades8(View v) {
        checkUpgrades(7);
    }


    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    public String timeFormat(int time)
    {
        String timeStr = "";
        // Time is a number of seconds, which is converted to an h:mm:ss format.

        // Calculate hours (can be zero)
        timeStr += (time / 3600) + ":";
        time %= 3600;

        // Calculate minutes
        int numMins = time/60;
        if (numMins < 10) timeStr += "0";
        timeStr += (numMins) + ":";
        time %= 60;

        // Calculate seconds
        if (time < 10) timeStr += "0";
        timeStr += time;
        return timeStr;
    }


    private void timersTick()
    {
        if (running) {
            for (int i=0; i<numTimers; i++) {
                if (timesActive[i]) {
                    if (times[i] > 0) {
                        times[i]--;
                    } else {
                        sendPush(i);
                        timesActive[i] = false;
                    }
                }
            }
        }
    }

    private void sendPush(int index)
    {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(index, notifications[index].build());
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            timersTick();
            String timeStr;
            for (int i=0; i<numTimers; i++)
            {
                timeStr = "" + times[i];

                TextView timeRem = findViewById(timeDisplays[i]);
                timeRem.setText(timeFormat(Integer.parseInt(timeStr)));
            }

        }
    };

    public void startAll(View v) {
        // Get associated button to change its text
        Button curBtn = findViewById(R.id.buttonStart);

        running = !running;

        if (running) {
            curBtn.setText("Stop All");
        }
        else {
            curBtn.setText("Start All");
        }
    }


    public void updateTimers() {

        if (running) return; // Can't update timers while running


        // Bunker
        times[0] = 6000 + upgradeLevels[0] * 1200; // 100 mins, 120 mins, 140 mins
        times[1] = 6000 + upgradeLevels[1] * 1200;
        if (timesActive[0] && timesActive[1]) {
            // bunker takes twice as long if both are active
            times[0] *= 2;
            times[1] *= 2;
        }


        // Herbs
        times[2] = 28800;
        if (upgradeLevels[2] == 1) times[2] = 24000;
        else if (upgradeLevels[2] == 2) times[2] = 19080;

        // Rock candy
        times[3] = 36000;
        if (upgradeLevels[3] == 1) times[3] = 28800;
        else if (upgradeLevels[3] == 2) times[3] = 21600;

        // Party sugar
        times[4] = 29880;
        if (upgradeLevels[4] == 1) times[4] = 24000;
        else if (upgradeLevels[4] == 2) times[4] = 18000;

        // Mints
        times[5] = 28800;
        if (upgradeLevels[5] == 1) times[5] = 24000;
        else if (upgradeLevels[5] == 2) times[5] = 19080;

        // Invitations
        times[6] = 28800;
        if (upgradeLevels[6] == 1) times[6] = 18000;
        else if (upgradeLevels[6] == 2) times[6] = 10800;


        // Nightclub popularity
        times[7] = 28800;
        if (upgradeLevels[7] == 1) times[7] = 57600;
    }

    public void resetAll(View v) {
        // resetting times
        times[0] = 6000;
        times[1] = 6000;
        times[2] = 28800;
        times[3] = 36000;
        times[4] = 29880;
        times[5] = 28800;
        times[6] = 28800;
        times[7] = 20160;
        times[8] = 20160;

        Button curBtn = findViewById(R.id.buttonStart);
        curBtn.setText("Start All");
        running = false;

        // reset active checks
        CheckBox curCheck;
        for (int i=0; i<numTimers; i++) {
            curCheck = findViewById(activeChecks[i]);
            curCheck.setChecked(false);
            timesActive[i] = false;

            // reset upgrades
            upgradeLevels[i] = 0;
            curCheck = findViewById(upgradeChecks[i]);
            curCheck.setChecked(false);
        }
        for (int i=numTimers; i<upgradeChecks.length; i++) {
            curCheck = findViewById(upgradeChecks[i]);
            curCheck.setChecked(false);
        }

        updateTimers();
    }
}
