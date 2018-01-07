package mb.com.mp3player.activities;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mb.com.mp3player.fragment.SongFragment;
import mb.com.mp3player.R;

/**
 * Created by Anshul on 07-12-17.
 */

public class ShowSongDetailsonFragment extends DialogFragment implements View.OnClickListener {
    String artist, location, album, composer;
    TextView songdetails;
    Button backButton;
    Uri selectedSonguri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vi=inflater.inflate(R.layout.showsongdetail,container,false);
        songdetails=(TextView)vi.findViewById(R.id.songdetails);
        backButton=(Button)vi.findViewById(R.id.backbutton);
        getSongDetails();
        backButton.setOnClickListener(this);
        return vi;
    }

    public void getSongDetails()
    {
        selectedSonguri = SongFragment.uriForSongDetails;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getActivity(), selectedSonguri);
        artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        location = selectedSonguri.toString();
        album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        composer = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        getDialog().setTitle("Details");
        songdetails.setText("SongName:" + System.getProperty("line.separator")+artist+
                System.getProperty("line.separator")+System.getProperty("line.separator")
                +"Album:" + System.getProperty("line.separator") + album+
                System.getProperty("line.separator")+System.getProperty("line.separator")+System.getProperty("line.separator")
                +"Composer:" + System.getProperty("line.separator") + composer+
                System.getProperty("line.separator")+ System.getProperty("line.separator")+System.getProperty("line.separator")
                +"Location:" + System.getProperty("line.separator") + location);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.backbutton:
                getDialog().dismiss();
                break;
        }

    }
}
