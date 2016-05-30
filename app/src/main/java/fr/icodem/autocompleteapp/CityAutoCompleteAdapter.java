package fr.icodem.autocompleteapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CityAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<City> resultList = new ArrayList<>();

    public CityAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public City getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        City city = getItem(position);
        String text = "[" + city.getId() + "] " + city.getName();

        ((TextView) convertView.findViewById(android.R.id.text1)).setText(text);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<City> cities = findCities(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = cities;
                    if (cities != null) {
                        filterResults.count = cities.size();
                    } else {
                        filterResults.count = 0;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<City>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<City>  findCities(String query) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://SEM12.sesame.infotel.com:8080/carpooling-rs/trips/cities/search/" + query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");

            List<City> cities = parse(readStream(connection));

            return cities;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return null;
    }

    private List<City> parse(StringBuilder json) {
        List<City> cities = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                cities.add(new City(id, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cities;
    }

    private StringBuilder readStream(HttpURLConnection connection) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        return sb;
    }

}
