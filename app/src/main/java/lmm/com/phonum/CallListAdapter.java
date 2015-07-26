package lmm.com.phonum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lmm.com.phonum.utils.CallListUtils;

/**
 * Created by HP on 25.07.2015.
 */
public class CallListAdapter extends ArrayAdapter<CallListUtils.Number.Call> {

    private Context context;

    private ImageView call_ico;
    private TextView date;
    private TextView duration;

    public CallListAdapter(Context context){
        super(context, R.layout.call_list_item);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallListUtils.Number.Call  item = getItem(position);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.call_list_item, parent, false);
        }
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        call_ico = (ImageView)view.findViewById(R.id.call_ico);
        date = (TextView)view.findViewById(R.id.date);
        duration = (TextView)view.findViewById(R.id.duration);

        call_ico.setImageDrawable(context.getDrawable(CallListUtils.getDrawableIdFromType(item.type)));
        DateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy HH:mm");
        date.setText(dateFormat.format(new Date(item.date)));
        DateFormat durationFormat = new SimpleDateFormat("m 'мин.' ss 'сек.'");
        duration.setText(durationFormat.format(new Date((long)item.duration * 1000)));

        return view;
    }
}
