package mb.com.mp3player.Service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.io.File;
import java.util.ArrayList;

import mb.com.mp3player.activities.HomePage;
import mb.com.mp3player.R;

/**
 * Created by Anshul on 04-12-17.
 */

public class BackgroundService extends Service {
    static int length=0;
    static Bundle bun;
    String currentSongName;
    public Uri uri;
    static int position;
    public static MediaPlayer mediaPlayer;
    public static ArrayList<File> getSongList;
    ArrayList<String> songNamelist;
    byte[] albumArt;
    private final int NOTIFICATION_ID_CUSTOM_BIG = 9;
    public NotificationCompat.Builder nc;
    public NotificationManager nm;
    public PendingIntent pendingIntent;
    public static final String NOTIFY_PLAY = "mb.com.mp3player.play";
    public static final String NOTIFY_PREVIOUS = "mb.com.mp3player.previous";
    public static final String NOTIFY_CANCEL = "mb.com.mp3player.cancel";
    public static final String NOTIFY_PAUSE = "mb.com.mp3player.pause";
    public static final String NOTIFY_NEXT = "mb.com.mp3player.next";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        if (intent != null&& intent.getExtras() != null ) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            bun = intent.getExtras();
            getSongList = (ArrayList<File>) intent.getSerializableExtra("songList");
            uri = Uri.parse(bun.getString("songUri"));
            albumArt = bun.getByteArray("Albumart");
            currentSongName = bun.getString("CurrentSongName");
            position = bun.getInt("songNumber");
            songNamelist=bun.getStringArrayList("SongNames");
            generateNotification(intent);
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();
            playNextSong(intent);
        }
        return START_STICKY;
    }

    //Function to play next song when one finished.
    public void playNextSong(final Intent intent) {

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (position >= getSongList.size()) {
                    position = 0;
                } else {
                    position++;
                    currentSongName=songNamelist.get(position);
                    HomePage.setName.setText(currentSongName);
                    MediaMetadataRetriever obj = new MediaMetadataRetriever();
                    obj.setDataSource(getSongList.get(position).toString());
                    albumArt = obj.getEmbeddedPicture();
                    generateNotification(intent);
                    if(albumArt==null)
                    {
                        HomePage.img.setImageResource(R.drawable.defaultmusicalbumart);
                    }
                    else{
                        HomePage.img.setImageBitmap(BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length));
                    }
                    try {
                        Thread.sleep(2000);
                        uri = Uri.parse(getSongList.get(position).toString());
                        mediaPlayer = MediaPlayer.create(BackgroundService.this, uri);
                        mediaPlayer.setOnCompletionListener(this);
                        mediaPlayer.start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);
        super.onTaskRemoved(rootIntent);
    }
    //Generating custom notification for song
    @SuppressLint("RestrictedApi")
    public  void generateNotification(Intent notifyIntent) {

        pendingIntent = PendingIntent.getActivity(BackgroundService.this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.music_player_notification);
        setListeners(remoteView);
        //Create Notification using NotificationCompat.Builder
        nc = new NotificationCompat.Builder(getApplicationContext());
        // Set PendingIntent into Notification
        nc.setContentIntent(pendingIntent);
        // Set Icon
        nc.setSmallIcon(R.drawable.playbutton);
        //set Title on music player notification when screen is locked
        nc.setContentTitle("Music Player");
        //set name of song on music player notification when screen is locked
        nc.setContentText(currentSongName);
        nc.setCustomBigContentView(remoteView);
        nc.setOngoing(true);
        setListeners(remoteView);
        //set current song name on music player notification
        nc.getBigContentView().setTextViewText(R.id.songdes, currentSongName);
        //set album art of current song in notification builder
        if (albumArt != null) {
            nc.getBigContentView().setImageViewBitmap(R.id.songimg, BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length));
        } else {
            nc.getBigContentView().setImageViewResource(R.id.songimg, R.drawable.defaultmusicalbumart);
        }
        //Create Notification Manager
        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //Build Notification with Notification Manager
        nm.notify(NOTIFICATION_ID_CUSTOM_BIG, nc.build());


    }

    public void setListeners(RemoteViews view) {
       // Intent notificationIntent = new Intent(this, HandleNotificationIntent.class);
        Intent previousintent = new Intent(NOTIFY_PREVIOUS);
        Intent cancelintent = new Intent(NOTIFY_CANCEL);
        Intent pauseintent = new Intent(NOTIFY_PAUSE);
        Intent nextintent = new Intent(NOTIFY_NEXT);
        Intent playintent = new Intent(NOTIFY_PLAY);
        PendingIntent pause = PendingIntent.getBroadcast(this, 0, pauseintent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.pausebtn, pause);
        PendingIntent play = PendingIntent.getBroadcast(this, 0, playintent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.playBtn, play);
        PendingIntent next = PendingIntent.getBroadcast(this, 0, nextintent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.forwordbtn,next);
        PendingIntent previous = PendingIntent.getBroadcast(this, 0, previousintent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.backwordbtn,previous);
        PendingIntent cancel = PendingIntent.getBroadcast(this, 0, cancelintent,PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.cancelbtn,cancel);
    }
    public static void pauseSong()
    {
        mediaPlayer.pause();
        length = mediaPlayer.getCurrentPosition();
    }
    public static void resumeSong()
    {
        mediaPlayer.seekTo(length);
        mediaPlayer.start();
    }


    @Override
    public  void onDestroy() {
        mediaPlayer.stop();
        nm.cancelAll();
    }
    public void setCurrentPosition(int currentPosition) {
        this.position = currentPosition;
    }

    public static int getCurrrentPosition() {
        return position;
    }

}

