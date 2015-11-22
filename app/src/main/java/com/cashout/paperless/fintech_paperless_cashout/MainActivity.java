package com.cashout.paperless.fintech_paperless_cashout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tekstas);
        ReceiptEntryV2.deleteAll(ReceiptEntryV2.class);

        findViewById(R.id.button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN"); intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                for (List<String> list : getResults(contents)) {
                    ReceiptEntryV2 rp = new ReceiptEntryV2(list);
                    rp.save();
                }

                String stuff = "";
                for (ReceiptEntryV2 re : ReceiptEntryV2.getEntries()) {
                    stuff += String.format("%s %s %s %s %s %s %s\n", re.itemname, re.quantity, re.priceperitem,
                            re.totalprice, re.date, re.location, re.receiptid);
                }
                new NetworkOperator(tv).sendGetRequest();
            }
            else if(resultCode == RESULT_CANCELED){ // Handle cancel
                Toast.makeText(this, "Scan Cancelled!", Toast.LENGTH_LONG).show();
            }
        }
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

    public List<List<String>> getResults(String s) {
        List<List<String>> result = new ArrayList<>();

        Pattern idPattern = Pattern.compile("(ID\\d{8})");
        Pattern locPattern = Pattern.compile("\\d{8} (.*)\n");
        Pattern strPattern = Pattern.compile("(.*) (\\d\\.\\d\\d\\d+)x(\\d+\\.\\d\\d) (\\d+\\.\\d\\d)");
        Matcher product = strPattern.matcher(s);

        Matcher idMatcher = idPattern.matcher(s);
        idMatcher.find();
        String receiptId = idMatcher.group(0);

        Matcher locMatcher = locPattern.matcher(s);
        locMatcher.find();
        String location = locMatcher.group(1);

        while (product.find())
        {
            ArrayList<String> oneResult = new ArrayList<>();

            oneResult.add(product.group(1));
            oneResult.add(product.group(2));
            oneResult.add(product.group(3));
            oneResult.add(product.group(4));

            oneResult.add(ReceiptEntryV2.dateToString(ReceiptEntryV2.generateDate()));
            oneResult.add(location);
            oneResult.add(receiptId);

            result.add(oneResult);
        }

        return result;
    }
}
