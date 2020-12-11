package com.app.msqltext;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.msql.MSQLHelper;
import com.app.msql.SQLiteWhere;
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
//                Random random = new Random();
//                final int count = 500;
//                TestV[] array = new TestV[count];
//                for (int i = 0; i < count; i++) {
//                    TestV testV = new TestV();
//                    testV.setTag("testV"+i);
//                    testV.setName("感谢您访问数字尾巴");
//                    testV.setTimes(System.currentTimeMillis());
//                    testV.setVersion(random.nextInt(100));
//                    array[i]=testV;
//                }
//                MSQLHelper.SQL().replace(array);

//                SystemClock.sleep(100);

                List<TestV> list = MSQLHelper.SQL()
                        .model(TestV.class)
                        .query()
                        .EndWith("tag","V5")
                        .find(TestV.class);
                Log.e("noah","size="+list.size());
                for (TestV testV : list) {
                    Log.e("noah",testV.tag()+" --> "+testV.version());
                }
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