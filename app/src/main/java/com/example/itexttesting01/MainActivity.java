package com.example.itexttesting01;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    TextView nameedittxt,ageedittxt,numberedittxt,locationedittxt;
    Button submit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameedittxt = findViewById(R.id.nameTxt);
        ageedittxt = findViewById(R.id.ageTxt);
        numberedittxt = findViewById(R.id.mobileNumberTxt);
        locationedittxt  = findViewById(R.id.locationTxt);
        submit = findViewById(R.id.submitBtn);

        submit.setOnClickListener(v -> {
            String name = nameedittxt.getText().toString();
            String age = ageedittxt.getText().toString();
            String number = numberedittxt.getText().toString();
            String location = locationedittxt.getText().toString();

            try {
                createPDF(name,age,number,location);
            } catch (FileNotFoundException e) {
               e.printStackTrace();
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private  void createPDF(String name, String age, String number, String location) throws FileNotFoundException{
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath,"myInvoice.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        //size of page
        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0,0,0,0);

        //top image/logo picture
        Drawable drawable = getDrawable(R.drawable.tourist);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);

        //Title
        Paragraph visitorTicket = new Paragraph("Visitor Ticket").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);
        Paragraph gom = new Paragraph("Tourism Department\n"+"Government of Maharashtra,India").setTextAlignment(TextAlignment.CENTER).setFontSize(12);
        Paragraph pune = new Paragraph("Pune").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER);

        float[] width = {100f,100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell().add(new Paragraph("Visitor Name")));
        table.addCell(new Cell().add(new Paragraph(name)));

        table.addCell(new Cell().add(new Paragraph("Visitor Age")));
        table.addCell(new Cell().add(new Paragraph(age)));

        table.addCell(new Cell().add(new Paragraph("Visitor Mobile No.")));
        table.addCell(new Cell().add(new Paragraph(number)));

        table.addCell(new Cell().add(new Paragraph("Location/ Place ")));
        table.addCell(new Cell().add(new Paragraph(location)));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        table.addCell(new Cell().add(new Paragraph("Date:")));
        table.addCell(new Cell().add(new Paragraph(LocalDate.now().format(dateTimeFormatter).toString())));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a");
        table.addCell(new Cell().add(new Paragraph("Time")));
        table.addCell(new Cell().add(new Paragraph(LocalTime.now().format(timeFormatter).toString())));

        table.addCell(new Cell().add(new Paragraph("Price")));
        table.addCell(new Cell().add(new Paragraph("50 rupees")));

        BarcodeQRCode qrCode = new BarcodeQRCode(name+"\n"+age+"\n"+number+"\n"+location+"\n"+LocalDate.now().format(dateTimeFormatter)+"\n"+LocalTime.now().format(timeFormatter));
        PdfFormXObject pdfFormXObject = qrCode.createFormXObject(ColorConstants.BLACK,pdfDocument);
        Image qrCodeImage = new Image(pdfFormXObject).setWidth(80).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);
        document.add(visitorTicket);
        document.add(gom);
        document.add(pune);
        document.add(table);
        document.add(qrCodeImage);

        document.close();
        Toast.makeText(this,"invoice pdf created",Toast.LENGTH_LONG).show();
    }
}