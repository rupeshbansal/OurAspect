package Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;

/**
 * Created by rupesh on 14/08/15.
 */
public class SeamCarving {
    Bitmap image , energyMap , grayImage;
    int nCols,nRows;
    long dp[][];

    public SeamCarving(Bitmap image) {
        this.image = image;
        this.nCols = image.getWidth();
        this.nRows = image.getHeight();
    }

    public Bitmap toGrayScale(Bitmap image){
        grayImage = Bitmap.createBitmap(nCols, nRows, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayImage);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);
        return grayImage;
    }

    public Bitmap applySeamCarving(Bitmap grayImage , int n){
        for(int z = 0 ; z < n ; z++) {
            dp = new long[nRows][nCols];
//            int key = 0;
//            long minCost = Long.MAX_VALUE;
//
//            for (int i = 0; i < nCols; i++) {
//                long cost = pathCost(0, i);
//                if (cost < minCost) key = i;
//                minCost = (Math.min(minCost, cost));
//            }
//            int x = key;
//            int y = 0;
            int x = 0 , y = 0;
            for(int i = nRows - 1 ; i >= 0 ; i--){
                for(int j = 0 ; j < nCols ; j++){

                    if(i == nRows - 1){
                        int pixel = energyMap.getPixel(j, i);
                        int pixelValue = Color.red(pixel);
                        dp[i][j] = pixelValue;
                        continue;
                    }
                    int pixel = energyMap.getPixel(j, i);
                    int pixelValue = Color.red(pixel);
                    long a = dp[i + 1][j];

                    int rightBuffer = 1;

                    while(j + rightBuffer < nCols && Color.red(grayImage.getPixel(j + rightBuffer , i + 1)) == Color.rgb(255 , 0 , 0)) rightBuffer++;
                    long b = (j + rightBuffer < nCols)?dp[i + 1][j + 1]:Long.MAX_VALUE/2;
                    int leftBuffer = 1;
                    while(j - leftBuffer >= 0 && Color.red(grayImage.getPixel(j - leftBuffer , i + 1)) == Color.rgb(255 , 0 , 0)) leftBuffer--;
                    long c = (j - 1 >= 0)?dp[i + 1][j - 1]:Long.MAX_VALUE/2;
                    dp[i][j] = Math.min(a , Math.min(b , c)) + pixelValue;
//                    if(i == 0){
//                        if(dp[y][x] > dp[y][i]) x = i;
//                    }
                }
            }

            for(int q = nCols - 1 ; q >= 0; q--){
                if(dp[0][x] > dp[0][q]) x = q;
            }

            while (true) {
                grayImage.setPixel(x, y, Color.rgb(255, 0, 0));
                y = y + 1;
                if (y == nRows)
                    break;
                int rightBuffer = 1;
                int leftBuffer = 1;
                int pixelValue = 0;
                if(x + rightBuffer < nCols) pixelValue = Color.red(grayImage.getPixel(x + rightBuffer, y));
                while(rightBuffer + x < nCols && pixelValue == Color.rgb(255 , 0 , 0)) {
                    rightBuffer++;
                    pixelValue = Color.red(energyMap.getPixel(x + rightBuffer, y));
                }

                if(x - leftBuffer >= 0) pixelValue = Color.red(grayImage.getPixel(x - leftBuffer, y));

                while(x - leftBuffer >= 0 && pixelValue == Color.rgb(255 , 0 , 0)) {
                    leftBuffer--;
                    pixelValue = Color.red(energyMap.getPixel(x - leftBuffer, y));
                }


                if (x - leftBuffer >= 0)
                    x = Math.min(dp[y][x - leftBuffer], dp[y][x]) == dp[y][x] ? x : x - leftBuffer;
                if (x + rightBuffer < nCols)
                    x = Math.min(dp[y][x + 1], dp[y][x]) == dp[y][x] ? x : x + rightBuffer;
            }



        }
        return grayImage;
    }

