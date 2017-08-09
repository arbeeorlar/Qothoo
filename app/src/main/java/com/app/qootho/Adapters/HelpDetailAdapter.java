package com.app.qootho.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.qootho.Model.HelpModel;
import com.app.qootho.R;

import java.util.ArrayList;
import java.util.List;

public class HelpDetailAdapter extends BaseAdapter {

    //public ArrayList<HelpModel> itemList;

    public Activity context;
    public LayoutInflater inflater;
    List<HelpModel.HelpSubTitle> itemList;

    public HelpDetailAdapter(Activity context, ArrayList<HelpModel.HelpSubTitle> voiceItemList) {
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

        View v = convertView;
        HelpModel.HelpSubTitle file_item = itemList.get(position);

        if (file_item.isSectionHeader()) {

            v = inflater.inflate(R.layout.listview_section_header, null);
            v.setClickable(false);
            TextView header = (TextView) v.findViewById(R.id.section_header);
            header.setText(file_item.getSubTitle());

        } else {

            v = inflater.inflate(R.layout.layout, null);
            TextView txtFeedName = (TextView) v.findViewById(R.id.title);
            txtFeedName.setText(file_item.getSubTitleContent());


        }

        return v;
    }

    public static class ViewHolder {
        TextView txtFeedName;
        ImageView direction;


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

 final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout, null);
            //holder.txtViewAccountType = (TextView) convertView.findViewById(R.id.name);
            holder.txtFeedName = (TextView) convertView.findViewById(R.id.title);
            holder.direction = (ImageView) convertView.findViewById(R.id.direction);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        HelpModel.HelpSubTitle file_item = itemList.get(position);
        holder.txtFeedName.setText(file_item.getSubTitle());
       /// it = itemList.get(position).getHelpSubTitle();

    convertView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();
        }
    });



        return convertView;




     */

}