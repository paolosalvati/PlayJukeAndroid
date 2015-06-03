package com.example.paolosalvati.demo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.paolosalvati.demo.R;
import com.example.paolosalvati.demo.dataClasses.UserObject;
import com.example.paolosalvati.demo.dataClasses.WifiObject;
import com.example.paolosalvati.demo.jsonWcf.JsonParserObject;
import com.example.paolosalvati.demo.spotifyDataClasses.SpotifyUserObject;
import com.example.paolosalvati.demo.utilities.GlobalObjects;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class FrgmtActivity extends android.support.v4.app.FragmentActivity {
    ViewPager vp;
    private MobileServiceClient mClient;

    private WifiObject wifiObject;
    private UserObject userObject;
    private   String SERVICE_URI;//  "http://jukeserver.cloudapp.net/JukeSvc.svc/";

    private List<Fragment> getFreagments(){

        List<Fragment> list= new ArrayList<Fragment>();
        list.add(ModelFragment.newIstance("Playjuke","Drive your music to the sky!",R.drawable.jukebox_img));
        list.add(ModelFragment.newIstance("Vote Songs","Vote songs you like!",R.drawable.home2));
        list.add(ModelFragment.newIstance("Listen Playlist","Listen at your favourite songs!!!",R.drawable.home3));
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        vp = (ViewPager) findViewById(R.id.vp_infos);
        ControllerFrament cf = new ControllerFrament(getSupportFragmentManager(),getFreagments());
        vp.setAdapter(cf);

        //Mi connetto al Mobile Service PlayJuke
        try {
            // Creao l'Istanza ZUMO using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    getResources().getString(R.string.azure_zumo_url),    //ZUMO URL
                    getResources().getString(R.string.azure_zumo_key),    //ZUMO KEY
                    // this.getString(R.string.azure_zumo_url),            //ZUMO URL
                    // this.getString(R.string.azure_zumo_key),            //ZUMO KEY
                    this);
        } catch (MalformedURLException e) {
            Log.e("MainActivity:onCreate", "Can not conntect to ZUMO :" + e.getMessage().toString());
            return;
        }


        //Recupero il Bottone di Login tramite l'ID
        ImageButton btnLoginFacebook =(ImageButton)  findViewById(R.id.loginFacebook);
        ImageButton btnLoginGoogle =(ImageButton)  findViewById(R.id.loginGoogle);
/*
        Typeface font = Typeface.createFromAsset(getAssets(), "MyUnderwood.ttf");
        TextView fragment_title =(TextView)  findViewById(R.id.tv_title);
        TextView fragment_subtitle =(TextView)  findViewById(R.id.tv_subtitle);
        fragment_title.setTypeface(font);
        fragment_subtitle.setTypeface(font);

*/

        //Aggiungo Sentinella al Bottone di Login
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lancio l'Autenticazione via ACS sul Mobile Service di Azure
                userLogin(MobileServiceAuthenticationProvider.Facebook);
            }
        });
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lancio l'Autenticazione via ACS sul Mobile Service di Azure
                userLogin(MobileServiceAuthenticationProvider.Google);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_, menu);
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

    /**
     * Authenticate User to ZUMO Client
     */
    private void userLogin(MobileServiceAuthenticationProvider authProvider)
    {

        Log.d("aaaaaaaa", authProvider.toString());

        mClient.login(authProvider,
                new UserAuthenticationCallback() {

                    @Override
                    public void onCompleted(MobileServiceUser user,
                                            Exception exception,
                                            ServiceFilterResponse response
                    )
                    {

                        Log.d("bbbbb","tttt");

                        if (exception == null) {
                            mClient.setContext(getApplicationContext());
                            if (exception == null) {
                                Log.d("bbbbb","dddd");
                                //Set User
                                mClient.setCurrentUser(user);

                                //Setto la connessione ad Acure ZUMO client come variabile globale dell appplicazione
                                GlobalObjects zumoClient = ((GlobalObjects) getApplicationContext());
                                zumoClient.setZumoClient(mClient);


                                Log.d("bbbbb","sss");
                                //Lancio la Menu Activity
                                //Intent loadMenuActivityIntent = new Intent(getApplicationContext(), MenuActivity.class);
                                //startActivity(loadMenuActivityIntent);




                                    //Create WifiObject and Scan wifi list in range
                                    wifiObject = new WifiObject(getApplicationContext());

                                    //Create User Object
                                    userObject = new UserObject();
                                    userObject.setAuthProvider("Facebook");
                                    userObject.setMac(wifiObject.getWifiMAC());
                                    userObject.setSsid(wifiObject.getWifiSSID());
                                    userObject.setOs("Android");
                                    userObject.setUsernameID("paolo.salvati@hotmail.it");

                                    //Con oggetti pre costruiti relativi a User Playlist e Wifi, costruisco Json per la chiamata del wcf
                                    JsonParserObject jsonParserObject = new JsonParserObject(wifiObject,userObject);
                                    String s_json = null;
                                    try {
                                        s_json = jsonParserObject.jsonClientRegistration();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    if (android.os.Build.VERSION.SDK_INT > 9) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                    };

                                    SERVICE_URI = getApplication().getString(R.string.azure_wcf_service_uri);
                                    // POST request to WCF
                                    HttpPost request = new HttpPost(SERVICE_URI + "ClientRegistration");
                                    request.setHeader("Accept", "application/json");
                                    request.setHeader("Content-type", "application/json");


                                    StringEntity entity = null;
                                    try {
                                        Log.d("WebInvoke Client muffa", "uuu : " + s_json);
                                        entity = new StringEntity(s_json, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    request.setEntity(entity);

                                    // Send request to WCF service

                                    DefaultHttpClient httpClient = new DefaultHttpClient();
                                    HttpResponse httpResponse = null;
                                    String songs="";

                                    try {
                                        ResponseHandler<String> responseHandler=new BasicResponseHandler();
                                        String responseBody = httpClient.execute(request,responseHandler);
                                        Log.d("FATTO",responseBody);
                                        songs=responseBody;
                                    } catch (ClientProtocolException e) {
                                        Log.d("FATTO","e1"+e.getMessage());
                                        // TODO Auto-generated catch block
                                    } catch (IOException e) {
                                        Log.d("FATTO","e2"+e.getMessage());
                                    }
                                    if(songs!="") {
                                        //Lancio la Client Activity

                                        //Intent playActivityIntent = new Intent(getApplicationContext(), TabMenuActivity.class);

                                        Intent playActivityIntent = new Intent(getApplicationContext(), ClientActivity.class);
                                        playActivityIntent.putExtra("SONGS", songs);
                                        startActivity(playActivityIntent);
                                    } else{




                                        SpotifyUserObject.userAuthSpotify(FrgmtActivity.this);
                                        //Intent playActivityIntent = new Intent(getApplicationContext(), TabMenuActivity.class);
                                        //Intent playActivityIntent = new Intent(getApplicationContext(), ClientActivity.class);
                                        //playActivityIntent.putExtra("SONGS", songs);
                                        //startActivity(playActivityIntent);
                                    }



                            } else {
                                Log.e("MainActivity:userLogin", "User did not login successfully");
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:

                    Log.d("Marina","b");
                    Intent loadPlayListActivityIntent = new Intent(getApplicationContext(), HostActivity.class);
                    Log.d("Marina","c");
                    GlobalObjects globalObjects = ((GlobalObjects) getApplicationContext());
                    Log.d("Marina","d");
                    globalObjects.setSpotifyAccessToken(response.getAccessToken().toString());
                    Log.d("Marina","e");
                    startActivity(loadPlayListActivityIntent);
                    Log.d("Marina","f");
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

}
