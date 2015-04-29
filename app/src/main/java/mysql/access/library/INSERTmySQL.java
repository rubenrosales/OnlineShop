package mysql.access.library;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Andrey on 3/12/2015.
 * Insert data into mySQL DB via php script
 *
 * */

//TODO refactor, both method doing same with different strings
 public class INSERTmySQL {

    public INSERTmySQL(){

    }

    public String  insertMailAndPass(String email, String password) {
        String url_set = "http://csufshop.ozolin.ru/insert.php?email=" + email + "&password="+ password;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_set);
            HttpResponse response = httpClient.execute(httpPost);
            return response.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", "Client");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Ioexception", "Ioexption");
            e.printStackTrace();
        }
        return null;
    }

    public void insertToCart (String user_id, String song_id){
        String url_set = "http://csufshop.ozolin.ru/addToCart.php?user_id=" + user_id +"&song_id=" + song_id;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_set);
            HttpResponse response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", "Client");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Ioexception", "Ioexption");
            e.printStackTrace();
        }

    }

    public void deleteFromCart (String user_id, String song_id){
        String url_set = "http://csufshop.ozolin.ru/deleteFromCart.php?user_id=" + user_id +"&song_id=" + song_id;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_set);
            HttpResponse response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", "Client");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Ioexception", "Ioexption");
            e.printStackTrace();
        }

    }

    public  void sendEmail (String email){
        String url = "http://csufshop.ozolin.ru/sendEmail.php?email=" + email;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", "Client");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Ioexception", "Ioexption");
            e.printStackTrace();
        }
    }
}
