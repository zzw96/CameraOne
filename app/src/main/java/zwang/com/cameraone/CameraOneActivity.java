package zwang.com.cameraone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraOneActivity extends AppCompatActivity {

    private static final int ACTIVITY_START_CAMERA_APP = 11;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 9;
    private ImageView myPhotoCapturedImageView;
    private String myImageFileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_one);
        myPhotoCapturedImageView = findViewById(R.id.caputuredPhotoImageView);
    }

    public void takePhoto(View view) {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
             callCameraApp();
        } else {
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this,
                        "External Storage Permission Required to Save Images!",
                        Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callCameraApp();
            } else {
                Toast.makeText(this,
                        "External Write Permission Has not been Granted!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void callCameraApp() {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String authorities = getApplicationContext().getPackageName() + ".fileprovider";
        Uri imageUri = FileProvider.getUriForFile(this, authorities, photoFile);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            /*
            // For bitmap compressed thumbnail, use full-size image instead!
            Bundle extras = data.getExtras();
            Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
            Bitmap modifiedBitmap = Bitmap.createBitmap(photoCapturedBitmap.getWidth(), photoCapturedBitmap.getHeight(), Bitmap.Config.ARGB_4444);
            for(int i = 0; i < photoCapturedBitmap.getWidth() - 2; i++) {
                for(int j = 0; j < photoCapturedBitmap.getHeight() - 2; j++) {
                    int pixel1 = photoCapturedBitmap.getPixel(i, j);
                    int pixel2 = photoCapturedBitmap.getPixel(i+1, j+1);
                    int red = Math.abs(Color.red(pixel1) - Color.red(pixel2) + 128);
                    int green = Math.abs(Color.green(pixel1) - Color.green(pixel2) + 128);
                    int blue = Math.abs(Color.blue(pixel1) - Color.blue(pixel2) + 128);
                    if(red > 255) red = 255;
                    if(red < 0) red = 0;
                    if(green > 255) green = 255;
                    if(green < 0) green = 0;
                    if(blue > 255) blue = 255;
                    if(blue < 0) blue = 0;
                    modifiedBitmap.setPixel(i, j, Color.rgb(red, green, blue));
                }
            }
            myPhotoCapturedImageView.setImageBitmap(modifiedBitmap);*/

            /*
            // Showing and petrifying full size photo
            Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(myImageFileLocation);
            Bitmap modifiedBitmap = petrify(photoCapturedBitmap);
            myPhotoCapturedImageView.setImageBitmap(modifiedBitmap);
            */

            // Showing photo reduced to imageView size
            // Take up less RAM for large full-images
            rotateImage(setReducedImageSize());
        }
    }

    private Bitmap petrify(Bitmap photoCapturedBitmap) {
        Bitmap modifiedBitmap = Bitmap.createBitmap(photoCapturedBitmap.getWidth(), photoCapturedBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        for(int i = 0; i < photoCapturedBitmap.getWidth() - 2; i++) {
            for(int j = 0; j < photoCapturedBitmap.getHeight() - 2; j++) {
                int pixel1 = photoCapturedBitmap.getPixel(i, j);
                int pixel2 = photoCapturedBitmap.getPixel(i+1, j+1);
                int red = Math.abs(Color.red(pixel1) - Color.red(pixel2) + 128);
                int green = Math.abs(Color.green(pixel1) - Color.green(pixel2) + 128);
                int blue = Math.abs(Color.blue(pixel1) - Color.blue(pixel2) + 128);
                if(red > 255) red = 255;
                if(red < 0) red = 0;
                if(green > 255) green = 255;
                if(green < 0) green = 0;
                if(blue > 255) blue = 255;
                if(blue < 0) blue = 0;
                modifiedBitmap.setPixel(i, j, Color.rgb(red, green, blue));
            }
        }
        return modifiedBitmap;
    }

    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        //save as temp file
        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        //remember location for use in app
        myImageFileLocation = image.getAbsolutePath();

        return image;
    }

    private Bitmap setReducedImageSize() {
        int targetImageViewWidth = myPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = myPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(myImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth,
                cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        //Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(myImageFileLocation, bmOptions);
        //myPhotoCapturedImageView.setImageBitmap(petrify(photoReducedSizeBitmap));

        Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(myImageFileLocation, bmOptions);
        return photoReducedSizeBitmap;

    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(myImageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        myPhotoCapturedImageView.setImageBitmap(petrify(rotatedBitmap));
    }
}
