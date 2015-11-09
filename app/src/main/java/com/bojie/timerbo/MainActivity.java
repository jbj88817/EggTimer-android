package com.bojie.timerbo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SeekBar mSeekBar;
    TextView timeTextView;
    Button mButton;
    int secondLeft = 0;
    Handler handler;
    Runnable run;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    public static final int mId = 1;
    public static final String TIME_LEFT = "TimeLeft";
    int timeIntentSecondLeft = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        timeTextView = (TextView) findViewById(R.id.tv_time);
        mButton = (Button) findViewById(R.id.button);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        setUpSeekBar();

        setUpButton();

        Intent intent = getIntent();
        timeIntentSecondLeft = intent.getIntExtra(TIME_LEFT, 0);
        if (timeIntentSecondLeft > 0 ){
            startCountdownTimer();
        }
    }

    private void setUpButton() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButton.getText() == getString(R.string.btn_go)) {
                    if (secondLeft <= 0) {
                        Toast.makeText(getApplicationContext(), "Please set time by using seekBar on the top.", Toast.LENGTH_LONG).show();
                    } else {
                        showNotification();
                        startCountdownTimer();
                    }
                } else if (mButton.getText() == getString(R.string.btn_stop)) {
                    mButton.setBackgroundColor(getResources().getColor(R.color.resetColor));
                    mButton.setText(getString(R.string.btn_reset));
                    mNotificationManager.cancelAll();
                    mSeekBar.setEnabled(true);
                    handler.removeCallbacksAndMessages(null);

                } else if (mButton.getText() == getString(R.string.btn_reset)) {

                    resetAll();

                }
            }
        });
    }

    private void startCountdownTimer() {
        mButton.setBackgroundColor(getResources().getColor(R.color.stopColor));
        mButton.setText(getString(R.string.btn_stop));
        runningCountdown();
        mSeekBar.setEnabled(false);
    }

    private void showNotification() {
         mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_action_alarm_on)
                        .setContentTitle("Time Left")
                        .setContentText(formatMilliseconds(secondLeft));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(TIME_LEFT, secondLeft);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private void resetAll() {
        mButton.setBackgroundColor(getResources().getColor(R.color.startColor));
        mButton.setText(getString(R.string.btn_go));
        mSeekBar.setProgress(0);
        updateTimeTextview(0);
        mNotificationManager.cancelAll();
        mSeekBar.setEnabled(true);
    }

    private void runningCountdown() {
        handler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                if (secondLeft > 0) {
                    updateTimeTextview(secondLeft);
                    updateNotification(secondLeft);
                    secondLeft -= 1000;
                    handler.postDelayed(this, 1000);
                } else {
                    // play music
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                    mediaPlayer.start();
                    resetAll();
                }
            }
        };
        handler.post(run);
    }

    private void updateNotification(int secondLeft) {
        int numMessages = 0;
        mBuilder.setContentText(formatMilliseconds(secondLeft))
                .setNumber(++numMessages);
        mNotificationManager.notify(mId,
                mBuilder.build());
    }


    private void setUpSeekBar() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                secondLeft = progress;
                updateTimeTextview(progress);
                if (mButton.getText() == getString(R.string.btn_reset)) {
                    mButton.setBackgroundColor(getResources().getColor(R.color.startColor));
                    mButton.setText(getString(R.string.btn_go));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateTimeTextview(int progress) {
        String seekedTime = formatMilliseconds(progress);
        timeTextView.setText(seekedTime);
    }


    private String formatMilliseconds(int millis) {
        String formated = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        return formated;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (timeIntentSecondLeft > 0 ){
            startCountdownTimer();
        }
    }
}
