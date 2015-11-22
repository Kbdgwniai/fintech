/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cashout.paperless.fintech_paperless_cashout;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new LaunchpadSectionFragment();

                case 1:
                    Fragment frag = new DirectIDFragment();
                    return frag;
                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "QR Scanning";
                case 1:
                    return "Direct ID";
                case 2:
                    return "Categories";
                default:
                    return "Section " + (position + 1);
            }
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {
        TextView tv;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            tv = (TextView) rootView.findViewById(R.id.tekstas);
            ReceiptEntryV2.deleteAll(ReceiptEntryV2.class);
            BankTransactionEntry.deleteAll(BankTransactionEntry.class);

            rootView.findViewById(R.id.demo_external_activity).getBackground().setColorFilter(0xfff55000, PorterDuff.Mode.MULTIPLY);

            rootView.findViewById(R.id.demo_external_activity).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                }
            });

            return rootView;
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
                        stuff += String.format("%s--> %s\n___location:%s\n___quantity:%s\n___date: %s\n\n", (""+re.totalprice+" GBP ------").substring(0,12), re.quantity, re.location, re.itemname,
                                re.date);
                    }
                    stuff += String.format("\n\n\n");
                    tv.setText(stuff);

                }
                else if(resultCode == RESULT_CANCELED){ // Handle cancel
                    Toast.makeText(getView().getContext(), "Scan Cancelled!", Toast.LENGTH_LONG).show();
                }
            }
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



    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            //((TextView) rootView.findViewById(android.R.id.text1)).setText("Categories will be displayed here...");

            new NetworkOperatorCat((TextView) rootView.findViewById(R.id.text02),(PieChart) rootView.findViewById(R.id.chart),rootView.getContext()).sendGetRequest();
            return rootView;
        }
    }

    public static class DirectIDFragment extends Fragment {

        TextView tv;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_did, container, false);
            tv = (TextView) rootView.findViewById(R.id.text01);
            new NetworkOperator(tv).sendGetRequest();
            return rootView;
        }
    }
}
