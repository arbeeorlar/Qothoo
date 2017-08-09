package com.app.qootho.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Model.InterStateTrip;
import com.app.qootho.R;

import java.util.List;


public class PreviousInterStateTripAdapter extends RecyclerView.Adapter<PreviousInterStateTripAdapter.ViewHolder> {

    private final List<InterStateTrip> mValues;
    //private final OnListFragmentInteractionListener mListener;
    private Activity context;

    public PreviousInterStateTripAdapter(List<InterStateTrip> items, Activity context) {
        mValues = items;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previous_inter_trip, parent, false);


        //mMapView.onCreate(savedInstanceState);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //holder.mItem = mValues.get(position);
        //  holder.mIdView.setText(mValues.get(position).id);
        // holder.mContentView.setText(mValues.get(position).content);
        Log.i("position :: ", position + "");
        holder.txtTripAmount.setText("N" + mValues.get(position).getTotalFare() + "");
        holder.txtTripCarModel.setText(mValues.get(position).getTripDate());
        holder.txtDropoff.setText(mValues.get(position).getSourceState() + " - " + mValues.get(position).getDestinationSate());
        if (mValues.get(position).getIsTripConcluded()) {

            holder.txtStatus.setText("Concluded");
        } else {

            holder.txtStatus.setText("Not Concluded");
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, position, Toast.LENGTH_SHORT).show();

//                Intent intent  =  new Intent(context, PreviousTripDetail.class);
//                Bundle args = new Bundle();
//                args.putSerializable("help",(Serializable)mValues);
//                args.putInt("position",position);
//                intent.putExtra("BUNDLE",args);
//
//                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder

    {
        View mView;
        TextView txtDropoff;
        TextView txtTripAmount;
        TextView txtTripCarModel;
        TextView txtStatus;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            //  mIdView = (TextView) view.findViewById(R.id.id);
            // mContentView = (TextView) view.findViewById(R.id.content);
            txtDropoff = (TextView) view.findViewById(R.id.txtDropOff);
            txtTripAmount = (TextView) view.findViewById(R.id.txtTripAmount);
            txtTripCarModel = (TextView) view.findViewById(R.id.txtTripCarModel);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);


        }


    }


}
