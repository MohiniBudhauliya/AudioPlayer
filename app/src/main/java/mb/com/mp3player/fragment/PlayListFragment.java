package mb.com.mp3player.fragment;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mb.com.mp3player.activities.HomePage;
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


public class PlayListFragment extends android.support.v4.app.Fragment {
    public static ListView playlist;
    int i = 0;
    static ArrayList<String> songs = new ArrayList<>();
    static ArrayList<String> songArt = new ArrayList<>();
    static CustomAdapterforList adapter;
    Retrofit retrofit;
    ApiInterface apiInterface;
    String name;
    AdapterView.AdapterContextMenuInfo header;
    MediaMetadataRetriever mediaMetadataObj = new MediaMetadataRetriever();
    int number=0;


    public PlayListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vi = inflater.inflate(R.layout.fragment_play_list, container, false);
        playlist = (ListView) vi.findViewById(R.id.playlist);
        return vi;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        try {
            final ArrayList<String> songss=new ArrayList<>();
            final ArrayList<String> songArts=new ArrayList<>();
            Call<List<SongData>> call = apiInterface.getplaylist();   //Making Api call using Call Retrofit method that sends a request to a webserver and returns a response.
            call.enqueue(new Callback<List<SongData>>() {  //enqueue  send request asynchronously and notify it response or any error occurs while talking to server.
                //In case of Success and server responds.
                @Override
                public void onResponse(Call<List<SongData>> call, Response<List<SongData>> response) {
                    List<SongData> songdata = response.body(); //storing response body in songinfo.
                    for (SongData song : songdata) {
                        songss.add(i, song.getsongName());
                        songArts.add(i, song.getsongArt());
                        i++;
                    }
                    songs=songss;
                    songArt=songArts;
                    adapter = new CustomAdapterforList(getActivity(),songArts, songss);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.refreshEvents(songss,songArts);
                            }
                        });
                    // Assign adapter to ListView
                    playlist.setAdapter(adapter);
                    registerForContextMenu(playlist);
                    // Checks If item on ListView is Clicked And performs Required Function.
                    playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HomePage.play.setVisibility(View.GONE);
                            HomePage.pause.setVisibility(View.VISIBLE);
                            Bundle playlistbun = new Bundle();
                            if (SongFragment.songsName.contains(songs.get(position).toString())) {
                                int index=SongFragment.songsName.indexOf(songs.get(position));
                                String path=SongFragment.songList.get(index).getPath();
                                Uri uri = Uri.parse(path);
                                playlistbun.putString("songUri", uri.toString());
                                name = songs.get(position);
                                HomePage.setName.setText(name);
                                int songNumber = position;
                                mediaMetadataObj.setDataSource(uri.toString());
                                byte[] image = mediaMetadataObj.getEmbeddedPicture();
                                playlistbun.putInt("songNumber", songNumber);
                                playlistbun.putString("CurrentSongName", name);
                                playlistbun.putStringArrayList("AllSongName", songs);
                                playlistbun.putByteArray("Albumart", image);
                                //Sending control to background service to play music
                                Intent gotoService = new Intent(getActivity(), BackgroundService.class);
                                gotoService.putExtra("songList", songs);
                                gotoService.putExtras(playlistbun);
                                getActivity().startService((gotoService));
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Song is not available on your device",Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                }

                @Override
                public void onFailure(Call<List<SongData>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("Something went wrong", t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        header = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(songs.get(header.position));
        menu.add(0, v.getId(), 0, "Remove from Playlist");
        menu.add(0, v.getId(), 0, "Cancel");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Remove from Playlist") {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiInterface.BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            try {
                final SongData songData = new SongData(songs.get(header.position));
                ApiInterface apiInterface = retrofit.create(ApiInterface.class);
                apiInterface.removefromplaylist(songData).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.isSuccessful()) {
                            songs.remove(songs.get(header.position));
                            final ArrayList<String> songss=songs;
                            songArt.remove(songArt.get(header.position));
                            final ArrayList<String> songArts=songArt;

                            adapter = new CustomAdapterforList(getActivity(),songArts, songss);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.refreshEvents(songss,songArts);
                                }
                            });
                            // Assign adapter to ListView
                            playlist.setAdapter(adapter);
                            registerForContextMenu(playlist);
                            Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == 404) {
                            Toast.makeText(getActivity(), "Not Removed", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == 500) {
                            Toast.makeText(getActivity(), "Already Removed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getActivity(), "Getting issue while Removing data from the server ", Toast.LENGTH_SHORT);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (item.getTitle() == "Cancel")
        {

        }
        return  true;
    }

}
//    private void notifyAdapter()  {
//        activity.runOnUiThread(new Runnable()  {
//            public void run() {
//                listView.setAdapter(null);
//                if(adapter != null) {
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
//    }