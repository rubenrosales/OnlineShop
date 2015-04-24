package com.example.lemon.onlineshop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.lemon.onlineshop.Library.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mysql.access.library.INSERTmySQL;
import mysql.access.library.JSONParser;

/**
 * Created by Andrey on 3/18/2015.
 * Home page activity for user
 */
public class HomeActivity extends Activity {
    SessionManager session;
    String[] artist;
    String[] artwork;
    String[] songName;
    LazyAdapter adapter;
    ListView list;
    TextView name;
    TextView author;
    TextView price;
    ArrayList <HashMap<String, String> > songList = new ArrayList<>();
    //URL to get JSON Array
    private static String url = "http://csufshop.ozolin.ru/selectTop.php";
    //JSON Node Names
    private static final String TAG_ROWS = "rows";
    private static final String TAG_ID = "idsong";
    private static final String TAG_NAME = "name";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PRICE = "price";
    private static final String TAG_ARTWORK = "artwork";
    JSONArray android = null;

    Button btnCart;
    Button btnCheckout;
    Button btnLogout;
    Button btnSearch;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        list=(ListView)findViewById(R.id.homeListView);

        list.setAdapter(adapter);

        // Create session class instance
        session = new SessionManager(getApplicationContext());
        // Find all buttons
        btnCart = (Button) findViewById(R.id.btnCart);
        btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        // Find textview
        TextView lblWelcome = (TextView) findViewById(R.id.tvWelcome);
        // Check if user logged in
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        String email = user.get(SessionManager.KEY_EMAIL);
        lblWelcome.setText("Welcome " + email);

        // load list
        songList = new ArrayList<>();
        new requestSQL().execute();
        // end of loading list

        // Cart button listner
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Logout button listner
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session data and
                // redirect user to MainActivity
                session.logoutUser();
            }
        });
        // Checkout button listner
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Search button listner
        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class requestSQL extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = (TextView)findViewById(R.id.name);
            author = (TextView)findViewById(R.id.author);
            price = (TextView)findViewById(R.id.price);
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            return jParser.getJSONFromUrl(url);
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                android = json.getJSONArray(TAG_ROWS);
                int size= android.length();
                artist = new String[size];
                songName = new String[size];
                artwork = new String[size];

                // Getting JSON Array from URL

                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i) ;
                    // Storing  JSON item in a Variable
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String author = c.getString(TAG_AUTHOR);
                    String price = "$" + c.getString(TAG_PRICE);
                    String art = c.getString(TAG_ARTWORK);
                    artist[i] = author;
                    songName[i] = name;
                    artwork[i] = art;

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_ID, id);
                    map.put(TAG_NAME, name);
                    map.put(TAG_AUTHOR, author);
                    map.put(TAG_PRICE, price);
                    songList.add(map);

                    //ListAdapter adapter = new SimpleAdapter(HomeActivity.this, songList,
                    //      R.layout.list_cart,
                    //    new String[] { TAG_NAME, TAG_AUTHOR, TAG_PRICE }, new int[] {
                    //  R.id.name,R.id.author, R.id.price});
                    //list.setAdapter(adapter);
                }

                adapter=new LazyAdapter(HomeActivity.this, artwork,artist,songName);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(HomeActivity.this, "You added " + songList.get(+position).get("name") + "with id "
                                + songList.get(+position).get("idsong") + " to cart", Toast.LENGTH_SHORT).show();

                        // Need user id and song id to store in Cart
                        HashMap<String, String> user = session.getUserDetails();
                        String user_id = user.get(SessionManager.KEY_ID);
                        //TODO BUG with user_id, it resolves to 0;
                        new addToCart().execute(user_id, songList.get(+position).get("idsong"));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }


    private class addToCart extends AsyncTask <String, String, String> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Posting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            INSERTmySQL insertToCart = new INSERTmySQL();
            String a = params[0];
            String b = params[1];
            insertToCart.insertToCart(a, b);
            pDialog.dismiss();
            return null;
        }

    }

}
