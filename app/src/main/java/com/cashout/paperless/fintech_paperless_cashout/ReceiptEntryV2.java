package com.cashout.paperless.fintech_paperless_cashout;

import com.orm.SugarRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by DeKondra on 21/11/2015.
 */
public class ReceiptEntryV2 extends SugarRecord<ReceiptEntryV2> {
    String quantity;
    String itemname;
    String priceperitem;
    String totalprice;
    String date;
    String location;
    String receiptid;

    public ReceiptEntryV2(){
    }

    public ReceiptEntryV2(String quantity, String itemname, String priceperitem, String totalprice, String date, String location, String receiptid) {
        this.quantity = quantity;
        this.itemname = itemname;
        this.priceperitem = priceperitem;
        this.totalprice = totalprice;
        this.date = date;
        this.location = location;
        this.receiptid = receiptid;
    }

    public ReceiptEntryV2(List<String> list) {
        this(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6));
    }

    public static List<ReceiptEntryV2> getEntries() {
        return ReceiptEntryV2.listAll(ReceiptEntryV2.class);
    }

    public static Date generateDate() {
        return new Date();
    }

    public static String dateToString(Date d) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(d); //2014/08/06 15:59:48
    }
}