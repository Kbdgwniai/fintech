package com.cashout.paperless.fintech_paperless_cashout;

import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DeKondra on 22/11/2015.
 */
public class NetworkOperatorCat {

    private String response = "";
    private TextView tv;

    NetworkOperatorCat(TextView tv) {
        this.tv = tv;
    }

    public void sendGetRequest() {
        new SendGetRequest().execute("https://still-fjord-9559.herokuapp.com/api/defaultuser");
    }

    private class SendGetRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    response = EntityUtils.toString(execute.getEntity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            toTransLists(result);
            String tb_text = "";
            String[] shopping = {"TESCO","SAINSBURYS", "ASDA" };
            double[] ams = new double[shopping.length];

            for (BankTransactionEntry s : BankTransactionEntry.getEntries()) {
                String des= s.description;
                for(int i=0;i<shopping.length;i++){
                    if(des.contains(shopping[i])){
                        ams[i] += Double.parseDouble(s.amount);
                    }
                }
            }
            for(int i=0;i<shopping.length;i++){
                tb_text += String.format("%s: %.2f GBP\n\n", shopping[i], ams[i]);
            }
            tv.setText(tb_text);
        }
    }

    public void toTransLists(String unparsed) {
        ArrayList<String> listdata = new ArrayList<>();
        try {
            JSONObject full = new JSONObject(unparsed);
            JSONArray jArray = full.getJSONArray("Transactions");

            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject trans = jArray.getJSONObject(i);
                    String type = trans.getString("Type");
                    String desc = trans.getString("Description");
                    String date = trans.getString("Date");
                    String amnt = trans.getString("Amount");
                    new BankTransactionEntry(type, desc, date, amnt).save();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
