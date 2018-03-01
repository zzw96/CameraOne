package zwang.com.cameraone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraOneActivity extends AppCompatActivity {

    private static final int ACTIVITY_START_CAMERA_APP = 11;
    private ImageView myPhotoCapturedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_one);
        myPhotoCapturedImageView = findViewById(R.id.caputuredPhotoImageView);
    }

    public void takePhoto(View view) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
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
            myPhotoCapturedImageView.setImageBitmap(modifiedBitmap);
        }
    }
}
