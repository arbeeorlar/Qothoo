package com.app.qootho.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.qootho.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by macbookpro on 29/07/2017.
 */


public class GooglePlaceAdapter extends BaseAdapter implements Filterable {
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "your_api_key";
    public LayoutInflater inflater;
    ArrayList<String> descriptionList;
    private ArrayList<String> resultList;
    private Context context = null;

    public GooglePlaceAdapter(Context context, int textViewResourceId, ArrayList<String> descriptionList) {
        //super(context, textViewResourceId);
        super();
        this.context = context;
        this.descriptionList = descriptionList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if (resultList != null)
            return resultList.size();
        else
            return 0;
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        ArrayList<String> descriptionList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + R.string.google_maps_key);
            sb.append("&components=country:en");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            System.out.println("String SB :: " + sb);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("TAG", "", e);
            e.printStackTrace();
            return resultList;
        } catch (IOException e) {
            Log.e("TAG", "Error connecting to Places API", e);
            e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            Log.d("jsonResults", jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            descriptionList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).toString());
                descriptionList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }

            //saveArray(resultList.toArray(new String[resultList.size()]), "predictionsArray", getContext());

        } catch (JSONException e) {
            Log.e("TAG", "- E  -", e);
            e.printStackTrace();
        }

        return descriptionList;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = descriptionList; //autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    //  setImageVisibility();
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final GooglePlaceAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new GooglePlaceAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.layout_account_type, null);
            //holder.txtViewAccountType = (TextView) convertView.findViewById(R.id.name);
            holder.txtFeedName = (TextView) convertView.findViewById(R.id.txtFeedName);


            convertView.setTag(holder);
        } else
            holder = (GooglePlaceAdapter.ViewHolder) convertView.getTag();

        String file_item = descriptionList.get(position);
        holder.txtFeedName.setText(file_item);
        System.out.println("position::: " + position);

        return convertView;
    }

    public static class ViewHolder {
        TextView txtFeedName;
        ImageView direction;


    }
}