package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

//import com.example.myapplication.adapter.ImageAdapter;
import com.example.myapplication.adapter.CustomAdapter;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int pic_id = 123;
    MaterialButton addImg;
    MaterialButton addGalery;

    GridView gridView;
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    Uri photoUri =  result.getData().getData();
                    try (InputStream inputStream = getContentResolver().openInputStream(photoUri)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        clickImg.setImageBitmap(bitmap);// colocar a imagem da galeria na tela
                        saveImg(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addImg = findViewById(R.id.btnImage);
//        clickImg = findViewById(R.id.ClickImage);
        addGalery = findViewById(R.id.btnGalery);
        gridView = findViewById(R.id.GridView);

        gridView.setAdapter(new CustomAdapter(this, getImagesFromMediaStore(getApplicationContext())));

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                startActivityForResult(camera_intent, pic_id);

            }
        });

        addGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });

    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            Bitmap photo = (Bitmap) data.getExtras()
                    .get("data");
            saveImg(photo);
        }
    }

    protected void saveImg(Bitmap bitmap){

        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                UUID.randomUUID().toString(),
                ""
        );

        if (path != null) {
            Uri photoUri = Uri.parse(path);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(photoUri);
            sendBroadcast(mediaScanIntent);

            //recarrega o grid com nova imagem salva
            gridView.setAdapter(new CustomAdapter(this, getImagesFromMediaStore(getApplicationContext())));

        } else {
            Log.e(TAG, "Erro ao salvar a imagem na galeria");
        }
    }

    private ArrayList<String> getImagesFromMediaStore(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                do {
                    String imagePath = cursor.getString(dataColumnIndex);
                    imagePaths.add(imagePath);
                } while (cursor.moveToNext());
            }
        }

        return imagePaths;
    }
}


