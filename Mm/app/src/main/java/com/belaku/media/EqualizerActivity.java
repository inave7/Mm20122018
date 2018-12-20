package com.belaku.media;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EqualizerActivity extends AppCompatActivity {

    private MediaPlayer MyMediaPlayer;
    private Equalizer mEqualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);


    }
}
