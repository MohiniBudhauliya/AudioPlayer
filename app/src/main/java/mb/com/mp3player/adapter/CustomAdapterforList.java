package mb.com.mp3player.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mb.com.mp3player.R;

/**
 * Created by Anshul on 11-12-17.
 */

public class CustomAdapterforList extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> song;
    private final ArrayList<String> imageId;
    public CustomAdapterforList(Activity context, ArrayList<String> imageId, ArrayList<String> song) {
        super(context, R.layout.music_list_xml,song);
        this.context = context;
        this.imageId = imageId;
        this.song=song;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.music_list_xml, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.songImage);

        if(imageId.get(position)==" ")
        {
            imageView.setImageResource(R.drawable.defaultmusicalbumart);

        }
        else
        {
            try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            byte[] decodedString = Base64.decode(imageId.get(position),Base64.CRLF);
                Bitmap bitmap =BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 70, 60, false));
                //bitmap.setPixel(60,60, Color.BLACK);
                //imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        TextView textView=(TextView)rowView.findViewById(R.id.songname);
        textView.setText(song.get(position));
        return rowView;
    }
    public void refreshEvents(ArrayList<String> imageId,ArrayList<String> song) {

        this.imageId.clear();
        this.imageId.addAll(imageId);
        this.song.clear();
        this.song.addAll(song);
        notifyDataSetChanged();
    }


}
