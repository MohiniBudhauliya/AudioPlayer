package mb.com.mp3player.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import mb.com.mp3player.R;
import mb.com.mp3player.Service.BackgroundService;
import mb.com.mp3player.fragment.PlayListFragment;
import mb.com.mp3player.fragment.SongFragment;

/**
 * Created by Anshul on 12-12-17.
 */



public class HandleNotificationIntent extends BroadcastReceiver {

    MediaPlayer mediaPlayer= BackgroundService.mediaPlayer;
    //public static ArrayList<File> getSongList=BackgroundService.getSongList;
    public ArrayList<String>getSongList=BackgroundService.songNamelist;;
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
            HomePage.pause.setVisibility(View.GONE);
            HomePage.play.setVisibility(View.VISIBLE);
            mediaPlayer.pause();
        }
    }

    public void resumeMusic()
    {
        if(mediaPlayer.isPlaying()==false)
        {
            HomePage.pause.setVisibility(View.VISIBLE);
            HomePage.play.setVisibility(View.GONE);
            mediaPlayer.start();
        }
    }

    public void nextorPrevious(Context context) {
        if (SongFragment.songsName.contains(getSongList.get(pos))) {
            int index= SongFragment.songsName.indexOf(getSongList.get(pos));
            String path= SongFragment.songList.get(index).getPath();
            Uri u= Uri.parse(path);
            final ArrayList<String> songsName=new ArrayList<>();
            for(int i=0;i<getSongList.size();i++)
            {
                songsName.add(i,getSongList.get(i));
            }
            String name = getSongList.get(pos);
            HomePage.setName.setText(name);
            int songNumber = pos;
            MediaMetadataRetriever mediaMetadataObj = new MediaMetadataRetriever();
            mediaMetadataObj.setDataSource(u.toString());
            byte[] image = mediaMetadataObj.getEmbeddedPicture();
            if (image == null) {
                HomePage.img.setImageResource(R.drawable.defaultmusicalbumart);
            } else {
                HomePage.img.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            bundle.putInt("songNumber", songNumber);
            bundle.putString("CurrentSongName", name);
            bundle.putByteArray("Albumart", image);
            bundle.putStringArrayList("SongNames", songsName);
            bundle.putString("songUri", u.toString());

            Intent nextintent = new Intent(context, BackgroundService.class);
            nextintent.putExtras(bundle);
            context.startService(nextintent);

        }
        else
        {
            Toast.makeText(context,"Song is not available on your device",Toast.LENGTH_SHORT).show();

        }
    }

}
