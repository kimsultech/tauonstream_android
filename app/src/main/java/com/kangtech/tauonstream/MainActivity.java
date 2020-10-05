package com.kangtech.tauonstream;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;


import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.kangtech.sc_client.Ack;
import com.kangtech.sc_client.BasicListener;
import com.kangtech.sc_client.Emitter;
import com.kangtech.sc_client.ReconnectStrategy;
import com.kangtech.sc_client.Socket;
import com.kangtech.tauonstream.api.ApiServiceInterface;
import com.kangtech.tauonstream.model.data.musicModel;
import com.kangtech.tauonstream.model.data.updateModel;
import com.kangtech.tauonstream.util.Server;
import com.kangtech.tauonstream.util.SharedPreferencesUtils;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.graphics.Typeface.BOLD;


public class MainActivity extends AppCompatActivity {


    private SimpleExoPlayer player;
    private MediaItem mediaItem;

    private LinearLayout llRefresh;
    private LottieAnimationView lavRefresh;

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

    int portOri = Integer.parseInt(SharedPreferencesUtils.getString("port", "7501"));
    int port = portOri + 1;

    String url="ws://" + Server.BASE_URL + ":" + port + "/socketcluster/";
    Socket socket;
    EmojiconEditText emojiconEditText;
    ImageView emojiButton;
    NestedScrollView scrollView;
    ImageView submitButton;
    View rootView;
    LinearLayout container;
    EmojIconActions emojIcon;
    EditText username;
    String Username="<font color=\"#ff00000\">Demo</font>";
    Handler Typinghandler=new Handler();
    Boolean typing=false;

    TextView tvNotSupportChat;

    private TextView tvOnline;
    private String countOnline;

    private Button btnJoin;
    boolean keyboardOpen = false;

    //Bind/Unbind music service
    private boolean mIsBound = false;
    private StreamService mServ;

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
        lavRefresh = findViewById(R.id.lav_refresh);
        ivCoverAlbum = findViewById(R.id.iv_cover_album);
        tvTitleSong = findViewById(R.id.tv_title_song);
        tvLyrics = findViewById(R.id.tv_lyrics);

        cvLyrics = findViewById(R.id.cv_lyrics);

        pbSong = findViewById(R.id.pb_song);

        tvOnline = findViewById(R.id.tv_online);

        btnJoin = findViewById(R.id.btn_join);

        tvNotSupportChat = findViewById(R.id.tv_not_support);

        tvTitleSong.setSelected(true);


        player = new SimpleExoPlayer.Builder(this).build();

