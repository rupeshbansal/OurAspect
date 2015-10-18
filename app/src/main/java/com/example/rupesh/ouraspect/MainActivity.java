package com.example.rupesh.ouraspect;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
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

public class MainActivity extends Activity implements View.OnClickListener {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    static Bitmap bitmap , grayImage , energyMap , gaussianImage;
    static ImageView imgView;
    static Button removingSeams , changeToGray , computeGaussian , computeEnergy , loadPicture , applySeamCarving;
    static long pathCost[][];
    static SeamCarving seamCarving;
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
                bitmap = BitmapFactory.decodeFile(imgDecodableString);
                seamCarving = new SeamCarving(bitmap);
//                loadPicture.setVisibility(View.INVISIBLE);
//                loadPicture.setClickable(false);

                imgView.setImageBitmap(bitmap);


                changeToGray = (Button) findViewById(R.id.changeToGray);
                changeToGray.setOnClickListener(this);

                changeToGray.setVisibility(View.VISIBLE);
                changeToGray.setClickable(true);

                computeGaussian = (Button) findViewById(R.id.applyGaussianBlur);
                computeGaussian.setOnClickListener(this);

                computeEnergy = (Button) findViewById(R.id.computeEnergyMap);
                computeEnergy.setOnClickListener(this);

                applySeamCarving = (Button) findViewById(R.id.applySeamCarving);
                applySeamCarving.setOnClickListener(this);


//                AsyncTaskRunner aSynchtask = new AsyncTaskRunner();
//                aSynchtask.execute();
//
//                EditText textBox = (EditText)findViewById(R.id.number_of_seams);
//                textBox.setVisibility(View.VISIBLE);
//
//                removingSeams = (Button)findViewById(R.id.removingSeams);
//                removingSeams.setVisibility(View.VISIBLE);
//                removingSeams.setClickable(false);

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
    static int flag = 0;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.changeToGray:
                grayImage = seamCarving.toGrayScale(bitmap);
                imgView.setImageBitmap(grayImage);
                changeToGray.setVisibility(View.INVISIBLE);
                changeToGray.setClickable(false);
                computeGaussian.setVisibility(View.VISIBLE);
                computeGaussian.setClickable(true);
                break;

            case R.id.applyGaussianBlur:

                gaussianImage = seamCarving.applyGaussian(grayImage);
                imgView.setImageBitmap(gaussianImage);

                computeGaussian.setVisibility(View.INVISIBLE);
                computeGaussian.setClickable(false);
                computeEnergy.setVisibility(View.VISIBLE);
                computeEnergy.setClickable(true);
                break;

            case R.id.computeEnergyMap:
                energyMap = seamCarving.findDiff(gaussianImage, grayImage);
                imgView.setImageBitmap(energyMap);

                computeEnergy.setVisibility(View.INVISIBLE);
                computeEnergy.setClickable(false);
                applySeamCarving.setVisibility(View.VISIBLE);
                applySeamCarving.setClickable(true);

                break;

            case R.id.applySeamCarving:
                Bitmap temp = seamCarving.applySeamCarving(grayImage ,2);
                imgView.setImageBitmap(temp);

                computeEnergy.setVisibility(View.INVISIBLE);
                computeEnergy.setClickable(false);
                break;

            default:
                break;
        }

    }
    public Bitmap toGrayScale(Bitmap image){
        int nCols = image.getWidth();
        int nRows = image.getHeight();

        Bitmap grayScale = Bitmap.createBitmap(nCols, nRows, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);
        return grayScale;
    }
    private class AsyncTaskRunner extends AsyncTask<URL, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... urls) {
            SeamCarving seamCarving = new SeamCarving(bitmap);
           // pathCost = seamCarving.computePathCost();
            Bitmap result = seamCarving.applySeamCarving(bitmap , 2);
            Log.d("doInBackground ", "Entering do in background");
            return result;
        }

        protected void onPostExecute(Bitmap result) {
            Log.d("onPostExecute ", "Entering onPostExecute");
            removingSeams.setClickable(true);
            ImageView imgview = (ImageView)findViewById(R.id.imgView);
            imgview.setImageBitmap(result);
//            if(flag == 0){
//                flag = 1;
//                AsyncTaskRunner aSynchtask = new AsyncTaskRunner();
//                aSynchtask.execute();
//            }
        }

    }

}
