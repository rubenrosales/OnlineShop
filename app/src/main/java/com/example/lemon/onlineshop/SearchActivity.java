package com.example.lemon.onlineshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;

import com.example.lemon.onlineshop.Library.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mysql.access.library.INSERTmySQL;
import mysql.access.library.JSONParser;

/**
 * Activity for Search layout
 */
public class SearchActivity extends Activity {
    ListView list;
    TextView name;
    TextView city;
    TextView phone;
    String isArtist;
    String[] artist;
    String[] artwork;
    String[] songName;
    String[] priceArray;
    String[] urlArray;
    String fullURL;
    LazyAdapter lAdapter;
    String searchText;
    JSONObject jsonURL;
    ArrayList<HashMap<String, String>> songList = new ArrayList<>();
    SessionManager session;
    MediaPlayer mp = new MediaPlayer();
    //JSON Node Names
    private static final String TAG_ROWS = "rows";
    private static final String TAG_ID = "idsong";
    private static final String TAG_NAME = "name";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PRICE = "price";
    private static final String TAG_ARTWORK = "artwork";
    private static final String TAG_URL = "url";
    JSONArray sAndroid = null;

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
                final RadioButton rb = (RadioButton) findViewById(R.id.rbArtist);
                searchText = searchField;
                if(mp.isPlaying()){ mp.stop();  }
               if(rb.isChecked())
                {
                    isArtist = "artist";
                    new Search().execute(searchField);
                }
                else{
                    isArtist = "song";
                    new Search().execute(searchField);
                }

            }
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isPlaying()){ mp.stop();  }
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private class addToCart extends AsyncTask <String, String, String> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchActivity.this);
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
    private class Search extends AsyncTask <String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            fullURL = urlSearch + searchText+","+isArtist;
            //jsonURL = jParser.getJSONFromUrl("http://csufshop.ozolin.ru/test_search.php?name=Diplo,artist");
            return jParser.getJSONFromUrl(fullURL);
    }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                sAndroid = json.getJSONArray(TAG_ROWS);
                Integer size= sAndroid.length();

                artist = new String[size];
                songName = new String[size];
                artwork = new String[size];
                priceArray = new String[size];
                urlArray = new String[size];

                for(int i = 0; i < sAndroid.length(); i++){
                    JSONObject c = sAndroid.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String author = c.getString(TAG_AUTHOR);
                    String price = "$" + c.getString(TAG_PRICE);
                    String art = c.getString(TAG_ARTWORK);
                    String urlParsed = c.getString(TAG_URL);
                    artist[i] = author;
                    songName[i] = name;
                    artwork[i] = art;
                    priceArray[i] = price;
                    urlArray[i] = urlParsed;
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_ID, id);
                    map.put(TAG_NAME, name);
                    map.put(TAG_AUTHOR, author);
                    map.put(TAG_PRICE, price);
                    songList.add(map);

                    list=(ListView)findViewById(R.id.homeListView);
                    lAdapter = new LazyAdapter(SearchActivity.this, artwork,artist,songName,priceArray);
                    list.setAdapter(lAdapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            if(mp.isPlaying()){
                                mp.stop();
                            }else {

                                try {
                                    mp = new MediaPlayer();
                                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mp.setDataSource(urlArray[position]);
                                    //int mp3Resource = getResources().getIdentifier(mySounds[position],"raw",MediaPlayer.create(getApplicationContext(),R.raw.sound1);
                                    mp.prepare();
                                    mp.start();
                                }
                                catch(Exception e){

                                }
                            }

                            //this stops the song when it finishes i cant get it to work
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    mp.reset(); // finish current activity
                                }
                            });

                        }
                    });
                    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                       int position, long id) {
                            Toast.makeText(SearchActivity.this, "You added " + songList.get(position).get("name") + " with id "
                                    + songList.get(position).get("idsong") + " to cart", Toast.LENGTH_SHORT).show();

                            // Need user id and song id to store in Cart
                            HashMap<String, String> user = session.getUserDetails();
                            String user_id = user.get(SessionManager.KEY_ID);
                            //TODO BUG with user_id, it resolves to 0;
                            new addToCart().execute(user_id, songList.get(position).get("idsong"));
                            return true;
                        }
                    });
                    if(size == null){
                        list.setAdapter(null);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
