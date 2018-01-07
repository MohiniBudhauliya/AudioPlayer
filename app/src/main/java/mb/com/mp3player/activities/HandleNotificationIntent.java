package mb.com.mp3player.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import mb.com.mp3player.R;
import mb.com.mp3player.Service.BackgroundService;
import mb.com.mp3player.fragment.SongFragment;

/**
 * Created by Anshul on 12-12-17.
 */



public class HandleNotificationIntent extends BroadcastReceiver {

    MediaPlayer mediaPlayer= BackgroundService.mediaPlayer;
    public static ArrayList<File> getSongList=BackgroundService.getSongList;
    Bundle bundle = new Bundle();
    int pos;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (BackgroundService.NOTIFY_PLAY.equals(intent.getAction())) {
            resumeMusic();
        } else if (BackgroundService.NOTIFY_PAUSE.equals(intent.getAction())) {
            if (mediaPlayer.isPlaying()) {
                pauseMusic();
            } else {
                resumeMusic();
            }
        } else if (BackgroundService.NOTIFY_CANCEL.equals(intent.getAction())) {
            context.stopService(new Intent(context, BackgroundService.class));

        } else if (BackgroundService.NOTIFY_NEXT.equals(intent.getAction())) {
            pos=BackgroundService.getCurrrentPosition()+1;
            if(pos>=getSongList.size())
            {
                pos=0;
            }
            nextorPrevious(context);
        } else if (BackgroundService.NOTIFY_PREVIOUS.equals(intent.getAction())) {
            pos=BackgroundService.getCurrrentPosition()-1;
            if (pos<0)
            {
                pos=getSongList.size()-1;
            }
            nextorPrevious(context);
        }
    }



    public void pauseMusic()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic()
    {
        if(mediaPlayer.isPlaying()==false)
        {
            mediaPlayer.start();
        }
    }

    public void nextorPrevious(Context context)
    {
        Uri u = Uri.parse(getSongList.get(pos).toString());
        String name = getSongList.get(pos).getName();
        HomePage.setName.setText(name);
        ArrayList<String> songsName= SongFragment.songsName;
        int songNumber = pos;
        MediaMetadataRetriever mediaMetadataObj=new MediaMetadataRetriever();
        mediaMetadataObj.setDataSource(u.toString());
        byte[] image = mediaMetadataObj.getEmbeddedPicture();
        if(image==null)
        {
            HomePage.img.setImageResource(R.drawable.defaultmusicalbumart);
        }
        else{
            HomePage.img.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        bundle.putInt("songNumber", songNumber);
        bundle.putString("CurrentSongName", name);
        bundle.putByteArray("Albumart", image);
        bundle.putStringArrayList("SongNames",songsName);
        bundle.putString("songUri", u.toString());

        Intent nextintent=new Intent(context,BackgroundService.class);
        nextintent.putExtras(bundle);
        context.startService(nextintent);

    }
}
