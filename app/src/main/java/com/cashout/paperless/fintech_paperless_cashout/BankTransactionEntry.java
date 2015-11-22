package com.cashout.paperless.fintech_paperless_cashout;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by DeKondra on 21/11/2015.
 */
public class BankTransactionEntry extends SugarRecord<ReceiptEntryV2> {
    String type;
    String description;
    String date;
    String amount;

    public BankTransactionEntry() {
    }

    public BankTransactionEntry(String type, String description, String date, String amount) {
        this.type = type;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public static List<BankTransactionEntry> getEntries() {
        return BankTransactionEntry.listAll(BankTransactionEntry.class);
    }
}

