package com.example.paolosalvati.demo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.paolosalvati.demo.R;

import java.util.ArrayList;
import java.util.List;

public class FrgmtActivity extends android.support.v4.app.FragmentActivity {
    ViewPager vp;

    private List<Fragment> getFreagments(){

        List<Fragment> list= new ArrayList<Fragment>();
        list.add(ModelFragment.newIstance("Playjuke","Drive your music to the sky!",R.drawable.imghome0));
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
}
