package com.example.docvision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class Helper {


    public static Bitmap toGrey(Bitmap src) {

        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas c = new Canvas(bmOut);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(src, 0, 0, paint);

        return bmOut;
    }

    // taking too much time
    public static Bitmap adaptiveThresholding(Bitmap src, int window, int C) {

        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int width = src.getWidth();
        int height = src.getHeight();

        int blue, mean, count, y1, y2, x1, x2;

        int[] arr = new int[width * height];

        src.getPixels(arr, 0, width, 0, 0, width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                blue = 0;

                y1 = Math.max(y - (window / 2), 0);
                y2 = Math.min(y + (window / 2), height - 1);
                x1 = Math.max(x - (window / 2), 0);
                x2 = Math.min(x + (window / 2), width - 1);

                for (int r = y1; r <= y2; r++) {
                    for (int c = x1; c <= x2; c++) {
                        int b = src.getPixel(c, r);
                        blue += Color.blue(b);

                    }
                }
                mean = blue / (window * window);

                int b = src.getPixel(x, y);
                int blu = Color.blue(b);

                if (blu >= mean) {
                    bmOut.setPixel(x, y, Color.argb(255, 255, 255, 255));
                } else {
                    bmOut.setPixel(x, y, Color.argb(255, 0, 0, 0));
                }
            }
        }
        return bmOut;
    }


    public static Bitmap threshold(Bitmap src) {
        int thresh1 = 50;
        int thresh2 = 100;
        int thresh3 = 127;
        int thresh4 = 150;
        int thresh5 = 200;

        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int A, R, G, B, pixel;
        int height = src.getHeight();
        int width = src.getWidth();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = src.getPixel(x, y);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                A = Color.alpha(pixel);

                int val = ((R + G + B) / 3);

                if (val < thresh1)//(R >= thresh && G >= thresh && B >= thresh)
                    bmOut.setPixel(x, y, Color.argb(A, thresh1, thresh1, thresh1));
                else if (val < thresh2)
                    bmOut.setPixel(x, y, Color.argb(A, thresh2, thresh2, thresh2));
                else if (val < thresh3)
                    bmOut.setPixel(x, y, Color.argb(A, thresh3, thresh3, thresh3));
                else if (val < thresh4)
                    bmOut.setPixel(x, y, Color.argb(A, thresh4, thresh4, thresh4));
                else if (val < thresh5)
                    bmOut.setPixel(x, y, Color.argb(A, thresh5, thresh5, thresh5));
                else bmOut.setPixel(x, y, Color.argb(A, 255, 255, 255));

            }
        }

        return bmOut;

    }

    public static Bitmap adjustBrightness(Bitmap src, int val) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int A, G, B, R, pixel;
        int height = src.getHeight();
        int width = src.getWidth();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = src.getPixel(x, y);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                A = Color.alpha(pixel);
                G = Math.max(Math.min(255, G + val), 0);
                R = Math.max(Math.min(255, R + val), 0);
                B = Math.max(Math.min(255, B + val), 0);
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));

            }
        }

        return bmOut;
    }

    public static void ada(Bitmap bitmap, String filename, Connect connect) {
        connect.ops("adaptivethresholding", filename, bitmap);
    }

    public static void ocr(Bitmap bitmap, String filename, Connect connect) {
        connect.ops("ocr", filename, bitmap);
    }


}
