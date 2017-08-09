package com.app.qootho.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.qootho.Model.PassengerAccount;
import com.app.qootho.OnFragmentInteractionListener;
import com.app.qootho.R;

import java.util.ArrayList;

public class UserAccountListViewCustomAdapter extends BaseAdapter {

    public ArrayList<PassengerAccount> itemList;

    public Activity context;
    public LayoutInflater inflater;
    LinearLayout mLinear = null;
    LinearLayout _linear = null;

    OnFragmentInteractionListener listner;

    public UserAccountListViewCustomAdapter(Activity context, ArrayList<PassengerAccount> voiceItemList, OnFragmentInteractionListener listner) {
        super();

        this.context = context;
        this.itemList = voiceItemList;
        this.listner = listner;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public UserAccountListViewCustomAdapter(Activity context, ArrayList<PassengerAccount> voiceItemList, LinearLayout mLinear) {
        super();

        this.context = context;
        this.itemList = voiceItemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLinear = mLinear;
    }

    public UserAccountListViewCustomAdapter(Activity context, ArrayList<PassengerAccount> voiceItemList, LinearLayout mLinear, LinearLayout _linear) {
        super();

        this.context = context;
        this.itemList = voiceItemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLinear = mLinear;
        this._linear = _linear;
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
            convertView = inflater.inflate(R.layout.layout, null);
            //holder.txtViewAccountType = (TextView) convertView.findViewById(R.id.name);
            holder.txtFeedName = (TextView) convertView.findViewById(R.id.title);
            // holder.radioButton = (RadioButton) convertView.findViewById(R.id.txtRadioButton);
            //holder.linear_parent = (LinearLayout) convertView.findViewById(R.id.linear_parent);
            holder.img = (ImageView) convertView.findViewById(R.id.list_image);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final PassengerAccount file_item = itemList.get(position);
        holder.txtFeedName.setText(file_item.getInstitutionName());


        if (position == 0) {
            holder.img.setImageResource(R.drawable.ic_work);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(context, holder.txtFeedName.getText().toString(), Toast.LENGTH_SHORT).show();

                listner.onClick(file_item);

//                if(mLinear != null && _linear != null) {
//                    mLinear.setVisibility(View.VISIBLE);
//                    _linear.setVisibility(View.GONE);
//                }

            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView txtFeedName;
        RadioButton radioButton;
        LinearLayout linear_parent;
        ImageView img;


    }

}