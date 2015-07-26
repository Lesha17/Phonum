package lmm.com.phonum;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import lmm.com.phonum.utils.CallListUtils;

/**
 * Created by HP on 22.07.2015.
 */
public class NumberListAdapter extends ArrayAdapter<CallListUtils.Number> {
    private final Context context;
    private static final double MAX_RATIO = 1.5;

    public NumberListAdapter(Context context){
        super(context, R.layout.number_list_item);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallListUtils.Number item = getItem(position);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        if(view == null) {
            view = inflater.inflate(R.layout.number_list_item, parent, false);
        }

        //TODO: Set OnClick Listeners

        ImageButton btn1 = (ImageButton)view.findViewById(R.id.button1);
        btn1.setImageDrawable(context.getDrawable(R.drawable.phone));
        //btn1.setOnClickListener(item.btn1click);

        ImageButton btn2 = (ImageButton)view.findViewById(R.id.button2);
        btn2.setImageDrawable(context.getDrawable(R.drawable.tack));
        //btn2.setOnClickListener(item.btn2click);

        ImageButton btn3 = (ImageButton)view.findViewById(R.id.button3);
        btn3.setImageDrawable(context.getDrawable(R.drawable.edit));
        //btn3.setOnClickListener(item.btn3click);

        final TextView label1 = (TextView)view.findViewById(R.id.label1);
        if(item.hasName) {
            label1.setText(item.name);
        } else {
            label1.setText(item.formatted_number);
        }
        label1.setSelected(true);

        TextView label2 = (TextView)view.findViewById(R.id.label2);
        label2.setText(item.category);
        if(item.category == null){
            label2.setText("Без категории");
        }

        int calls_size = item.calls.size();

        ImageView ico1 = (ImageView)view.findViewById(R.id.ico1);
        ico1.setImageDrawable(context.getDrawable(CallListUtils.getDrawableIdFromType(item.calls.get(0).type)));

        if(calls_size > 1){
            try{
                Drawable i2 = context.getDrawable(CallListUtils.getDrawableIdFromType(item.calls.get(1).type));
                ImageView ico2 = (ImageView)view.findViewById(R.id.ico2);
                ico2.setImageDrawable(i2);
                ico2.setVisibility(View.VISIBLE);
                if(calls_size > 2){
                    Drawable i3 = context.getDrawable(CallListUtils.getDrawableIdFromType(item.calls.get(2).type));
                    ImageView ico3 = (ImageView)view.findViewById(R.id.ico3);
                    ico3.setImageDrawable(i3);
                    ico3.setVisibility(View.VISIBLE);
                }
            } catch (Exception e){

            }
        }
        TextView label3 = (TextView) view.findViewById(R.id.label3);
        Date date = new Date(item.last);

        SimpleDateFormat format = new SimpleDateFormat(" E d MMMM yyyy HH:mm");
        label3.setText(format.format(date));

        return view;
    }

    public NumberListAdapter search(String str){
        NumberListAdapter adapter = new NumberListAdapter(context);

        String str2 = null;
        if(str.startsWith("8")){
            str2 = "+7" + str.substring(1);
        } else if (str.startsWith("+7")){
            str2 = "8" + str.substring(2);
        }

        for (int i = 0; i < getCount(); ++i){
            CallListUtils.Number item = getItem(i);
            if((item.number != null && (item.number.contains(str) || (str2 != null && item.number.startsWith(str2))))
                    || (item.name != null  && any_starts_with(item.name, str))
                    || (item.category != null && any_starts_with(item.category, str))
                    || (item.hint != null && any_starts_with(item.hint, str))){
                adapter.add(item);
            }
        }


        return adapter;
    }

    private static boolean any_starts_with(String in, String str){
        for(String s : in.toUpperCase().split(" ")){
            if(s.startsWith(str.toUpperCase())){
                return true;
            }
        }
        return false;
    }
}
