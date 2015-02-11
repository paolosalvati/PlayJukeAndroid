package com.example.paolosalvati.demo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

//Cache authentication tokens on the client


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONStringer;

public class MainActivity extends ActionBarActivity {

/*
    //Recupero il Bottone di Load Playlist tramite l'ID
    Button btnLoadPlaylists =(Button) findViewById(R.id.btnLoadPlaylists);
    //Recupero il Bottone di Logout tramite l'ID
    Button btnLogout =(Button) findViewById(R.id.btnLogout);

    private final static String SERVICE_URI = "http://jukeserver.cloudapp.net/JukeServer.svc/";
 */
    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("PAOLO 3","1");
        //Mi connetto al Mobile Service PlayJuke
        try {
            // Creao l'Istanza ZUMO using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://playjuke.azure-mobile.net/",       //ZUMO URL
                    "faMsCpUEYWcknZULBGywlrFxPBDqDM33",         //ZUMO KEY
                    this);

            //Aggiungo Il Mobile Service Al Singleton Briedge in modo che sia accessibile in tutte le Activity
            //SingletonParametersBridge.getInstance().addParameter("ZUMOClient", mClient);



        } catch (MalformedURLException e) {
            Log.e("onCreate",e.getMessage().toString());
            return;
        }


        //Recupero il Bottone di Login tramite l'ID
        Button btnLogin =(Button)  findViewById(R.id.btnLogin);
        //Aggiungo Sentinella al Bottone di Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lancio l'Autenticazione via ACS sul Mobile Service di Azure
                userLogin();
            }
        });
    }



    private void userLogin()
    {


            mClient.login(MobileServiceAuthenticationProvider.Facebook,
                    new UserAuthenticationCallback() {

                        @Override
                        public void onCompleted(MobileServiceUser user,
                                                Exception exception,
                                                ServiceFilterResponse response
                                                )
                        {
                            if (exception == null) {
                                mClient.setContext(getApplicationContext());
                                if (exception == null) {

                                    //Set User
                                    mClient.setCurrentUser(user);

                                    Log.e("PAOLO:MainActivity:userLogin:USER_ID",mClient.getCurrentUser().getUserId());
                                    Log.e("PAOLO:MainActivity:userLogin:ACS_TOKEN",mClient.getCurrentUser().getAuthenticationToken());
                                    Intent loadMenuActivityIntent = new Intent(getApplicationContext(), MenuActivity.class);
                                    loadMenuActivityIntent.putExtra("ZUMO_ACS_USER_ID",mClient.getCurrentUser().getUserId());
                                    loadMenuActivityIntent.putExtra("ZUMO_ACS_TOKEN",mClient.getCurrentUser().getAuthenticationToken());


                                    startActivity(loadMenuActivityIntent);

                                } else {
                                    Log.e("PAOLO:MainActivity:userLogin", "User did not login successfully");
                                }


                            } else {
                                createAndShowDialog("You must login.", "Error");
                                return;
                            }
                        }
                    });




       //Log.d("user id on login", usr.getUserId().toString());
    }





    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }




    /*
    Classe singleton,
    che ha dentro di sè un HashMap di cui la chiave è una stringa e l’elemento è un Object
    cosicchè possiamo inserirci qualsiasi cosa (benedetto Polimorfismo)!
    */
/*
    public static class SingletonParametersBridge {

        private static SingletonParametersBridge instance = null;

        private HashMap<String, Object> map;

        public static  SingletonParametersBridge getInstance() {

            if (instance == null)

            instance = new SingletonParametersBridge();

            return instance;

        }

        private SingletonParametersBridge() {

            map = new HashMap<String, Object>();

        }

        public void addParameter(String key, Object value) {

            map.put(key, value);

        }

        public Object getParameter(String key) {

            return map.get(key);

        }

        public void removeParameter(String key) {

            map.remove(key);

        }

    }
*/
}
