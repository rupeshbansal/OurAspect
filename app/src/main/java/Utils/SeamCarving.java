package Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.InputMismatchException;

/**
 * Created by rupesh on 14/08/15.
 */
public class SeamCarving {
    Bitmap image , energyMap;
    int nCols,nRows;
    PathCost dp[][];

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
        for(int i=0;i<nCols;i++){
            for(int j=0;j<nRows;j++){
                int pixel = grayScale.getPixel(i,j);
                int px = Color.alpha(pixel&0xFF);
                 px = Color.argb(pixel & 0xFF, 0, 0, 0);
                grayScale.setPixel(i,j,px);
            }
        }
        return grayScale;
    }


    public Bitmap applySeamCarving(){
        dp = new PathCost[nCols][nRows];
        Bitmap grayImage = toGrayScale(image);
        Bitmap gaussimage = applyGaussian(grayImage);
        energyMap = findDiff(gaussimage , grayImage);
        int flag = 0;
        PathCost pathcost = new PathCost(Long.MAX_VALUE,null);
//        for(int i=0;i<flag*nCols+(1-flag)*nRows;i++) {
//            PathCost getcost= findLowIntensityPath(i*flag, i*(1-flag), 0);
//            pathcost = (Math.min(pathcost.cost, getcost.cost)==pathcost.cost)?pathcost:getcost;
//        }
        return energyMap;
    }

    public PathCost findLowIntensityPath(int i , int j,int flag){
        if(i >= nRows || j >= nCols || i<0 || j<0) return new PathCost(0,null);
        if(dp[i][j] != null) return dp[i][j];
        PathCost costA = findLowIntensityPath(i + (flag*-1) , j + (1-flag)*-1,flag);
        PathCost costB = findLowIntensityPath(i  , j ,flag);
        PathCost costC = findLowIntensityPath(i + (flag) , j + (1-flag),flag);
        PathCost minCost;
        minCost = Math.min(costB.cost, costB.cost)==costB.cost?costB:costA;
        minCost = Math.min(costC.cost, minCost.cost)==costC.cost?costC:minCost;
        int pixel = energyMap.getPixel(j,i);

       // int gray = (int)(pixel & 0xFF);
        minCost.cost+= pixel;
        minCost.pixelPath.add(new Pixels(i,j));
        dp[i][j] = minCost;
        return minCost;
    }

    public Bitmap findDiff(Bitmap gaussimage , Bitmap grayImage) {
        Bitmap energyMap = grayImage.copy(grayImage.getConfig(),true);
        for(int i=0;i<nCols;i++) {
            for (int j = 0; j < nRows; j++) {
                int pixel = grayImage.getPixel(i, j);
                pixel = (int)(pixel & 0xFF);
                int pixel2 = gaussimage.getPixel(i, j);
                pixel2 = (int)(pixel2 & 0xFF);
                energyMap.setPixel(i, j, Math.abs(pixel-pixel2));
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
                    energyMap.setPixel(i, j,sum);

            }
        }
        return energyMap;
    }

}