        player.addListener(new Player.EventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {

                    if (player.getCurrentPosition() == 0) {

                    }
                } else {

                }
            }
        });

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

        liveChat();


        llRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lavRefresh.playAnimation();

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


        JSONObject object=new JSONObject();
        try {
            object.put("ismessage",false);
            object.put("data","<b><gray>"+Username+"</b></gray> leaved the chat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.publish("livechat",object);

    }

    private void liveChat() {

        socket = new Socket(url);

        socket.setListener(new BasicListener() {

            public void onConnected(Socket socket, Map<String, List<String>> headers) {
                socket.createChannel("livechat").subscribe(new Ack() {
                    @Override
                    public void call(String name, Object error, Object data) {
                        if (error==null){
                            Log.e ("Success apaannnn","subscribed to channel "+ data);
                        }
                    }
                });
                Log.i("Success ","Connected to endpoint");

                tvNotSupportChat.setVisibility(View.GONE);
                btnJoin.setVisibility(View.VISIBLE);
            }

            public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                Log.i("Success ","Disconnected from end-point");
            }

            public void onConnectError(Socket socket, WebSocketException exception) {
                tvNotSupportChat.setVisibility(View.VISIBLE);
                btnJoin.setVisibility(View.GONE);

                Log.e("Success ","Got connect error "+ exception);
            }

            public void onSetAuthToken(String token, Socket socket) {
                socket.setAuthToken(token);
            }

            public void onAuthentication(Socket socket,Boolean status) {
                if (status) {
                    Log.i("Success ","socket is authenticated");
                } else {
                    Log.i("Success ","Authentication is required (optional)");
                }
            }

        });

        /*
        Socket.Channel channel = socket.getChannelByName("MyClassroom");
        */
        socket.setReconnection(new ReconnectStrategy().setMaxAttempts(10).setDelay(3000));

        socket.connectAsync();

        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        emojiButton.setVisibility(View.GONE);

        submitButton = (ImageView) findViewById(R.id.submit_btn);
        submitButton.setVisibility(View.GONE);

        container = (LinearLayout) findViewById(R.id.container);
        scrollView =  findViewById(R.id.scroll);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojiconEditText.setVisibility(View.GONE);

        emojIcon = new EmojIconActions(this,rootView,emojiconEditText,emojiButton,"#495C66","#DCE1E2","#E6EBEF");
        emojIcon.ShowEmojIcon();
        emojIcon.setUseSystemEmoji(true);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {

            @Override
            public void onKeyboardOpen() {
                keyboardOpen = true;
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard","close");
            }
        });



        View v = getLayoutInflater().inflate(R.layout.dialogue_layout, null);
        username =  v.findViewById(R.id.username_input);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinChat();
            }

            private void joinChat() {
                builder.setView(v)
                        .setCancelable(false)
                        .setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                if (username.getText().length() == 0 || username.getText().toString().equals("")) {
                                    Toast.makeText(MainActivity.this, "Are you Sure?\ndid you forget something?", Toast.LENGTH_LONG).show();
                                } else {
                                    // sign in the user ...
                                    Username = username.getText().toString();
                                    JSONObject object=new JSONObject();
                                    try {
                                        object.put("ismessage",false);
                                        object.put("data","<b><gray>"+Username+"</b></gray> joined to chat");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    socket.publish("livechat", object);

                                    submitButton.setVisibility(View.VISIBLE);
                                    emojiButton.setVisibility(View.VISIBLE);
                                    emojiconEditText.setVisibility(View.VISIBLE);

                                    btnJoin.setVisibility(View.GONE);
                                }

                            }
                        })
                        .create()
                        .show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject object=new JSONObject();
                try {
                    object.put("user",Username);
                    object.put("ismessage",true);
                    object.put("data",emojiconEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.publish("livechat", object, new Ack() {
                    @Override
                    public void call(String name, Object error, Object data) {
                        if (error==null){
                            Log.i ("Success","Publish sent successfully");
                        }
                    }
                });

                emojiconEditText.setText("");
            }
        });

/*        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Username==null) return;

                if (!typing){
                    typing=true;
                    if (!s.equals("")) {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("user", Username);
                            object.put("istyping", true);
                            object.put("data", "<b>" + Username + "</b> is typing...");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        socket.publish("livechat",object);
                    }
                }

*//*                Typinghandler.removeCallbacks(onTypingTimeout);
                Typinghandler.postDelayed(onTypingTimeout,600);*//*

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        socket.onSubscribe("livechat",new Emitter.Listener() {
            @Override
            public void call(String name, final Object data) {

                try {
                    JSONObject object= (JSONObject) data;
                    if (object.opt("istyping") != null &&  !object.getString("user").equals(Username)){
                       /* if (object.getBoolean("istyping")){
                            final TextView textView=getTextView();
                            textView.setText(Html.fromHtml(object.getString("data")));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    container.addView(textView);
                                    scrollView.scrollTo(0,scrollView.getBottom());
                                }
                            });
                        }else{*/
                            Log.i ("success","Stopped typing");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    container.removeViewAt(container.getChildCount()-1);
                                }
                            });
                        /*}*/

                    } else {
                        final TextView textView = getTextView();


                                /*countOnline = String.valueOf(data);*/
                        

                        if (!object.getBoolean("ismessage")) {
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                            textView.setText(Html.fromHtml(object.getString("data")), TextView.BufferType.SPANNABLE);
                        } else {
                            textView.setText(Html.fromHtml("<b>" + object.getString("user") + "</b> : " + object.getString("data")), TextView.BufferType.SPANNABLE);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                container.addView(textView);
                                scrollView.scrollTo(0, scrollView.getBottom());

                                /*tvOnline.setText(countOnline);*/
                                //Log.e("Count Online",countOnline);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private EmojiconTextView getTextView(){
        EmojiconTextView textView = new EmojiconTextView(MainActivity.this);
        textView.setUseSystemDefault(false);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setEmojiconSize(44);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 5);
        textView.setLayoutParams(params);
        return textView;
    }
/*    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!typing) return;

            typing = false;
//            mSocket.emit("stop typing");
            JSONObject object=new JSONObject();
            try {
                object.put("user",Username);
                object.put("istyping",false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.publish("livechat",object);
        }
    };*/

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        }

        //super.onBackPressed();
    }
}