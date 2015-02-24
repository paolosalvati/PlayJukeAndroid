package com.example.paolosalvati.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

import java.net.MalformedURLException;

//Cache authentication tokens on the client


public class MainActivity extends ActionBarActivity {

    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mi connetto al Mobile Service PlayJuke
        try {
            // Creao l'Istanza ZUMO using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://playjuke.azure-mobile.net/",       //ZUMO URL
                    "faMsCpUEYWcknZULBGywlrFxPBDqDM33",         //ZUMO KEY
                    this);

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


                                    Intent loadMenuActivityIntent = new Intent(getApplicationContext(), MenuActivity.class);
                                    //loadMenuActivityIntent.putExtra("ZUMO_ACS_USER_ID",mClient.getCurrentUser().getUserId());
                                    //loadMenuActivityIntent.putExtra("ZUMO_ACS_TOKEN",mClient.getCurrentUser().getAuthenticationToken());


                                    //Setto la connessione ad Acure ZUMO client come variabile globale dell appplicazione
                                    GlobalObjects zumoClient = ((GlobalObjects) getApplicationContext());
                                    zumoClient.setZumoClient(mClient);
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

}
