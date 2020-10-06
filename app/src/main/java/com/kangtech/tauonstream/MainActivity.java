package com.kangtech.tauonstream;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.kangtech.tauonstream.api.ApiServiceInterface;
import com.kangtech.tauonstream.model.data.musicModel;
import com.kangtech.tauonstream.model.data.updateModel;
import com.kangtech.tauonstream.util.Server;
import com.kangtech.tauonstream.util.SharedPreferencesUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {


    private SimpleExoPlayer player;
    private MediaItem mediaItem;

    private LinearLayout llRefresh;

    private TextView tvStation;

    private TextView tvTitleSong, tvLyrics;
    private ImageView ivCoverAlbum;

    private ApiServiceInterface apiServiceInterface;

    private String titleSong, artistSong, lyricsSong;
    private String coverSong;
    private Float getSongPosition;

    private CardView cvLyrics;

    private ProgressBar pbSong;
    int getSongPositionRound;

    DrawerLayout drawer;
    NavigationView navigationView;

    TextView tvNotSupportChat;

    private TextView tvOnline;

    private Button btnJoin;

    public MainActivity() {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiServiceInterface = Server.getApiServiceInterface();

        tvStation = findViewById(R.id.tv_station);
        tvStation.setText(SharedPreferencesUtils.getString("ip", "Station"));

        llRefresh = findViewById(R.id.ll_nav_refresh);
        ivCoverAlbum = findViewById(R.id.iv_cover_album);
        tvTitleSong = findViewById(R.id.tv_title_song);
        tvLyrics = findViewById(R.id.tv_lyrics);

        cvLyrics = findViewById(R.id.cv_lyrics);

        pbSong = findViewById(R.id.pb_song);


        btnJoin = findViewById(R.id.btn_join);

        tvNotSupportChat = findViewById(R.id.tv_not_support);

        tvTitleSong.setSelected(true);


        player = new SimpleExoPlayer.Builder(this).build();


        /*Intent Stream = new Intent(this, StreamService.class);
        Stream.putExtra("play", "play");
        startService(Stream);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Stream);
        }*/

        if (player.isPlaying()) {
            player.stop(true);
        } else {
            initPlay();
        }


        geDataMusic();

        delayed();

        //customNotification();


        llRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (player.isPlaying()) {
                    player.stop(true);

                    initPlay();
                    geDataMusic();
                } else {
                    initPlay();
                    geDataMusic();
                }
                Log.e("Posisi nya", String.valueOf(player.getCurrentPosition()));


            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        final CoordinatorLayout content = findViewById(R.id.cl_main);

        drawer.setScrimColor(Color.TRANSPARENT);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(-slideX);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);


        LinearLayout llNavChat = findViewById(R.id.ll_nav_chat);
        llNavChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(navigationView);
            }
        });



        Log.e("OnCreate", "yapp");
    }


    private void delayed() {
        int delay = 2000; // 0,5 detik
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                getUpdate();

                delayed();
            }
        },delay);


    }



    public void countDown(final int c){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("KIMMM", (c-1)+ "");
                if(c <= 1){
                    Log.e("KIMMM a", "gone");
                    new Handler().removeCallbacks(this);
                }else{
                    countDown(c-1);
                }
            }
        }, 1000);
    }

    private void initPlay() {
        // Build the media item.
        mediaItem = MediaItem.fromUri("http://" + Server.BASE_URL + ":" + SharedPreferencesUtils.getString("port", "7590") + "/stream.ogg");
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void geDataMusic() {
        apiServiceInterface.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<musicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("gett daaata ", d.toString());
                    }

                    @Override
                    public void onNext(musicModel musicModel) {
                        //loading.show();
                        Log.e("getting ", musicModel.title.toString());

                        titleSong = musicModel.title;
                        artistSong = musicModel.artist;
                        lyricsSong = musicModel.lyrics;
                        coverSong = musicModel.image;

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("gett error ", e.toString());


                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {
                        Log.e("gett daaata ", "aa");

                        byte[] decodedString = Base64.decode(coverSong, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        if (coverSong.equals("None")) {
                            ivCoverAlbum.setImageResource(R.drawable.ic_round_music_note_24);
                        } else {
                            ivCoverAlbum.setImageBitmap(decodedByte);
                        }

                        tvTitleSong.setText(artistSong + " - " + titleSong);

                        if (lyricsSong.isEmpty() || lyricsSong.toString().equals("")) {
                            tvLyrics.setText("No Lyrics");
                            tvLyrics.setTextSize(22);
                            tvLyrics.setGravity(Gravity.CENTER);
                        } else {
                            tvLyrics.setText(Html.fromHtml(lyricsSong));
                            tvLyrics.setTextSize(14);
                            tvLyrics.setGravity(Gravity.NO_GRAVITY);
                        }

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUpdate() {

        apiServiceInterface.getUpdateData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<updateModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("gett dsub1212 ", d.toString());
                    }

                    @Override
                    public void onNext(updateModel updateModel) {

                        getSongPosition = updateModel.position;

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("gett erro12121r ", e.toString());

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {

                        Log.e("pos " , String.valueOf(Math.round(getSongPosition * 100)));
                        getSongPositionRound = Math.round(getSongPosition * 100);

                        if (Math.round(getSongPosition * 100) <= 5 ) {
                            int delay = 2500; // 2,5 detik
                            new Handler().postDelayed(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {

                                    geDataMusic();
                                    pbSong.setProgress(getSongPositionRound);

                                    if (player.isPlaying()) {

                                    } else {
                                        initPlay();
                                    }

                                }
                            },delay);
                        }
                        pbSong.setProgress(getSongPositionRound);

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void customNotification() {
/*        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_round_play_arrow_24)
                .setContentTitle("title")
                .setContentText("Don't CLick Here")
                .setAutoCancel(true)
                .setOngoing(true)
                .setChannelId("313131")
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "313131";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }


        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notification);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content_main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Dialog mDialog;
        mDialog=new Dialog(MainActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.changelog_dialog);
        Button closeDialog;
        closeDialog = mDialog.findViewById(R.id.btn_close_dialog_changelog);
        closeDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                mDialog.cancel();

            }
        });

        switch (item.getItemId()) {
            case R.id.menu_about :
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_opensourcelicense :
                Intent intent2 = new Intent(this, OssLicensesMenuActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_changelog :
                mDialog.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "yapp");

        Intent Stream = new Intent(this, StreamService.class);
        Stream.putExtra("play", "play");
        startService(Stream);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Stream.putExtra("play", "play");
            startForegroundService(Stream);
        }


    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        }

        //super.onBackPressed();
    }
}