package com.bojie.eggtimer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        timeTextView = (TextView) findViewById(R.id.tv_time);
        mButton = (Button) findViewById(R.id.button);

        setUpSeekBar();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButton.getText() == getString(R.string.btn_go)) {
                    if (secondLeft <= 0) {
                        Toast.makeText(getApplicationContext(), "Please set time by using seekBar on the top.", Toast.LENGTH_LONG).show();
                    } else {
                        mButton.setBackgroundColor(getResources().getColor(R.color.stopColor));
                        mButton.setText(getString(R.string.btn_stop));
                        runningCountdown();
                        mSeekBar.setEnabled(false);
                    }
                } else if (mButton.getText() == getString(R.string.btn_stop)) {
                    mButton.setBackgroundColor(getResources().getColor(R.color.resetColor));
                    mButton.setText(getString(R.string.btn_reset));
                    mSeekBar.setEnabled(true);
                    handler.removeCallbacksAndMessages(null);

                } else if (mButton.getText() == getString(R.string.btn_reset)) {

                    resetAll();

                }
            }
        });
    }

    private void resetAll() {
        mButton.setBackgroundColor(getResources().getColor(R.color.startColor));
        mButton.setText(getString(R.string.btn_go));
        mSeekBar.setProgress(0);
        updateTimeTextview(0);
        mSeekBar.setEnabled(true);
    }

    private void runningCountdown() {
        handler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                if (secondLeft > 0) {
                    updateTimeTextview(secondLeft);
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


}
