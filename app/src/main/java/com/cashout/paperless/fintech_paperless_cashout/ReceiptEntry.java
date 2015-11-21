package com.cashout.paperless.fintech_paperless_cashout;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by DeKondra on 21/11/2015.
 */
public class ReceiptEntry extends SugarRecord<ReceiptEntry> {
    String itemName;
    String cost;
    String date;
    String place;
    String receiptID;
    String category;

    public ReceiptEntry(){
    }

    public ReceiptEntry(String itemName, String cost, String date, String place, String receiptID, String category) {
        this.itemName = itemName;
        this.cost = cost;
        this.date = date;
        this.place = place;
        this.receiptID = receiptID;
        this.category = category;
    }

    public static List<ReceiptEntry> getEntries() {
        return ReceiptEntry.listAll(ReceiptEntry.class);
    }
}