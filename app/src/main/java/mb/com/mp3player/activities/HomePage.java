package mb.com.mp3player.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import mb.com.mp3player.adapter.TabAdapter;
import mb.com.mp3player.fragment.SongFragment;
import mb.com.mp3player.R;
import mb.com.mp3player.Service.BackgroundService;

public class HomePage extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener, View.OnClickListener {
    android.support.v7.app.ActionBar actionBar;
    ViewPager viewPager;
    public static Button play,pause;
    public static TextView setName;
    public static ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The Action Bar is a window feature. The feature must be requested
        // before setting a content view. Normally this is set automatically
        // by your Activity's theme in your manifest. The provided system
        // theme Theme.WithActionBar enables this for you. Use it as you would
        // use Theme.NoTitleBar. You can add an Action Bar to your own themes
        // by adding the element <item name="android:windowActionBar">true</item>
        // to your style definition.
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_home_page);
        play = (Button) findViewById(R.id.playbutton);
        pause=(Button)findViewById(R.id.pausebutton);
        setName=(TextView)findViewById(R.id.name);
        setName.setSelected(true);
        img=(ImageView)findViewById(R.id.img);
        play.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFF,Color.parseColor("#FFFFFF")));
        pause.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFF,Color.parseColor("#FFFFFF")));
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        //view pager is responsible for changing the tab based on user swapping.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             //
            }

            @Override
            public void onPageSelected(int position) {
                //linking the tab along with the page
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == viewPager.SCROLL_STATE_IDLE) {
                    //Toast.makeText(getApplicationContext(),"Idle",Toast.LENGTH_SHORT).show();
                }
                if (state == viewPager.SCROLL_STATE_DRAGGING) {
                    //Toast.makeText(getApplicationContext(),"Dragging",Toast.LENGTH_SHORT).show();
                }
                if (state == viewPager.SCROLL_STATE_SETTLING) {
                    //Toast.makeText(getApplicationContext(),"Settling",Toast.LENGTH_SHORT).show();
                }

            }
        });
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        android.support.v7.app.ActionBar.Tab songTab = actionBar.newTab();
        songTab.setText("Songs");
        //songTab.setIcon(R.drawable.bgwithheadphone);
        songTab.setTabListener(this);


        android.support.v7.app.ActionBar.Tab playlistTab = actionBar.newTab();
        playlistTab.setText("Playlist");
        playlistTab.setTabListener(this);

        actionBar.addTab(songTab);
        actionBar.addTab(playlistTab);

    }



    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        //linking page with the tab

    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

   }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds songsName to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_open_music_drawer, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.logout));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog));
            // set title
            alertDialogBuilder.setTitle("Logout");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            Intent gotologin = new Intent(HomePage.this, LoginPage.class);
                            startActivity(gotologin);
                        }
                    });
            alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }

        return true;//super.onOptionsItemSelected(item);
    }

    //Functionality of Play/Pause/Stop Button
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    switch (id) {
                        case R.id.playbutton:
                            if (BackgroundService.mediaPlayer == null) {
                                Uri uri = Uri.parse(SongFragment.songList.get(0).toString());
                                BackgroundService.mediaPlayer = MediaPlayer.create(this, uri);
                                BackgroundService.mediaPlayer.start();
                                play.setVisibility(View.GONE);
                                pause.setVisibility(View.VISIBLE);
                            } else {
                                play.setVisibility(View.GONE);
                                pause.setVisibility(View.VISIBLE);
                                BackgroundService.resumeSong();
                            }
                            break;
                        case R.id.pausebutton:
                            play.setVisibility(View.VISIBLE);
                            pause.setVisibility(View.GONE);
                            BackgroundService.pauseSong();
                            break;
                    }

                }
            }