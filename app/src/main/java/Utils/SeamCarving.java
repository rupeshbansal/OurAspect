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
    Bitmap image , energyMap;
    int nCols,nRows;
    long dp[][];

    public SeamCarving(Bitmap image) {
        this.image = image;
        this.nCols = image.getWidth();
        this.nRows = image.getHeight();
    }

    public Bitmap toGrayScale(Bitmap image){
        Bitmap grayScale = Bitmap.createBitmap(nCols, nRows, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);
       // int px = grayScale.getPixel(0 , 0);
     //   grayScale.setPixel(0 , 0 , Color.argb(0 , 0 , 0 , 0));
//        for(int i=0;i<nCols;i++){
//            for(int j=0;j<nRows;j++){
//                int pixel = grayScale.getPixel(i,j);
//                int nrewR = pixel&0xFF;
//                int A = Color.alpha(pixel);
//                int R = Color.red(pixel);
//                int G = Color.green(pixel);
//                int B = Color.blue(pixel);
//
//
//            }
//        }
        return grayScale;
    }


    public Bitmap applySeamCarving(){
        dp = new long[nRows][nCols];
        for(int i = 0 ; i < nRows ; i++) Arrays.fill(dp[i] , -1);
        Bitmap grayImage = toGrayScale(image);
        Bitmap gaussimage = applyGaussian(grayImage);
        energyMap = findDiff(gaussimage , grayImage);
        int flag = 0;
        int key=0;
        long minCost = Long.MAX_VALUE;
        for(int i=0;i<flag*nRows+(1-flag)*nCols;i++) {
            long cost= pathCost(0,i);
            minCost = (Math.min(minCost, cost));
            key = i;
        }
        int x = key;
        int y=0;


        while(true){
            energyMap.setPixel(x,y,Color.rgb(255,0,0));
            y=y+1;
            if(y==nRows)
                break;
            if(x-1>=0)
                x = Math.min(dp[y][x-1],dp[y][x])==dp[y][x]?x:x-1;
            if(x+1<nCols)
                x = Math.min(dp[y][x+1],dp[y][x])==dp[y][x]?x:x+1;

        }
        return energyMap;
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
        if(dp[i][j] != -1) return dp[i][j];

        long a1 = pathCost(i + 1 , j);
        long a2 = pathCost(i + 1 , j + 1);
        long a3 = pathCost(i + 1 , j - 1);
        long ans = Math.min(a1, Math.min(a2, a3));
        int pixel = energyMap.getPixel(j, i);
        int pixelValue = Color.red(pixel);
        dp[i][j] = ans + pixelValue;
        return dp[i][j];
    }

    public Bitmap findDiff(Bitmap gaussimage , Bitmap grayImage) {
        Bitmap energyMap = grayImage.copy(grayImage.getConfig(),true);
        for(int i=0;i<nCols;i++) {
            for (int j = 0; j < nRows; j++) {
                int pixel = grayImage.getPixel(i, j);
                pixel = (int)(pixel & 0xFF);
                int pixel2 = gaussimage.getPixel(i, j);
                pixel2 = (int)(pixel2 & 0xFF);
                int val = Math.abs(pixel - pixel2);
                energyMap.setPixel(i, j, Color.argb(255,val,val,val));
            }
        }
        return energyMap;
    }

    public Bitmap applyGaussian(Bitmap grayImage) {
        Bitmap energyMap = grayImage.copy(grayImage.getConfig(),true);

        for(int i=0;i<nCols;i++){
            for(int j=0;j<nRows;j++){
                int sum = 0;
                float count = 0;
                for(int k=1;k<15;k++) {
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
                    energyMap.setPixel(i, j,Color.argb(255,sum,sum,sum));

            }
        }
        return energyMap;
    }

}
