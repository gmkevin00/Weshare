package com.example.user.weshare;

//v1.95

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class ImageComparator {
    private int size = 16;
    private int smallersize = 8;
    private double[] c;
    public static int diffs = 0;
    public static Bitmap graySource;
    public static Bitmap grayTarget;
    public static String hash1 = "";
    public static String hash2 = "";

    public ImageComparator()
    {
        //initBinaryString　
        c = new double[size];
        for (int i=1;i<size;i++) {
            c[i]=1;
        }
        c[0]=1/Math.sqrt(2.0);
    }

    public boolean Similar(Bitmap Source, Bitmap Target){
        boolean Sim;
        int Threshold = 19;

        Bitmap scaledSource = Resize(Source);
        graySource = ToGrayScale(scaledSource);

        double[][] SourceArray = new double[size][size];
        SourceArray = GetRGB(graySource);
        //System.out.println("SourceArray:" + SourceArray.toString());

        double[][] SourceDCTArray = new double[size][size];
        SourceDCTArray = GetDCT(SourceArray);
        //System.out.println("SourceDCTArray:" + SourceDCTArray.toString());

        String SourceHASH = GetHASH(SourceDCTArray);
        hash1 = SourceHASH;

        Bitmap scaledTarget = Resize(Target);
        grayTarget = ToGrayScale(scaledTarget);

        double[][] TargetArray = new double[size][size];
        TargetArray = GetRGB(grayTarget);
        double[][] TargetDCTArray = new double[size][size];
        TargetDCTArray = GetDCT(TargetArray);
        String TargetHASH = GetHASH(TargetDCTArray);
        hash2 = TargetHASH;

        int Diff = HammingDiff(hash1, hash2);
        if(Diff < Threshold){
            Sim = true;
        }
        else{
            Sim = false;
        }
        diffs = Diff;
        Log.d("DebugLog","threadhold:"+Threshold);
        Log.d("DebugLog","diff:"+Diff);
        return Sim;
    }

    public int Getdiffs(){
        return diffs;
    }

    public String Gethash1(){
        return hash1;
    }

    public String Gethash2() { return hash2; }

    /*private void Resize(Bitmap source) {
        source = Bitmap.createScaledBitmap(source, size, size, true);
    }*/

    private Bitmap Resize(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        float scaleX = size / (float) bitmap.getWidth();
        float scaleY = size / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    private Bitmap ToGrayScale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private double[][] GetRGB(Bitmap bmp) {
        double[][] values = new double[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                values[x][y] = bmp.getPixel(x, y);
                /*int pixel = bmp.getPixel(x, y);
                if(x < 3 && y < 3){
                    System.out.println(String.valueOf(pixel));
                }
                values[x][y] = pixel;*/
            }
        }
        return values;
    }

    private double[][] GetDCT(double[][] RGB){
        double[][] DCT = new double[size][size];
        for (int u=0;u<size;u++) {
            for (int v=0;v<size;v++) {
                double sum = 0.0;
                for (int i=0;i<size;i++) {
                    for (int j=0;j<size;j++) {
                        sum+=Math.cos(((2*i+1)/(2.0*size))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*size))*v*Math.PI)*(RGB[i][j]);
                    }
                }
                sum*=((c[u]*c[v])/4.0);
                DCT[u][v] = sum;
            }
        }
        return DCT;
    }

    private String GetHASH(double[][] DCT){
        double total = 0;
        int smallersize = this.smallersize;
        for (int x = 0; x < smallersize; x++) {
            for (int y = 0; y < smallersize; y++) {
                total += DCT[x][y];
            }
        }
        total -= DCT[0][0];

        double avg = total / (double) ((smallersize * smallersize) - 1);

        String HASH = "";

        for (int x = 0; x < smallersize; x++) {
            for (int y = 0; y < smallersize; y++) {
                if (x != 0 && y != 0) {
                    HASH += (DCT[x][y] > avg?"1":"0");
                }
            }
        }
        return HASH;
    }

    private int HammingDiff(String Source, String Target) {
        int counter = 0;
        for (int k = 0; k < Source.length();k++) {
            if(Source.charAt(k) != Target.charAt(k)) {
                counter++;
            }
        }
        return counter;
    }
}
