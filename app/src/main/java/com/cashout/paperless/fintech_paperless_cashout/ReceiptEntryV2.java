package com.cashout.paperless.fintech_paperless_cashout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    @Ignore
    static private String pdf;
    public static String makeReceiptPDF(List<ReceiptEntryV2> entries) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();        try {
            String file = Environment.getExternalStorageDirectory().getPath() + "/fintech-cashless/pdfs/doc0.pdf";
            pdf = file;
            PdfWriter.getInstance(document, new FileOutputStream(file));            //open the document
            document.open();
            Paragraph p1 = new Paragraph("Receipt details:");
            Font paraFont = new Font(Font.FontFamily.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);            //add paragraph to document
            document.add(p1);            String parText = "";
            String date = "";
            String location = "";
            for (ReceiptEntryV2 re : entries) {
                parText += String.format("%10d    %75s  %1.2f  \n", Math.round(Double.parseDouble(re.quantity)),
                        re.itemname, Double.parseDouble(re.totalprice));
                location = re.location;
                date = re.date;
            }
            Paragraph p2 = new Paragraph(parText);
            Font paraFont2 = new Font(Font.FontFamily.COURIER, 14.0f, 0, CMYKColor.GREEN);
            p2.setAlignment(Paragraph.ALIGN_LEFT);
            p2.setFont(paraFont2);            Paragraph p3 = new Paragraph(location + "\n" + date + "\n\n\n\n");
            paraFont = new Font(Font.FontFamily.COURIER);
            p3.setAlignment(Paragraph.ALIGN_CENTER);
            p3.setFont(paraFont);            document.add(p3);
            document.add(p2);            return file;
        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return null;
    }

    public static void openLatestPDF(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(pdf)), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}