package com.example.sachu.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 10;
    public static final int GALLERY_REQUEST_CODE = 11;
    Button btncamera, btngallery;
    ImageView ivimage;
    private static int CAMERA_REQUEST = 21200;
    private static int GALLERY_REQUEST = 12546;
    String mImagefileLocation;
    String ImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btncamera = (Button) findViewById(R.id.btncamera);
        btngallery = (Button) findViewById(R.id.btngallery);
        ivimage = (ImageView) findViewById(R.id.ivimage);
    }
    //Camera Permission
    public void takePhoto(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callCameraApp();
        } else {
            String[] permissionRequested = {Manifest.permission.CAMERA};
            requestPermissions(permissionRequested, CAMERA_REQUEST_CODE);
        }
    }
    //Gallery Permission
    public void selectPhotoFromGallery(View view) {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED) {
            callGalleryApp();
        }
        else {
            String[] permissionRequested = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissionRequested,GALLERY_REQUEST_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_REQUEST_CODE) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                callCameraApp();
            }
            else {
                Toast.makeText(this, "External write permission has not been granted", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == GALLERY_REQUEST_CODE) {
            if(grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                callGalleryApp();
            }
            else {
                Toast.makeText(this, "External read permission has not been granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void callCameraApp(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        ImageName = getPictureName();
        File imageFile = new File(pictureDirectory, ImageName);
        //File file = getFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        //to fetch the path of image captured.
        mImagefileLocation = imageFile.getAbsolutePath();
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void callGalleryApp() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Get picture name
        ImageName = getPictureName();
        //get pic location
        mImagefileLocation = pictureDirectory.getPath();
        mImagefileLocation = mImagefileLocation + "/"+ ImageName;
        //get Uri representation
        Uri data = Uri.parse(mImagefileLocation);
        //get all image types
        intent.setDataAndType(data, "image/*");
        startActivityForResult(intent,GALLERY_REQUEST );

    }

    private File getFile() {
        File folder = new File("sdcard/camera_app");
        if(!folder.exists()) {
            folder.mkdir();
        }
        ImageName = getPictureName();
        File image_file = new File (folder, ImageName);
        return image_file;
    }


    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return "Profile Pic" + timestamp+ ".jpg";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {

                String path = mImagefileLocation; //"sdcard/camera_app/"+ImageName;
                Toast.makeText(this, path, Toast.LENGTH_LONG).show();
                ivimage.setImageDrawable(Drawable.createFromPath(path));
            }
           else if(requestCode == GALLERY_REQUEST) {
                //The address of the image on sdcard
                Uri imageUri = data.getData();
                //declare a stream to read the image
                InputStream inputStream;
                //We are getting input stream based on the Uri of the Image
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    //get a bitmap from the stream
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Toast.makeText(this, mImagefileLocation, Toast.LENGTH_LONG).show();
                    ivimage.setImageBitmap(bitmap);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open Image", Toast.LENGTH_LONG).show();
                }

            }

        }
    }
}

