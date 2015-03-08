package com.example.lemon.onlineshop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import json.listview.library.JSONParser;

/**
 * Created by Andrey on 3/8/2015.
 * Class for Cart Activity
 */
public class CartActivity extends Activity {
    ListView list;
    TextView id;
    TextView name;
    TextView city;
    Button Btngetdata;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<>();
    //URL to get JSON Array
    private static String url = "http://csufshop.ozolin.ru/test_json.php";
    //JSON Node Names
    private static final String TAG_ROWS = "rows";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CITY = "city";
    JSONArray android = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        oslist = new ArrayList<>();
        Btngetdata = (Button)findViewById(R.id.getdata);
        Btngetdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONParse().execute();
            }
        });
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            id = (TextView)findViewById(R.id.id);
            name = (TextView)findViewById(R.id.name);
            city = (TextView)findViewById(R.id.city);
            pDialog = new ProgressDialog(CartActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            //JSONObject json = jParser.getJSONFromUrl(url);
            return jParser.getJSONFromUrl(url);
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_ROWS);
                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String ver = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String api = c.getString(TAG_CITY);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_ID, ver);
                    map.put(TAG_NAME, name);
                    map.put(TAG_CITY, api);
                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);
                    ListAdapter adapter = new SimpleAdapter(CartActivity.this, oslist,
                            R.layout.list_cart,
                            new String[] { TAG_ID,TAG_NAME, TAG_CITY }, new int[] {
                            R.id.id,R.id.name, R.id.city});
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(CartActivity.this, "You Clicked at " + oslist.get(+position).get("name"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
