package com.example.lemon.onlineshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.lemon.onlineshop.Library.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mysql.access.library.INSERTmySQL;
import mysql.access.library.JSONParser;


public class MainActivity extends ActionBarActivity {
    String LogInEmail = "";
    String LogInPassword = "";
    Button Btnsignin;
    Button Btnlogin;
    SessionManager session;
    String toCheck;
    String userId;
    private static String url = "http://csufshop.ozolin.ru/selectPasswordByEmail.php?email=";
    //JSON Node Names
    private static final String TAG_ROWS = "rows";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_ID = "iduser";
    JSONArray android = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare alert dialog
        // TODO rework alert as http://stackoverflow.com/questions/13268302/alternative-setbutton
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alert, int which) {
                alert.dismiss();
            }
        });

        session = new SessionManager(getApplicationContext());
        // Alert Dialog Manager

        final EditText emailView = (EditText) findViewById(R.id.editTextEmail);
        final EditText passwordView = (EditText) findViewById(R.id.editTextPassword);

        Btnsignin = (Button) findViewById(R.id.signIn);
        // TODO Add email check, if same email already register
        Btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailView.getText().toString();
                final String password = passwordView.getText().toString();
                new SignIn().execute(email, password);
            }
        });

        Btnlogin = (Button) findViewById(R.id.login);
        Btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogInEmail = emailView.getText().toString();
                LogInPassword = passwordView.getText().toString();
                new getUserId().execute(LogInEmail);
                new LogIn().execute(LogInEmail, LogInPassword);
            }
        });
    }
    //Description:
    public void executeLogIn(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        String email = LogInEmail;
        String password = LogInPassword;

        if(email.trim().length() > 0 && password.trim().length() > 0){
            // For testing puspose username, password is checked with sample data
            // email = test@test.com
            // password = test
            // if (password == getPasswordByEmail( String email))
            if(password.equals(toCheck)){
                // TODO fix problem when we need two login attempts
                // after delete alertDialog
                // Creating user login session
                session.createLoginSession(email, password, userId);
                // Staring HomeActivity
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();

            }else{
                alertDialog.setTitle("Login Failed");
                alertDialog.setMessage("Email and Password didn't match, database returned " + toCheck);
                alertDialog.show();
            }
        }else{
            alertDialog.setTitle("Login Failed");
            alertDialog.setMessage("Incorrect Email or Password"+email.trim().length());
            alertDialog.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SignIn extends AsyncTask <String, String, String> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Creating profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            INSERTmySQL insertUser = new INSERTmySQL();
            String a = params[0];
            String b = params[1];
            insertUser.insertMailAndPass(a, b);
            pDialog.dismiss();
            return null;
        }

    }

    private class LogIn extends AsyncTask <String, String, JSONObject> {

        private  ProgressDialog pDialog;
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Login in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            String email = params[0];
            // Getting JSON from URL
            return jParser.getJSONFromUrl(url+email);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_ROWS);
                    JSONObject c = android.getJSONObject(0);
                    // Storing  JSON item in a Variable
                    //String password = c.getString(TAG_PASSWORD);
                    toCheck = c.getString(TAG_PASSWORD);

                executeLogIn();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class getUserId extends AsyncTask <String, String, JSONObject> {
        String url_set = "http://csufshop.ozolin.ru/selectIdByEmail.php?email=";
        private  ProgressDialog pDialog;
        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Login in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            String email = params[0];
            // Getting JSON from URL
            return jParser.getJSONFromUrl(url_set+email);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_ROWS);
                JSONObject c = android.getJSONObject(0);
                // Storing  JSON item in a Variable
                //String password = c.getString(TAG_PASSWORD);
                userId = c.getString(TAG_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}