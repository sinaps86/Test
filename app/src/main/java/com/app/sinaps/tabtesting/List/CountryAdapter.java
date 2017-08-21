package com.app.sinaps.tabtesting.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sinaps.tabtesting.R;

import java.util.ArrayList;

/**
 * Created by Sinaps on 15.08.2017.
 */

/** Расширение для ArrayAdapter для списка.  Bспльзует холдера для ускорения
 *
 */
public class CountryAdapter extends ArrayAdapter<Country>{

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Country> countryList;
    private final int IMAGE_POSITIVE = R.drawable.trueimg;
    private final int IMAGE_NEGATIVE = R.drawable.falseimg;

    public CountryAdapter(Context context, int resource, ArrayList<Country> countries){
        super(context, resource, countries);

        this.countryList = countries;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder =  new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Country country = countryList.get(position);

        viewHolder.name.setText(country.getName());
        viewHolder.checker.setChecked(country.isVisited());
        if(country.isVisited())
            viewHolder.image.setImageResource(IMAGE_POSITIVE);
        else
            viewHolder.image.setImageResource(IMAGE_NEGATIVE);

        viewHolder.checker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBox = (CheckBox)v;
                boolean isVisited = checkBox.isChecked();
                if(isVisited) {
                    viewHolder.image.setImageResource(IMAGE_POSITIVE);
                    country.setVisited(isVisited);
                }else {
                    viewHolder.image.setImageResource(IMAGE_NEGATIVE);
                    country.setVisited(isVisited);
                }
            }
        });

        return convertView;
    }
    /** Класс "Холдер" для адаптера
     *
     */
    private class ViewHolder{
        final ImageView image;
        final TextView name;
        final CheckBox checker;

        ViewHolder(View view){
            image = (ImageView) view.findViewById(R.id.list_item_iv);
            name = (TextView) view.findViewById(R.id.list_item_tv);
            checker = (CheckBox) view.findViewById(R.id.list_item_chb);
        }
    }
}
