package com.xlw.ui.myactivity;


import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.xlw.onroad.R;

public class ThreeActivity extends ActionBarActivity implements ActionBar.OnNavigationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        ActionBar actionBar=getSupportActionBar();

        actionBar.setTitle("乐途记");

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"境内游","境外游"});
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//加载adpter，设置回调函数。第一个函数是SpinnerAdatper 接口，ArrayAdapter 已经实现
//        SpinnerAdatper 的getView()函数，可以直接使用
        actionBar.setListNavigationCallbacks(myAdapter, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_three, menu);
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

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.d("ListMode", "List select position " + itemPosition + " itemId " + itemId);
        return false;
    }
}
