package mb.com.mp3player.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mb.com.mp3player.fragment.PlayListFragment;
import mb.com.mp3player.fragment.SongFragment;

/**
 * Created by Anshul on 27-12-17.
 */

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        android.support.v4.app.Fragment  fragment=null;
        if(position==0)
        {
            fragment=new SongFragment();
        }
        if(position==1)
        {
            fragment=new PlayListFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
