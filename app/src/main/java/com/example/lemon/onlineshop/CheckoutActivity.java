package com.example.lemon.onlineshop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lemon.onlineshop.Library.SessionManager;
import java.util.HashMap;
import mysql.access.library.AccessMYSQL;


/**
 * Created by Andrey on 4/22/2015.
 * Class for activity_checkout
 */
public class CheckoutActivity extends Activity {
    SessionManager session;
    Button btnGoToCart;
    Button btnPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Create session class instance
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        // get user data from session
        final HashMap<String, String> user = session.getUserDetails();


        btnGoToCart = (Button)findViewById(R.id.goCart);
        btnGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnPurchase = (Button)findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View view) {
                new SendMail().execute(user.get(SessionManager.KEY_EMAIL));
                Intent intent = new Intent(getApplication(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class SendMail extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckoutActivity.this);
            pDialog.setMessage("Sending Email ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            AccessMYSQL insertToCart = new AccessMYSQL();
            String email = params[0];
            insertToCart.sendEmail(email);
            pDialog.dismiss();
            return null;
        }
    }
}
