package com.example.lemon.onlineshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lemon.onlineshop.Library.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mysql.access.library.JSONParser;

/**
 * Activity for Search layout
 */
public class SearchActivity extends Activity {
    ListView list;
    TextView name;
    TextView city;
    TextView phone;

    ArrayList<HashMap<String, String>> songList = new ArrayList<>();
    SessionManager session;
    //JSON Node Names
    private static final String TAG_ROWS = "rows";
    private static final String TAG_NAME = "name";
    private static final String TAG_CITY = "city";
    private static final String TAG_PHONE = "phone";
    JSONArray android = null;

    Button btnSearch;
    Button btnBackHome;
    private static String urlSearch = "http://csufshop.ozolin.ru/test_search.php?name=";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Declare alert dialog
        // TODO rework alert as http://stackoverflow.com/questions/13268302/alternative-setbutton
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alert, int which) {
                alert.dismiss();
            }
        });
        // Create session class instance
        session = new SessionManager(getApplicationContext());
        // Find textview
        final EditText searchView = (EditText) findViewById(R.id.twSearch);
        // Find all buttons
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnBackHome = (Button) findViewById(R.id.btnGoHome);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchField = searchView.getText().toString();
                new Search().execute(searchField);
            }
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class Search extends AsyncTask <String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = (TextView)findViewById(R.id.name);
            city = (TextView)findViewById(R.id.city);
            phone = (TextView) findViewById(R.id.phone);
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Searching ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            String searchAttribute = args[0];
            return jParser.getJSONFromUrl(urlSearch + searchAttribute);
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
                    String ver = c.getString(TAG_NAME);
                    String name = c.getString(TAG_CITY);
                    String api = c.getString(TAG_PHONE);
                    // Adding value HashMap key => value
                    HashMap <String, String> map = new HashMap<>();
                    map.put(TAG_NAME, ver);
                    map.put(TAG_CITY, name);
                    map.put(TAG_PHONE, api);
                    songList.add(map);
                    list=(ListView)findViewById(R.id.homeListView);
                    ListAdapter adapter = new SimpleAdapter(SearchActivity.this, songList,
                            R.layout.list_search_results,
                            new String[] { TAG_NAME,TAG_CITY, TAG_PHONE }, new int[] {
                            R.id.name,R.id.city, R.id.phone});
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(SearchActivity.this, "You added " + songList.get(+position).get("name") + " to cart.", Toast.LENGTH_SHORT).show();
                            // TODO insert here post data to db via url
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
