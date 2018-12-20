package com.belaku.media;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abdularis.civ.CircleImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class PlayerActivity extends AppCompatActivity {

    private static final int EQ_CODE = 2;
    private Intent playerintent;
    private ImageButton ImgBtnPlayPause, ImgBtnNext, ImgBtnPrev, ImgBtnff, ImgBtnrev, ImgBtnLyrics, ImgBtnEqualizer;
    public static MediaPlayer MyMediaPlayer;
    private static final String CHANNEL_ID = "myMusicService";
    //   public ArrayList<AudioSong> mAudioSongs;
    private int songPosition;
    private TextView TxTitle, TxArtist, TxAlbum, TxTotal, TxPresent;
    private CircleImageView AlbumArtImgV;
    private Handler mHandler = new Handler();
    private CircularSeekBar cSeekbar;
    private String songsAsString;
    private boolean repeat, repaetOne, shuffle;
    private int TimeToPlay = 0;
    RelativeLayout relativeLayout;
    float volume = 0;
    private Spinner TimerSpinner;
    private String artistName, albumName;
    private ArrayList<AudioSong> artistSongs = new ArrayList<>(), albumSongs = new ArrayList<>();
    private FloatingActionButton fabBg;
    private ImageView ImgvTimer, Imgvrepeat, ImgvShuffle, ImgvFav, ImgvPl;
    private MainActivity mainActivity = new MainActivity();
    public ArrayList<AudioSong> mAudioSongs;
    private Random rand;
    private ListView listViewCreatePL;
    private Button btnOkCreatePL, btnCancelCreatePL;
    private EditText edtxPlNameCreatePL;
    private ArrayList<String> playlistNames = new ArrayList<>();
    private AudioSong plAudioSong;
    private Playlist playlist;
    private ArrayList<AudioSong> plAudioSongs = new ArrayList<>();
    private ArrayList<String> playlistSongnames = new ArrayList<>();
    private Intent serviceIntent;
    private Thread colorThread;
    static final int SELECT_PHOTO = 1;
    private int iArtist, iAlbum;
    private Equalizer mEqualizer;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init();


        RotateAnimation rotateAnim;
        rotateAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setRepeatCount(0); //Repeat animation indefinitely
        rotateAnim.setDuration(3000); //Put desired duration per anim cycle here, in milliseconds

        TimerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                TimeToPlay = i * 60000;

                if (TimeToPlay != 0) {
                    makeToast("Each song will be played for " + i + " minute");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startFadeOut();
                            //  explosionField.explode(AlbumArtImgV);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    repeat = true;
                                    if (MyMediaPlayer.isPlaying())
                                        nextSong();
                                }
                            }, 5000);
                        }
                    }, TimeToPlay);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ImgvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0 ; i < mainActivity.mAudioSongs.size(); i++) {
                    if (songPosition < mAudioSongs.size())
                    if (mainActivity.mAudioSongs.get(i).getTitle().equals(mAudioSongs.get(songPosition).getTitle()))
                        songPosition = i;
                }
                if (mainActivity.mAudioSongs.get(songPosition).isFavorite) {
                    ImgvFav.setImageResource(R.drawable.fav_disabled);
                    mainActivity.mAudioSongs.get(songPosition).isFavorite = false;
                } else {
                    ImgvFav.setImageResource(R.drawable.fav);
                    mainActivity.mAudioSongs.get(songPosition).isFavorite = true;
                }
            }
        });

        ImgvPl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPLdialog(getCurrentFocus());
            }
        });


        ImgvTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TimerSpinner != null) {
                    makeToast("Set timer for each Song");
                    TimerSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        ImgvShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffle) {
                    shuffle = false;
                    ImgvShuffle.setImageResource(R.drawable.shuffle_disabled);
                } else {
                    shuffle = true;
                    ImgvShuffle.setImageResource(R.drawable.shuffle);
                }

            }
        });

        Imgvrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeat) {
                    repeat = false;
                    Imgvrepeat.setImageResource(R.drawable.repeat_one);
                    repaetOne = true;
                } else if (repaetOne) {
                    repaetOne = false;
                    repeat = false;
                    Imgvrepeat.setImageResource(R.drawable.repeat_all_disabled);
                } else {
                    repeat = true;
                    Imgvrepeat.setImageResource(R.drawable.repeat_all);
                    repaetOne = false;
                }
            }
        });


        ImgBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
                updateProgressBar();
            }
        });

        ImgBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousSong();
                updateProgressBar();
            }
        });

        ImgBtnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyMediaPlayer.getCurrentPosition() < MyMediaPlayer.getDuration() - 5000)
                    MyMediaPlayer.seekTo(MyMediaPlayer.getCurrentPosition() + 5000);
                else if (MyMediaPlayer.getCurrentPosition() < MyMediaPlayer.getDuration() - 3000)
                    MyMediaPlayer.seekTo(MyMediaPlayer.getCurrentPosition() + 5000);
                else makeToast("Can't ff to 3s / 5s");

            }
        });

        ImgBtnrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyMediaPlayer.getCurrentPosition() > 5000)
                    MyMediaPlayer.seekTo(MyMediaPlayer.getCurrentPosition() - 5000);
                else if (MyMediaPlayer.getCurrentPosition() < 3000)
                    MyMediaPlayer.seekTo(MyMediaPlayer.getCurrentPosition() - 5000);
                else makeToast("Can't rewind to 3s / 5s");

            }
        });

        ImgBtnEqualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MyMediaPlayer.getAudioSessionId());
                startActivityForResult(i, EQ_CODE);
            }
        });

        ImgBtnLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isQLInstalled();
            }
        });

        playerintent = getIntent();
        //   if (playerintent.getExtras() != null)
        if (playerintent != null && playerintent.getExtras() != null) {
            if (playerintent.getExtras().get("position") != null) {
                Bundle bundle = getIntent().getExtras();
                String jsonString = bundle.getString("KEY");
                songPosition = (int) bundle.get("position");

                Gson gson = new Gson();
                Type listOfdoctorType = new TypeToken<List<AudioSong>>() {
                }.getType();
                mAudioSongs = gson.fromJson(jsonString, listOfdoctorType);


                if (bundle.get("Pageposition") != null) {
                    if (Integer.valueOf((Integer) bundle.get("Pageposition")) == 0) {
                        makeToast("ARTISTpageINTENT");
                        artistName = mainActivity.mAudioSongs.get(songPosition).getArtist();
                    } else if (Integer.valueOf((Integer) bundle.get("Pageposition")) == 1) {
                        makeToast("SONGpageINTENT");
                    } else if (Integer.valueOf((Integer) bundle.get("Pageposition")) == 2) {
                        makeToast("ALBUMpageINTENT");
                        albumName = mainActivity.mAudioSongs.get(songPosition).getAlbum();
                    }
                }


                makeToast("starting service from playerActivity");

                Gson gson2 = new Gson();
                songsAsString = gson2.toJson(mAudioSongs);


                playSong(songPosition);


                ImgBtnPlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyMediaPlayer = MusicService.getInstance();

                        if (MyMediaPlayer.isPlaying()) {
                            MyMediaPlayer.pause();
                            ImgBtnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                        } else {
                            MyMediaPlayer.start();
                            ImgBtnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                        }

                    }
                });


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO :
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        AlbumArtImgV.setImageBitmap(selectedImage);
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.PNG,100, baos);
                        byte [] b=baos.toByteArray();
                        String temp=Base64.encodeToString(b, Base64.DEFAULT);
                        mAudioSongs.get(songPosition).setAlbumArt(temp);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case EQ_CODE :
                if (resultCode == RESULT_OK) {
                    return;
                }
                break;
        }
    }


    public void showPLdialog(View view) {

        LayoutInflater factory = LayoutInflater.from(this);
        final View plDialogView = factory.inflate(R.layout.playlist_view_dialog, null);
        final AlertDialog plDialog = new AlertDialog.Builder(this).create();
        listViewCreatePL = plDialogView.findViewById(R.id.dialoglist);
        edtxPlNameCreatePL = plDialogView.findViewById(R.id.edtx_pl_name);
        btnOkCreatePL = plDialogView.findViewById(R.id.btn_new_pl);
        btnCancelCreatePL = plDialogView.findViewById(R.id.btn_cancel);
        {
            plDialog.setTitle("Add to Playlist");
            plDialog.setView(plDialogView);
            if (mainActivity.playlists.size() > 0) {
                edtxPlNameCreatePL.setVisibility(View.INVISIBLE);
                listViewCreatePL.setVisibility(View.VISIBLE);
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, playlistNames);
                listViewCreatePL.setAdapter(arrayAdapter);
                listViewCreatePL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            makeToast(adapterView.getItemAtPosition(i).toString());
                            for (int z = 0; z < mainActivity.mAudioSongs.size(); z++) {
                                if (mainActivity.mAudioSongs.get(z).getTitle().equals(mainActivity.mAudioSongs.get(songPosition).getTitle()))
                                    plAudioSongs.add(mainActivity.mAudioSongs.get(z));
                            }
                            playlist.setPlSongs(plAudioSongs);
                            makeToast("Songs in the playlist : \n");
                            for (int p = 0 ; p < playlist.getPlSongs().size(); p++)
                                makeToast(playlist.getPlSongs().get(p).getTitle());
                    }
                });
            }

            btnOkCreatePL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnOkCreatePL.getText().toString().equals("Create Playlist")) {
                        edtxPlNameCreatePL.setVisibility(View.INVISIBLE);
                        if (!edtxPlNameCreatePL.getText().toString().trim().equals("")) {
                            playlist = new Playlist(edtxPlNameCreatePL.getText().toString());
                            mainActivity.playlists.add(playlist);
                        }
                        listViewCreatePL.setVisibility(View.VISIBLE);
                        if (mainActivity.playlists.size() > 0) {
                            for (int i = 0 ; i < mainActivity.playlists.size(); i++) {
                                playlistNames.add(mainActivity.playlists.get(i).getPlName());
                            }
                            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, playlistNames);
                            listViewCreatePL.setAdapter(arrayAdapter);
                        }
                        btnOkCreatePL.setText("New Playlist");
                    } else if (btnOkCreatePL.getText().toString().equals("New Playlist")) {
                        edtxPlNameCreatePL.setVisibility(View.VISIBLE);
                        listViewCreatePL.setVisibility(View.INVISIBLE);
                        btnOkCreatePL.setText("Create Playlist");
                    }
                }
            });


            plDialog.show();
        }
    }


    private void init() {


        relativeLayout = findViewById(R.id.p_layout);
        AlbumArtImgV = findViewById(R.id.imgv_albumart);
        cSeekbar = findViewById(R.id.c_seekbar);



/*
        colorThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!colorThread.isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                Random rnd = new Random();
                                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                                cSeekbar.setCircleProgressColor(color);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
*/

//        cSeekbar.setCircleProgressColor(android.R.color.transparent);
    //    colorThread.start();


        cSeekbar.setX(AlbumArtImgV.getX());
        cSeekbar.setY(AlbumArtImgV.getY());
        TxTitle = findViewById(R.id.tx_title);
        TxArtist = findViewById(R.id.tx_artist);
        TxAlbum = findViewById(R.id.tx_album);
        fabBg = findViewById(R.id.fab_bg);

        TimerSpinner = findViewById(R.id.timer_spinner);
        TimerSpinner.setVisibility(View.INVISIBLE);
        Integer[] items = new Integer[]{0, 1, 2, 3, 4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(PlayerActivity.this, R.layout.spinner_item, items);
        TimerSpinner.setAdapter(adapter);
        TimerSpinner.setSelection(0, false);

        TxTotal = findViewById(R.id.tx_total_duration);
        TxTotal.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "TxTP.ttf")));
        TxPresent = findViewById(R.id.tx_current_duration);
        TxPresent.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "TxTP.ttf")));

        ImgBtnPlayPause = findViewById(R.id.img_btn_playpause);
        ImgBtnNext = findViewById(R.id.imgbtn_next);
        ImgBtnPrev = findViewById(R.id.imgbtn_prev);
        ImgBtnff = findViewById(R.id.imgbtn_ff);
        ImgBtnrev = findViewById(R.id.imgbtn_rev);
        ImgBtnNext.setVisibility(View.INVISIBLE);
        ImgBtnPrev.setVisibility(View.INVISIBLE);
        ImgBtnff.setVisibility(View.INVISIBLE);
        ImgBtnrev.setVisibility(View.INVISIBLE);
        AlbumArtImgV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ImgBtnNext.bringToFront();
                ImgBtnPrev.bringToFront();
                ImgBtnff.bringToFront();
                ImgBtnrev.bringToFront();
                if (ImgBtnNext.getVisibility() == View.INVISIBLE) {
                    ImgBtnNext.setVisibility(View.VISIBLE);
                    ImgBtnPrev.setVisibility(View.VISIBLE);
                    ImgBtnff.setVisibility(View.VISIBLE);
                    ImgBtnrev.setVisibility(View.VISIBLE);
                } else if (ImgBtnNext.getVisibility() == View.VISIBLE) {
                    ImgBtnNext.setVisibility(View.INVISIBLE);
                    ImgBtnPrev.setVisibility(View.INVISIBLE);
                    ImgBtnff.setVisibility(View.INVISIBLE);
                    ImgBtnrev.setVisibility(View.INVISIBLE);
                }

                return true;
            }
        });

        AlbumArtImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        ImgBtnEqualizer = findViewById(R.id.imgbtn_music_equalizer);
        ImgBtnLyrics = findViewById(R.id.imgbtn_lyrics);
        ImgvTimer = findViewById(R.id.imgv_timer);
        Imgvrepeat = findViewById(R.id.imgv_repeat);
        ImgvShuffle = findViewById(R.id.imgv_shuffle);
        ImgvFav = findViewById(R.id.imgv_fav);
        ImgvPl = findViewById(R.id.imgv_pl);
    }


    @Override
    protected void onResume() {
        super.onResume();
        makeToast("onResume !");

    }



    public void updateProgressBar() {

        cSeekbar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = MyMediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer((int) seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                MyMediaPlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });

        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void previousSong() {
        //   fadeIn(MyMediaPlayer, 10000);

        if (repaetOne) {
            playSong(songPosition);
        } else if (shuffle) {
            Random ran = new Random();
            int randomNum = ran.nextInt(mainActivity.mAudioSongs.size()) + 0;
            makeToast("Random - " + randomNum);
            playSong(randomNum);
        } else if (repeat) {

            if (artistName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getArtist().equals(artistName)) {
                        artistSongs.add(mainActivity.mAudioSongs.get(i));
                    }
                }
                int noOfArtistSongs = artistSongs.size();
                for (int i = 0 ; i < artistSongs.size(); i++) {
                    if (artistSongs.get(i).getTitle().equals(mAudioSongs.get(songPosition).getTitle()))
                        iArtist = i;
                }
                if (iArtist != 0)
                playSong(iArtist--);
                else {
                    iArtist = artistSongs.size();
                    playSong(iArtist--);
                }

            } else if (albumName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getAlbum().equals(albumName)) {
                        albumSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            }

            // check if next song is there or not
            if (songPosition > 0) {

                playSong(songPosition - 1);
                songPosition = songPosition - 1;

                if (TimeToPlay != 0) {
                    //   TxTimer.setText("Each song will be played for " + i + " minute");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startFadeOut();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    repeat = true;
                                    nextSong();
                                }
                            }, 5000);
                        }
                    }, TimeToPlay);
                }
            } else {
                // play last song
                playSong(mainActivity.mAudioSongs.size() - 1);
                songPosition = mainActivity.mAudioSongs.size() - 1;
            }
        } else {

            if (artistName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getArtist().equals(artistName)) {
                        artistSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            } else if (albumName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getAlbum().equals(albumName)) {
                        albumSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            }

            // check if next song is there or not
            if (songPosition > 0) {

                playSong(songPosition - 1);
                songPosition = songPosition - 1;

                if (TimeToPlay != 0) {
                    //   TxTimer.setText("Each song will be played for " + i + " minute");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startFadeOut();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    repeat = true;
                                    nextSong();
                                }
                            }, 5000);
                        }
                    }, TimeToPlay);
                }
            } else {
                makeToast("You're playing the first song in the list");

            }
        }
    }


    private void nextSong() {
        //   fadeIn(MyMediaPlayer, 10000);

        if (repaetOne) {
            playSong(songPosition);
        } else if (shuffle) {
            Random ran = new Random();
            int randomNum = ran.nextInt(mainActivity.mAudioSongs.size()) + 0;
            makeToast("Random - " + randomNum);
            playSong(randomNum);
        } else if (repeat) {

            if (artistName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getArtist().equals(artistName)) {
                        artistSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            } else if (albumName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getAlbum().equals(albumName)) {
                        albumSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            }

            // check if next song is there or not
            if (songPosition < (mainActivity.mAudioSongs.size() - 1)) {

                playSong(songPosition + 1);
                songPosition = songPosition + 1;

                if (TimeToPlay != 0) {
                    //   TxTimer.setText("Each song will be played for " + i + " minute");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startFadeOut();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    repeat = true;
                                    nextSong();
                                }
                            }, 5000);
                        }
                    }, TimeToPlay);
                }
            } else {
                // play first song
                playSong(0);
                songPosition = 0;
            }
        } else {

            if (artistName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getArtist().equals(artistName)) {
                        artistSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            } else if (albumName != null) {
                for (int i = 0; i < mainActivity.mAudioSongs.size(); i++) {
                    if (mainActivity.mAudioSongs.get(i).getAlbum().equals(albumName)) {
                        albumSongs.add(mainActivity.mAudioSongs.get(i));
                        playSong(i);
                    }
                }
            }

            // check if next song is there or not
            if (songPosition < (mainActivity.mAudioSongs.size() - 1)) {

                playSong(songPosition + 1);
                songPosition = songPosition + 1;

                if (TimeToPlay != 0) {
                    //   TxTimer.setText("Each song will be played for " + i + " minute");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startFadeOut();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    repeat = true;
                                    nextSong();
                                }
                            }, 5000);
                        }
                    }, TimeToPlay);
                }
            } else {
                // play first song
                makeToast("You're playing the last song in the list");
            }
        }
    }

    private void startFadeOut() {
        final int FADE_DURATION = 10000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final float MIN_VOLUME = (float) 0.1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MIN_VOLUME / (float) numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeOutStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if (volume <= 1f) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);
    }

    // working fine !
    private void startFadeIn() {
        final int FADE_DURATION = 10000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MAX_VOLUME / (float) numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if (volume >= 1f) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);
    }

    private void fadeInStep(float deltaVolume) {
        //  if (MyMediaPlayer != null)
        try {
            MyMediaPlayer.setVolume(volume, volume);
        } catch (Exception ex) {
            Log.d("MyMediaPlayerEXCP", ex.toString());
        }
        volume += deltaVolume;
        Log.d("Vol217 - fadeInStep", String.valueOf(volume));

    }

    private void fadeOutStep(float deltaVolume) {
        //    if (MyMediaPlayer != null)
        try {
            MyMediaPlayer.setVolume(volume, volume);
        } catch (Exception ex) {
            Log.d("MyMediaPlayerEXCP", ex.toString());
        }
        volume -= deltaVolume;
        Log.d("Vol217 - fadeOutStep", String.valueOf(volume));
    }


    private int getDeviceVolume() {

        // Get the AudioManager instance
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);

        int music_volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        return music_volume_level;
    }

    public void playSong(int songIndex) {

        startFadeIn();
        ImgBtnPlayPause.setImageResource(android.R.drawable.ic_media_pause);

        getAlbumArt(songIndex);

        TxTitle.setText(mAudioSongs.get(songIndex).getTitle());
        TxArtist.setText(mAudioSongs.get(songIndex).getArtist());
        TxAlbum.setText(mAudioSongs.get(songIndex).getAlbum());

        TxTitle.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "TxTAA.ttf")));
        TxArtist.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "TxTAA.ttf")));
        TxAlbum.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "TxTAA.ttf")));

        if (mAudioSongs.get(songIndex).isFavorite)
            ImgvFav.setImageResource(R.drawable.fav);
        else ImgvFav.setImageResource(R.drawable.fav_disabled);

        serviceIntent = new Intent(PlayerActivity.this, MusicService.class)
                .putExtra("song_path", mAudioSongs.get(songIndex).getPath())
                .putExtra("song_name", mAudioSongs.get(songIndex).getTitle())
                .putExtra("song_position", songPosition)
                .putExtra("KEY", songsAsString);
        startService(serviceIntent);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MusicService.getInstance() != null) {
                    MyMediaPlayer = MusicService.getInstance();
                    updateProgressBar();
                }
                MyMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.d("HereAmI", "completedSinging :)");

                        stopService(new Intent(PlayerActivity.this, MusicService.class));

                        if (repaetOne) {
                            playSong(songPosition);
                        } else if (shuffle) {
                            Random ran = new Random();
                            int randomNum = ran.nextInt(mainActivity.mAudioSongs.size()) + 0;
                            makeToast("Random - " + randomNum);
                            playSong(randomNum);
                        } else if (repeat) {
                            if (songPosition < mAudioSongs.size() - 1)
                                playSong(songPosition + 1);
                            else {
                                playSong(0);
                                songPosition = 0;
                            }
                        } else {
                            if (songPosition < mAudioSongs.size() - 1)
                                playSong(songPosition + 1);
                            else {
                                makeToast("You've completed playing the last song in the list");
                            }
                        }

                    }
                });
            }
        }, 3000);
    }


    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public static int getOppositeColor(int color) {
        // get existing colors
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        // find compliments
        red = (~red) & 0xff;
        blue = (~blue) & 0xff;
        green = (~green) & 0xff;

        return Color.argb(alpha, red, green, blue);
    }

    public boolean isQLInstalled() {

        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            pm.getPackageInfo("com.geecko.QuickLyric", PackageManager.GET_ACTIVITIES);
            startActivity(new Intent("com.geecko.QuickLyric.getLyrics").putExtra("TAGS", new String[]{TxAlbum.getText().toString(), TxTitle.getText().toString()}));
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.geecko.QuickLyric")));
            return false;
        }
    }

    private void getAlbumArt(int songIndex) {


        if (mAudioSongs.get(songIndex).getAlbumArt() != null) {
            AlbumArtImgV.setImageURI(Uri.parse(mAudioSongs.get(songIndex).getAlbumArt()));
            //    ImgBtnPlayPause.setImageURI(Uri.parse(mAudioSongs.get(songIndex).getAlbumArt()));
            fabBg.setImageURI(Uri.parse(mAudioSongs.get(songIndex).getAlbumArt()));
        } else {
            AlbumArtImgV.setImageResource(R.drawable.mm_icon);
            fabBg.setImageResource(R.drawable.mm_icon);
        }

        AlbumArtImgV.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) AlbumArtImgV.getDrawable();
        Bitmap bm = drawable.getBitmap();

        relativeLayout.setBackground(new BitmapDrawable(getResources(), BlurBuilder(fabBg)));


        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        AlbumArtImgV.startAnimation(shake);

    }

    public Bitmap BlurBuilder(FloatingActionButton fab) {
        final float BITMAP_SCALE = 2.5f;
        final float BLUR_RADIUS = 25f;


        int width = Math.round(fab.getWidth() * BITMAP_SCALE);
        int height = Math.round(fab.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = ((BitmapDrawable) fab.getDrawable()).getBitmap();

        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(getApplicationContext());
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;

    }


    private Utilities utils = new Utilities();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = MyMediaPlayer.getDuration();
            long currentDuration = MyMediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            TxTotal.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            TxPresent.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            cSeekbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (MyMediaPlayer.isPlaying())
            Note();
    }


    private void Note()  {


        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_note);

        AlbumArtImgV.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) AlbumArtImgV.getDrawable();
        Bitmap bitmapIcon = drawable.getBitmap();

        notificationLayout.setTextViewText(R.id.song_title, mainActivity.mAudioSongs.get(songPosition).getTitle() +
                                                                 "\n" + mainActivity.mAudioSongs.get(songPosition).getAlbum() +
                                                                 "\n" + mainActivity.mAudioSongs.get(songPosition).getArtist());
        notificationLayout.setImageViewBitmap(R.id.image_note, bitmapIcon);
        notificationLayout.setImageViewResource(R.id.play_pause, android.R.drawable.ic_media_pause);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myChannel";
            String description = "myChannelDesc";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel nChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            nChannel.setDescription(description);
            nChannel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(nChannel);

            Intent intent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mm_icon);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.mm_icon)
                    .setLargeIcon(bitmap)
                    .setCustomBigContentView(notificationLayout)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);



// notificationId is a unique int for each notification that you must define
            int notificationId = 21;
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }


    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public class DownloadCancelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("HellYAYYYYYYYY", "NotificationClick");
            Toast.makeText(context, "Received Cancelled Event", Toast.LENGTH_SHORT).show();
            makeToast("Received Cancelled Event");
        }
    }

}
