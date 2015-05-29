package com.example.paolosalvati.demo.activities;

import android.util.Log;
import android.view.Menu;



        import android.app.ActionBar;
        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;

import com.example.paolosalvati.demo.R;

public class TabMenuActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabmenu);
        ActionBar ab = getActionBar();
        Log.d("marina","1");

        Log.d("marina","2");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        Log.d("marina","3");
        MenuInflater mMenuInflater = getMenuInflater();
        Log.d("marina","4");
        mMenuInflater.inflate(R.menu.menu_tab, menu);
        Log.d("marina","5");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_mi_client:
                Intent intent = new Intent(this, Act1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}