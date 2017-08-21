package com.app.sinaps.tabtesting.Scaler;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.sinaps.tabtesting.R;

import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sinaps on 19.08.2017.
 */

public class ScalerFragment extends Fragment {

    /** Реквест код для вызова стандартной галереи */
    private final int GALLERY_CODE = 123;
    /** Реквест код для вызова фотокамеры */
    private final int PHOTO_CODE = 234;
    /** Uri изображения полученного из галереи или фотокамеры */
    private Uri imageUri;
    /** Имя файла для сохраниния фотогрфии сделанной фотокамерой */
    private File photo;
    /** Ключ для передачи Uri в активность осущестляющую отображение картинки */
    public static String IMAGE_URI_KEY = "imageUri";
    private Intent intent;
    /** Реквест док для запроса на получение разрешения на использования SD-карты */
    public static final int PERMISSION_REQUEST_CODE = 555;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.scaler_layout, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().findViewById(R.id.scaler_galler_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if(checkPermission())
                    startActivityForResult(intent, GALLERY_CODE);
            }
        });

        getActivity().findViewById(R.id.scaler_photo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                photo = null;
                try{
                    photo = createPhotoFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photo != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                    if(checkPermission())
                        startActivityForResult(intent, PHOTO_CODE);
                }
            }
        });

    }
    /** Проверяет наличие разрешения на считывание из SD-карты
     *
     */
    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }else {
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case GALLERY_CODE:
                    imageUri = data.getData();
                    break;
                case PHOTO_CODE:
                    imageUri = Uri.fromFile(photo);
                    break;
            }
            Intent intent = new Intent(getActivity(), ScalerActivity.class);
            intent.putExtra(IMAGE_URI_KEY, imageUri);
            startActivity(intent);
        }
    }

    /**Герерирует уникальное имя файла для фоторафии
     *
     */
    private File createPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = new File(storageDir, imageFileName);

        return image;
    }
}
