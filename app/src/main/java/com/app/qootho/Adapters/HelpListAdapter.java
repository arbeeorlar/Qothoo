package com.app.qootho.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.qootho.HelpDetailActivity;
import com.app.qootho.Model.HelpModel;
import com.app.qootho.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HelpListAdapter extends BaseAdapter {

    public ArrayList<HelpModel> itemList;

    public Activity context;
    public LayoutInflater inflater;
    List<HelpModel.HelpSubTitle> subTitles;

    public HelpListAdapter(Activity context, ArrayList<HelpModel> voiceItemList) {
        super();

        this.context = context;
        this.itemList = voiceItemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.help_layout, null);
            //holder.txtViewAccountType = (TextView) convertView.findViewById(R.id.name);
            holder.txtFeedName = (TextView) convertView.findViewById(R.id.title);
            holder.direction = (ImageView) convertView.findViewById(R.id.direction);
            // holder.img  =  (ImageView) convertView.findViewById(R.id.list_image) ;
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        HelpModel file_item = itemList.get(position);
        holder.txtFeedName.setText(file_item.getTitle());
        subTitles = itemList.get(position).getHelpSubTitle();
        // holder.img.setVisibility(View.GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HelpDetailActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("help", (Serializable) subTitles);
                //args.putParcelableArrayList("Birds",  subTitles);
                //intent.putExtras(args);
                intent.putExtra("BUNDLE", args);
                context.startActivity(intent);
            }
        });

//        holder.direction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });


        return convertView;
    }

    public static class ViewHolder {
        TextView txtFeedName;
        ImageView direction;
        ImageView img;


    }

    /*
    ListView listView = (ListView) findViewById(R.id.list);
View header = getLayoutInflater().inflate(R.layout.header, null);
View footer = getLayoutInflater().inflate(R.layout.footer, null);
listView.addHeaderView(header);
listView.addFooterView(footer);
listView.setAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_single_choice,
        android.R.id.text1, names));
    }
}

     */

}