package mb.com.mp3player.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;

/**
 * Created by Anshul on 30-12-17.
 */

public class SongData {
    @SerializedName("songname")
    @Expose
    private String songname;
    @SerializedName("songalbum")
    @Expose
    private String songalbum;

    public SongData(String songname, String songalbum) throws JSONException {
        this.songname=songname;
        this.songalbum=songalbum;
    }
    public SongData(String songname) throws JSONException {
        this.songname=songname;
    }
    public String getsongName() {
        return songname;
    }

    public void setsongName(String songname) {
        this.songname = songname;
    }

    public String getsongArt() {

        return songalbum;
    }

    public void setsongArt(String songalbum) {
        this.songalbum = songalbum;
    }

}
