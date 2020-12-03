package com.app.msqltext;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.msql.MSQLHelper;
import com.app.thread.DataCallback;
import com.box.libs.DebugBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MSQLHelper.init(getApplication(), 1);
        MSQLHelper.registerDatabase(TestV.class);

        super.onCreate(savedInstanceState);

        DebugBox.init(getApplication());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            new Thread(() -> {
                final int count = 4;
                TestV[] array = new TestV[count];
                for (int i = 0; i < count; i++) {
                    TestV testV = new TestV();
                    testV.setTag("testV"+i);
                    testV.setName("182397128371298");
                    testV.setTimes(123);
                    array[i]=testV;
                }
//                MSQLHelper.SQL().insert(array);
                SystemClock.sleep(100);
//                TestV testV = new TestV();
//                testV.setTag("testV11");
//                testV.setName("sdaji");
//                testV.setTimes(12323);
                MSQLHelper.SQL().update(array);
            }).start();
        });
        DebugBox.get().open();
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
            DebugBox.get().open();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}