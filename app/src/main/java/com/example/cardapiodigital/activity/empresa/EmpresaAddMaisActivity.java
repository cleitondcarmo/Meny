package com.example.cardapiodigital.activity.empresa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cardapiodigital.R;
import com.example.cardapiodigital.helper.FirebaseHelper;
import com.example.cardapiodigital.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class EmpresaAddMaisActivity extends AppCompatActivity{

    private ImageView qrImage;
    private String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private File pasta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private File imagem = new File(pasta.getPath() + File.separator + "JPG_" + timeStamp + ".jpg");
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private AppCompatActivity activity;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_add_mais);

        qrImage = findViewById(R.id.qr_image);
        activity = this;

        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference().child("empresas").child(FirebaseHelper.getIdFirebase());;
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Empresa empresa = snapshot.getValue(Empresa.class);
                System.out.println("EMPRESA ID/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////" + empresa.getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        id = empresaRef.getKey();

        qrgEncoder = new QRGEncoder(
                id, null,
                QRGContents.Type.TEXT,
                smallerDimension);
                qrgEncoder.setColorBlack(Color.BLACK);
                qrgEncoder.setColorWhite(Color.WHITE);
                try {
                    bitmap = qrgEncoder.getBitmap();
                    qrImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        findViewById(R.id.save_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        boolean save = new QRGSaver().save(String.valueOf(imagem), id, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                        String result = save ? "Image Saved" : "Image Not Saved";
                        Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        });
    }
}