//    public PathCost findLowIntensityPath(int i , int j,int flag){
//        if(i >= nRows ) return new PathCost(0,null);
//        if(j >= nCols || i<0 || j<0) return new PathCost(Long.MAX_VALUE/2,null);
//        if(dp[i][j] != null) return dp[i][j];
//        PathCost costA = findLowIntensityPath(i + 1 , j ,flag);
//        PathCost costB = findLowIntensityPath(i + 1  , j + 1,flag);
//        PathCost costC = findLowIntensityPath(i + 1  , j - 1 ,flag);
//        PathCost minCost;
//        minCost = Math.min(costB.cost, costA.cost)==costB.cost?costB:costA;
//        minCost = Math.min(costC.cost, minCost.cost)==costC.cost?costC:minCost;
//        int pixel = energyMap.getPixel(j,i);
//
//       // int gray = (int)(pixel & 0xFF);
//        int pixelValue = Color.red(pixel);
//        minCost.cost+= pixelValue;
//        ArrayList<Pixels> tmp = (minCost.pixelPath);
//        ArrayList<Pixels> ans = new ArrayList<>();
//        if(tmp!=null)
//            ans.addAll(tmp);
//        ans.add(new Pixels(j, i));
//        minCost = new PathCost(minCost.cost,ans);
//        dp[i][j] = minCost;
//        return minCost;
//    }

    public long pathCost(int i , int j){
        if(i >= nRows) return 0;
        if(j < 0 || j >= nCols || i < 0) return Long.MAX_VALUE/2;
        if(dp[i][j] != 0) return dp[i][j];
        int leftBuffer = 1;
        int rightBuffer = 1;
        while(rightBuffer + j < nCols && grayImage.getPixel(j + rightBuffer , i) == Color.rgb(255 , 0 , 0)) rightBuffer++;
        while(j - leftBuffer >= 0 && grayImage.getPixel(j - leftBuffer , i) == Color.rgb(255 , 0 , 0)) leftBuffer--;

        long a1 = pathCost(i + 1 , j);
        long a2 = pathCost(i + 1 , j - leftBuffer);
        long a3 = pathCost(i + 1 , j + rightBuffer);
        long ans = Math.min(a1, Math.min(a2, a3));
        int pixel = energyMap.getPixel(j, i);
        int pixelValue = Color.red(pixel);
        dp[i][j] = ans + pixelValue;
        return dp[i][j];


    }

    public long[][] computePathCost(){
        Bitmap grayImage = toGrayScale(image);
        Bitmap gaussImage = applyGaussian(grayImage);
        energyMap = findDiff(gaussImage , grayImage);
        for(int i = 0 ; i < nCols ; i++) {
             pathCost(0,i);
        }
        return dp;
    }
    public Bitmap findDiff(Bitmap gaussImage , Bitmap grayImage) {
        energyMap = grayImage.copy(grayImage.getConfig(),true);
        for(int i=0;i<nCols;i++) {
            for (int j = 0; j < nRows; j++) {
                int pixel = grayImage.getPixel(i, j);
                pixel = (int)(pixel & 0xFF);
                int pixel2 = gaussImage.getPixel(i, j);
                pixel2 = (int)(pixel2 & 0xFF);
                int val = Math.abs(pixel - pixel2);
                energyMap.setPixel(i, j, Color.argb(255,val,val,val));
            }
        }
        return energyMap;
    }

    public Bitmap applyGaussian(Bitmap grayImage) {
        Bitmap gaussianImage = grayImage.copy(grayImage.getConfig(),true);

        for(int i=0;i<nCols;i++){
            for(int j=0;j<nRows;j++){
                int sum = 0;
                float count = 0;
                for(int k=1;k<5;k++) {
                    if (i - k >= 0) {
                        int pixel = grayImage.getPixel(i - k, j);
                        pixel = (int)(pixel & 0xFF);
                        sum += pixel;
                        count++;
                    }
                    if (j - k >= 0) {
                        int pixel = grayImage.getPixel(i, j - k);
                        pixel = (int)(pixel & 0xFF);
                        sum += pixel;
                        count++;
                    }
                    if ((i + k) < nCols) {
                        int pixel = grayImage.getPixel(i + k, j);
                        pixel = (int)(pixel & 0xFF);
                        sum += pixel;
                        count++;
                    }
                    if ((j + k) < nRows) {
                        int pixel = grayImage.getPixel(i, j + k);
                        pixel = (int)(pixel & 0xFF);
                        sum += pixel;
                        count++;
                    }

                }
                    sum = Math.round(sum / count);
                gaussianImage.setPixel(i, j,Color.argb(255,sum,sum,sum));

            }
        }
        return gaussianImage;
    }

}
