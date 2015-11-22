package com.cashout.paperless.fintech_paperless_cashout;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    private LinearLayout llay;
    private Context context;

    //
    private PieChart mChart;
    // we're going to display pie chart for smartphones martket shares
    float[] yData = { 5, 10, 15};
    String[] xData = {"TESCO","SAINSBURYS", "ASDA" };

    NetworkOperatorCat(TextView tv, PieChart llay, Context context) {
        this.tv = tv;
        this.mChart = llay;
        this.context = context;
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
            float[] ams = new float[shopping.length];

            for (BankTransactionEntry s : BankTransactionEntry.getEntries()) {
                String des= s.description;
                for(int i=0;i<shopping.length;i++){
                    if(des.contains(shopping[i])){
                        ams[i] += Float.parseFloat(s.amount);
                    }
                }
            }
            yData = ams;
            for(int i=0;i<shopping.length;i++){
                tb_text += String.format("%s: %.2f GBP\n\n", shopping[i], ams[i]);
            }
            tv.setText(tb_text);
            /////////////

            // configure pie chart
            mChart.setUsePercentValues(false);
            mChart.setDescription("...");


            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);
            mChart.setDrawCenterText(true);
            mChart.setCenterText("Shopping");
            mChart.setCenterTextColor(Color.WHITE);

            // enable hole and configure
            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColorTransparent(true);
            mChart.setHoleRadius(40f);
            mChart.setTransparentCircleRadius(100f);

            // enable rotation of the chart by touch
            mChart.setRotationAngle(0);
            mChart.setRotationEnabled(true);

            // set a chart value selected listener
            mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    // display msg when value selected
                    if (e == null)
                        return;

                    /*Toast.makeText(context,
                            xData[e.getXIndex()] + " = " + e.getVal() + "%", Toast.LENGTH_SHORT).show();*/
                }

                @Override
                public void onNothingSelected() {

                }
            });

            // add data
            addData();
            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            // customize legends
            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(10);//7
            l.setYEntrySpace(10);//5
            l.setEnabled(false);
        }
    }

    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "Shops");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

       // for (int c : ColorTemplate.PASTEL_COLORS)
        //    colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new DefaultValueFormatter(6));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // update pie chart
        mChart.invalidate();
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
