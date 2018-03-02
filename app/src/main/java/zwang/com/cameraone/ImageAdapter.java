package zwang.com.cameraone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Z on 02/03/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private File imagesFile;

    public ImageAdapter(File folderFile) {
        imagesFile = folderFile;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_images_relative_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File imageFile = imagesFile.listFiles()[position];
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        holder.getImageView().setImageBitmap(petrify(imageBitmap));
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

    @Override
    public int getItemCount() {
        return imagesFile.listFiles().length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.imageGalleryView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
