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
 * Created by Andrey on 3/8/2015.
 * Class for Cart Activity
 */
public class CartActivity extends Activity {
    SessionManager session;

    String[] artist;
    String[] artwork;
    String[] songName;
    String [] priceArray;


    ListView list;
    TextView name;
    TextView author;
    TextView price;
    TextView allPrice;
    ArrayList <HashMap<String, String> > songList = new ArrayList<>();
    Button btnGoHome;
    Button btnCheckout;
    //URL to get JSON Array
    private static String url = "http://csufshop.ozolin.ru/selectFromCart.php?user_id=";
    //JSON Node Names
    private static final String TAG_ROWS = "rows";
    private static final String TAG_ID = "idsong";
    private static final String TAG_NAME = "name";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PRICE = "price";
    private static final String TAG_ARTWORK = "artwork";
    JSONArray android = null;
    String summaryPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Create session class instance
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        String user_id = user.get(SessionManager.KEY_ID);

        new countSummaryPrice().execute(user_id);



        // load list
        songList = new ArrayList<>();
        new requestSQL().execute(user_id);
        // end of loading list

        btnGoHome = (Button)findViewById(R.id.goHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCheckout = (Button)findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void executeCount(){
        if(summaryPrice == null){
            summaryPrice = "0";
        }
        allPrice=(TextView)findViewById(R.id.twTotalCost);
        allPrice.setText("Total Cost: $" + summaryPrice);
    }

    private class requestSQL extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = (TextView)findViewById(R.id.name);
            author = (TextView)findViewById(R.id.author);
            price = (TextView)findViewById(R.id.price);
            pDialog = new ProgressDialog(CartActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            String user_id = args[0];
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            return jParser.getJSONFromUrl(url + user_id);
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_ROWS);
                int size= android.length();
                artist = new String[size];
                songName = new String[size];
                artwork = new String[size];
                priceArray = new String[size];

                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String author = c.getString(TAG_AUTHOR);
                    String price = "$" + c.getString(TAG_PRICE);
                    String art = c.getString(TAG_ARTWORK);
                    artist[i] = author;
                    songName[i] = name;
                    artwork[i] = art;
                    priceArray[i] = price;

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_ID, id);
                    map.put(TAG_NAME, name);
                    map.put(TAG_AUTHOR, author);
                    map.put(TAG_PRICE, price);
                    songList.add(map);

                    list=(ListView)findViewById(R.id.list);
                    LazyAdapter adapter = new LazyAdapter(CartActivity.this, artwork,artist,songName,priceArray);
                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(CartActivity.this, "You added " + songList.get(+position).get("name") + "with id "
                                    + songList.get(+position).get("idsong") + " to cart", Toast.LENGTH_SHORT).show();
                            // Need user id and song id to store in Cart
                            //HashMap<String, String> user = session.getUserDetails();
                            //String user_id = user.get(SessionManager.KEY_ID);
                            //TODO BUG with user_id, it resolves to 0;
                            //TODO change from addToCart to delete;
                            HashMap<String, String> user = session.getUserDetails();
                            String user_id = user.get(SessionManager.KEY_ID);
                            new removeFromCart().execute(user_id, songList.get(+position).get("idsong"));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //executeCount();
        }
    }

    private class removeFromCart extends AsyncTask <String, String, String> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CartActivity.this);
            pDialog.setMessage("Removing from cart ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            INSERTmySQL deleteFromCart = new INSERTmySQL();
            String a = params[0];
            String b = params[1];
            deleteFromCart.deleteFromCart(a, b);
            pDialog.dismiss();
            return null;
        }
    }

    private class countSummaryPrice extends AsyncTask <String, String, JSONObject> {
        String url_set = "http://csufshop.ozolin.ru/totalCostByUserId.php?user_id=";
        private  ProgressDialog pDialog;
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CartActivity.this);
            //pDialog.setMessage("Login in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            String userid = params[0];
            // Getting JSON from URL
            return jParser.getJSONFromUrl(url_set+userid);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_ROWS);
                JSONObject object = android.getJSONObject(0);
                // Storing  JSON item in a Variable
                //String password = c.getString(TAG_PASSWORD);
                summaryPrice = object.getString(TAG_PRICE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            executeCount();
        }
    }
}
