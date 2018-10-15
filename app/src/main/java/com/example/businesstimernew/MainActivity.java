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
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    /**
     * Timer notes:
     *
     * safe 48/deposit, 336 to fill (full popularity)
     *
     *
     * no upgrades:
     * bunker 100
     * herbs 480
     * rock candy 600
     * party sugar 498
     * mints 480
     * invitations 300
     *
     * one upgrade:
     * bunker 120
     * herbs 400
     * rock candy 480
     * party sugar 400
     * mints 400
     * invitations 480
     *
     * two upgrades:
     * bunker 140
     * herbs 318
     * rock candy 360
     * party sugar 300
     * mints 318
     * invitations 180
     *
     */

    public final int numTimers = 9;

    public int[] upgradeLevels = {0, 0, 0, 0, 0, 0, 0, 0, 0};

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
            "nightclub safe",
            "nightclub popularity"
    };

    public int[] textViews = {R.id.textView2, R.id.textView4, R.id.textView6, R.id.textView8,
            R.id.textView10, R.id.textView12, R.id.textView14, R.id.textView16, R.id.textView18};

    public int[] buttons = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9};

    public int[] times = {6000, 6000, 28800, 36000, 29880, 28800, 18000, 20160, 20160};


    public boolean[] timesActive = {true, true, true, true, true, true, true, true, true};

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

    public void toggle(int index) {
        timesActive[index] = !timesActive[index];

        // Get associated button
        Button curBtn = findViewById(buttons[index]);

        if (timesActive[index]) {
            curBtn.setText("Deactivate");
        }
        else {
            curBtn.setText("Activate");
        }

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


    private void updateTimers()
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
            updateTimers();
            String timeStr;
            for (int i=0; i<numTimers; i++)
            {
                timeStr = "" + times[i];

                TextView timeRem = findViewById(textViews[i]);
                timeRem.setText(timeFormat(Integer.parseInt(timeStr)));
            }

        }
    };

    public void startAll(View v) {
        // Get associated button
        Button curBtn = findViewById(R.id.buttonStart);

        running = !running;

        if (running) {
            curBtn.setText("Stop All");
        }
        else {
            curBtn.setText("Start All");
        }
    }
}
