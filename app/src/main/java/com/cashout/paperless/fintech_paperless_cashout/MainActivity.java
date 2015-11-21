package com.cashout.paperless.fintech_paperless_cashout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ReceiptEntry> entries = new ArrayList<>();
    TextView tv;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tekstas);
        ReceiptEntry.deleteAll(ReceiptEntry.class);

        findViewById(R.id.button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                new ReceiptEntry("name" + i, "cost" + i, "date" + i, "place" + i, "receiptID" + i, "category" + i).save();
                String stuff = "";
                for (ReceiptEntry re : ReceiptEntry.getEntries()) {
                    stuff += re.itemName + ": " + re.cost + ";\n";
                }
                tv.setText(stuff);
                Toast.makeText(MainActivity.this, "Clicked the button " + i + " times.", Toast.LENGTH_SHORT).show();
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
