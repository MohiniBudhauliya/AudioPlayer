package mb.com.mp3player.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import mb.com.mp3player.activities.HomePage;
import mb.com.mp3player.activities.ShowSongDetailsonFragment;
import mb.com.mp3player.adapter.CustomAdapterforList;
import mb.com.mp3player.interfaces.ApiInterface;
import mb.com.mp3player.R;
import mb.com.mp3player.Service.BackgroundService;
import mb.com.mp3player.models.SongData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SongFragment extends android.support.v4.app.Fragment {
    ListView songContainer;
    String name;
    public Uri uri;
    int songNumber, i, j;
    static int number=0;
    ArrayList<String>playlistsongs=new ArrayList<>();
    ArrayList<String> playlistSongArt=new ArrayList<>();
    public static Uri uriForSongDetails;
    byte[] image;
    static Bundle bun = new Bundle();
    public static ArrayList<File> songList = new ArrayList<>();
    ShowSongDetailsonFragment showSongDetails = new ShowSongDetailsonFragment();
    AdapterView.AdapterContextMenuInfo header;
    MediaMetadataRetriever mediaMetadataObj = new MediaMetadataRetriever();
    public static ArrayList<String>songsName=new ArrayList<>();
    ArrayList<String> allImages=new ArrayList<>();
    public FragmentManager fm;


    public SongFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vi=inflater.inflate(R.layout.fragment_song,null);
        songContainer=(ListView)vi.findViewById(R.id.musicList);
        getNameOfSongs();
        setAlbumArt();
        playSongs();
        return vi;
    }
    //Function for getting all songs from device to listView
    public ArrayList<File> findsongs(String root) {
        File rootFolder = new File(root);
        File[] songArray = rootFolder.listFiles();
        if (songArray != null) {
            for (File file : songArray) {
                if (file.isDirectory()) {
                    findsongs(file.getAbsolutePath());
                } else {
                    if (file.getName().endsWith(".mp3") || file.getName().endsWith(".Mp3")) {
                        songList.add(file);
                    }
                }
            }
        }

        return songList;
    }

    //Function for getting name of all songs
    public void getNameOfSongs() {
        String path = "/storage/";//storage/ext_sd/DCIM/
        songList = findsongs(path);
        for (i = 0; i < songList.size(); i++) {
            songsName.add(i ,songList.get(i).getName());//.replace(".mp3", " "));
        }
    }

    //Function for getting albumart for songs
    public boolean setAlbumArt() {

        for (j = 0; j < songList.size(); j++) {
            try {
                MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
                myRetriever.setDataSource(getActivity(), Uri.parse(songList.get(j).toString()));
                image = myRetriever.getEmbeddedPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (image != null) {
                allImages.add(j,Base64.encodeToString(image, Base64.CRLF));
            } else {
                allImages.add(j," ");
            }
        }
        return true;
    }


    //Function for playing songs
    public void playSongs() {
        CustomAdapterforList adapter = new CustomAdapterforList(getActivity(), allImages, songsName);
        songContainer.setAdapter(adapter);
        //registering menu with the list
        registerForContextMenu(songContainer);
        songContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomePage.setName.setText(songsName.get(position));
                HomePage.play.setVisibility(View.GONE);
                HomePage.pause.setVisibility(View.VISIBLE);
                uri = Uri.parse(songList.get(position).toString());
                bun.putString("songUri", uri.toString());
                name = songList.get(position).getName();
                songNumber = position;
                mediaMetadataObj.setDataSource(uri.toString());
                image = mediaMetadataObj.getEmbeddedPicture();
                if(image==null)
                {
                    HomePage.img.setImageResource(R.drawable.defaultmusicalbumart);
                }
                else{
                    HomePage.img.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
                }
                bun.putInt("songNumber", songNumber);
                bun.putString("CurrentSongName", name);
                bun.putByteArray("Albumart", image);
                bun.putStringArrayList("SongNames",songsName);
                //Sending control to background service to play music
                Intent gotoService = new Intent(getActivity(), BackgroundService.class);
                gotoService.putExtra("songList", songList);
                gotoService.putExtras(bun);
                getActivity().startService((gotoService));
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        header = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(songsName.get(header.position));
        uriForSongDetails = Uri.parse(songList.get(header.position).toString());
        menu.add(0, v.getId(), 0, "Add to playlist");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Song Details");
        menu.add(0, v.getId(), 0, "Cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Add to playlist") {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiInterface.BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            final String songNameforplaylist =songsName.get(header.position);
            final String album = " ";
            try {
                SongData songData = new SongData(songNameforplaylist, album);
                songData.setsongName(songNameforplaylist);
                songData.setsongArt(album);
                ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                apiInterface.addtoplaylist(songData).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.isSuccessful()) {
                            playlistsongs=PlayListFragment.songs;
                            playlistSongArt=PlayListFragment.songArt;
                            if(PlayListFragment.songs.size()==0&&PlayListFragment.songArt.size()==0) {
                                PlayListFragment.songs.add(number,songNameforplaylist);
                                PlayListFragment.songArt.add(number,album);
                                number++;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PlayListFragment.adapter.refreshEvents(PlayListFragment.songs,PlayListFragment.songArt);
                                    }
                                });

                            }else
                            {
                                number=PlayListFragment.songs.size();
                                PlayListFragment.songs.add(number,songNameforplaylist);
                                PlayListFragment.songArt.add(number,album);
                                number++;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PlayListFragment.adapter.refreshEvents(PlayListFragment.songs,PlayListFragment.songArt);
                                    }
                                });
                            }
                            Toast.makeText(getActivity(), "Added to playlist", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == 404) {
                            Toast.makeText(getActivity(), "Not added to the playlist", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == 500) {
                            Toast.makeText(getActivity(), "Already added", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getActivity(), "Getting issue while sending data to the server ", Toast.LENGTH_SHORT);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
           }
           else if (item.getTitle() == "Song Details") {
            fm=getActivity().getSupportFragmentManager();
            fm.beginTransaction();
            showSongDetails.show(fm, "Details");
        } else {
            return false;
        }
        return true;
    }

    }




