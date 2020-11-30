package com.app.msqltext;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.msql.MSQLHelper;
import com.box.libs.DebugBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MSQLHelper.init(getApplication(), 4);
        MSQLHelper.registerDatabase(TestV.class);

        super.onCreate(savedInstanceState);

        DebugBox.init(getApplication());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            new Thread(() -> {
//                MSQLHelper.SQL().rebuildTable(TestV.class);
//                String[] strings = new String[10];
//                List<String> list = Arrays.asList(strings);
//                JSONArray jsonArray = new JSONArray(list);


                final int maxCount = 1;
                long totalTime = 0;
                int index = 0;
                while (index < maxCount) {
                    index++;
                    synchronized (Object.class) {
                        final int dataCount = 100;
                        TestV[] array = new TestV[dataCount];
                        for (int i = 0; i < dataCount; i++) {
                            TestV support = new TestV();
                            support.setName("衬衫");
                            support.setSQLiteID(i * 2 + 1);
                            support.setTag("公子是你吗");
                            support.setVer("v2");
                            array[i] = support;
                        }
                        long start = System.currentTimeMillis();
//                        MSQLHelper.SQL().table(TestVSQL.DEFAULT_TABLE).asWhere().whereEqual("name", "李宇春").updateOrThrow(array[0]);
                        MSQLHelper.SQL().registerTempTable(TestV.class, "4515");

                        final long l = System.currentTimeMillis() - start;
                        totalTime += l;
                        Log.e("noah", "单次耗时:" + l + "ms  平均耗时:" + (totalTime / index) + "ms");
                    }
                    SystemClock.sleep(100);
                }
            }).start();
        });
        long start = System.currentTimeMillis();
        MSQLHelper.SQL().tempTableAsync(TestV.class, "das51").listen(data -> {
            final long l = System.currentTimeMillis() - start;
            Log.e("noah", "单次耗时:" + l + "ms");
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