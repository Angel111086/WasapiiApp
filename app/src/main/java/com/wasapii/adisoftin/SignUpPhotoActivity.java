package com.wasapii.adisoftin;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wasapii.adisoftin.util.AppSharedPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpPhotoActivity extends AppCompatActivity {

    private Button btnNext;
    private Context ctx;
    private static final int CHOICE_AVATAR_FROM_GALLERY=102;
    private static final int CHOICE_AVATAR_FROM_CAMERA=134;
    private static int MY_PERMISSIONS_REQUEST_MEDIA=103;
    private String base64String;
    private CircleImageView profile_pic;
    private Button btn_upload_pic;
    private  static Uri imageUri;
    private static Bitmap finalBitmap=null;
    private TextView tv_redirectToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up_photo);

        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        btnNext = (Button) findViewById(R.id.btn_next);
        tv_redirectToLogin = (TextView) findViewById(R.id.tv_redirectToLogin);
        btn_upload_pic = (Button) findViewById(R.id.btn_upload_pic);

        ctx = SignUpPhotoActivity.this;

        if(!AppSharedPreferences.getProfilePic(ctx).equalsIgnoreCase("")){
            Intent intent = new Intent(ctx, SignUpHashTagActivity.class);
            startActivity(intent);
            finish();
        }

        btn_upload_pic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isAllPermissionsGranted(ctx)) {
                    createImagePickerOption();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(base64String==null){
                    Toast.makeText(ctx,"Please upload a photo.",Toast.LENGTH_SHORT).show();
                }else {

                    AppSharedPreferences.putProfilePic(ctx,base64String);

                    Intent intent = new Intent(ctx, SignUpHashTagActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        tv_redirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAlreadyAccount();
            }
        });

    }


    private void actionAlreadyAccount(){

        Intent i = new Intent(ctx, LoginActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (finalBitmap != null) {
            outState.putString("cameraImageUri", "");
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("cameraImageUri")) {

//            imageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));

            if(finalBitmap!=null){

                profile_pic.setImageBitmap(finalBitmap);

                base64String = convertBitmapToBase64(finalBitmap);

            }/*else{

                getContentResolver().notifyChange(imageUri, null);

                ContentResolver cr = getContentResolver();

                Bitmap bitmap;

                try {

                    bitmap = rotateBitmap(MediaStore.Images.Media.getBitmap(cr, imageUri), getOrientation(getRealPathFromURI(imageUri)));

                    finalBitmap = bitmap;//getResizedBitmap(bitmap, 200);//Bitmap.createScaledBitmap(bitmap, 150, 150, true);

                    profile_pic.setImageBitmap(finalBitmap);

                    base64String = convertBitmapToBase64(finalBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }

    }

    public boolean isAllPermissionsGranted(Context ctx) {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;

            } else {
                ArrayList<String> unGrantedPermissionsList = new ArrayList<>();

                if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissionsList.add(Manifest.permission.CAMERA);
                }

                String[] permissionsArray = new String[unGrantedPermissionsList.size()];
                permissionsArray = unGrantedPermissionsList.toArray(permissionsArray);

                ActivityCompat.requestPermissions(SignUpPhotoActivity.this, permissionsArray, 108);

                return false;
            }

        }else { //permission is automatically granted on sdk<23 upon installation
            Log.v("com.adisoft.mls.temp", "Permission is granted");
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Map<String, Integer> perms = new HashMap<String, Integer>();

        // perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        // Fill with results
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);

        if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            createImagePickerOption();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void createImagePickerOption(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Choose Image Source");

        builder.setItems(new CharSequence[] {"Gallery", "Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:

                                // GET IMAGE FROM THE GALLERY
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, CHOICE_AVATAR_FROM_GALLERY);

                                break;

                            case 1:
                              /*  Intent getCameraImage = new Intent("android.media.action.IMAGE_CAPTURE");

                                File cameraFolder;

                                if (Environment.getExternalStorageState().equals
                                        (Environment.MEDIA_MOUNTED))
                                    cameraFolder = new File(Environment.getExternalStorageDirectory(),
                                            "wasapii/");
                                else
                                    cameraFolder= ctx.getCacheDir();
                                if(!cameraFolder.exists())
                                    cameraFolder.mkdirs();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                                String timeStamp = dateFormat.format(new Date());
                                String imageFileName = "picture_" + timeStamp + ".jpg";

                                File photo = new File(Environment.getExternalStorageDirectory(),
                                        "wasapii/" + imageFileName);
                                getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                imageUri = Uri.fromFile(photo);

                                startActivityForResult(getCameraImage, CHOICE_AVATAR_FROM_CAMERA);*/


                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, CHOICE_AVATAR_FROM_CAMERA);
                                }


                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();

    }





    private Intent getCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        return intent;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

          /*  if (requestCode == CHOICE_AVATAR_FROM_GALLERY) {
                Bitmap avatar = getBitmapFromData(data);
                // this bitmap is the finish image

                Log.i("mls","satish inside onactivity");
                profile_pic.setImageBitmap(avatar);

                base64String= new String(convertBitmapToBase64(avatar));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);*/


            Log.d("wasapii","cameraImageUri onActivityResult ");


        switch(requestCode) {

            case CHOICE_AVATAR_FROM_GALLERY:

                       try {

                            final Uri imageUri = data.getData();
    //                        Log.i("calm","image uri orientation "+getOrientation(getRealPathFromURI(imageUri)));
                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = rotateBitmap(BitmapFactory.decodeStream(imageStream), getOrientation(getRealPathFromURI(imageUri)));
                             finalBitmap = getResizedBitmap(selectedImage, 200);//Bitmap.createScaledBitmap(selectedImage, 150, 150, true);
                             profile_pic.setImageBitmap(finalBitmap);

                            base64String = new String(convertBitmapToBase64(finalBitmap));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }



                break;

            case CHOICE_AVATAR_FROM_CAMERA:

                    try {

                       /* Uri selectedImage = imageUri;

                        Log.e("wasapii","selected image uri="+selectedImage);

                        getContentResolver().notifyChange(selectedImage, null);

                        ContentResolver cr = getContentResolver();
                        Bitmap bitmap;

                        bitmap = rotateBitmap(MediaStore.Images.Media.getBitmap(cr, selectedImage), 0);

                        finalBitmap = getResizedBitmap(bitmap, 200);//Bitmap.createScaledBitmap(bitmap, 150, 150, true);*/

                        Bundle extras = data.getExtras();

                        finalBitmap = (Bitmap) extras.get("data");

                        profile_pic.setImageBitmap(finalBitmap);

                        base64String = convertBitmapToBase64(finalBitmap);

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


             }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (NullPointerException ne){
            ne.printStackTrace();
        }
        return "";
    }


    public int getOrientation(String filepath) {

        Log.d("wasapii","getOrientation "+filepath);

        try{
            ExifInterface exif = new ExifInterface(filepath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//            Log.d("calm","orentation "+orientation);
            return  orientation;

        }catch (IOException e){
            e.printStackTrace();
        }

        return 0;

    }

    public  Bitmap rotateBitmap(Bitmap source, int orientation) {

        int angle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
                angle = 0;
                break;
        }

        Log.e("wasapii","orientation="+orientation);

        Matrix mat = new Matrix();

        if (angle == 0 && source.getWidth() > source.getHeight())
            mat.postRotate(0);//(90);
        else
            mat.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), mat, true);
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }



    public String convertBitmapToBase64(Bitmap bitmap){
        String convertedString="";

        if(bitmap==null){
            return convertedString;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        convertedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return convertedString;
    }
}
