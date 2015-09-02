package com.example.rupesh.ouraspect;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URL;

import Utils.SeamCarving;

public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    static Bitmap bitmap;
    static ImageView imgView;
    static Button removingSeams;
    static long pathCost[][];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                bitmap = BitmapFactory
                        .decodeFile(imgDecodableString);

                imgView.setImageBitmap(bitmap);
                AsyncTaskRunner aSynchtask = new AsyncTaskRunner();
                aSynchtask.execute();

                EditText textBox = (EditText)findViewById(R.id.number_of_seams);
                textBox.setVisibility(View.VISIBLE);

                removingSeams = (Button)findViewById(R.id.removingSeams);
                removingSeams.setVisibility(View.VISIBLE);
                removingSeams.setClickable(false);

                Log.d("MainActivity ", "The first Log after loading the image.");

                Log.d("MainActivity ", "After gauss");
                //imgView.setImageBitmap(seamCarving.applyGaussian(seamCarving.toGrayScale(bm)));
                //imgView.setImageBitmap(seamCarving.applySeamCarving());
                Log.d("MainActivity ", "The first Log after loading the image.");


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("MainActivity",e.getMessage());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private class AsyncTaskRunner extends AsyncTask<URL, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... urls) {
            SeamCarving seamCarving = new SeamCarving(bitmap);
           // pathCost = seamCarving.computePathCost();
            Bitmap grayImage = seamCarving.applySeamCarving(bitmap);
            Log.d("doInBackground ", "Entering do in background");
            return grayImage;
        }

        protected void onPostExecute(Bitmap grayImage) {
            Log.d("onPostExecute ", "Entering onPostExecute");
            removingSeams.setClickable(true);
            ImageView imgview = (ImageView)findViewById(R.id.imgView);
            imgview.setImageBitmap(grayImage);
        }

    }

}
