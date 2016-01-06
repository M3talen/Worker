package it.neokree.materialnavigationdrawer.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;

import java.util.Locale;

/**
 * Class containing some static utility methods.
 * <p/>
 * Created by neokree on 06/01/15.
 */
public class Utils {
    private Utils() {}

    public static int getDrawerWidth(Resources res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            if (res.getConfiguration().smallestScreenWidthDp >= 600 || res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // device is a tablet
                return (int) (320 * res.getDisplayMetrics().density);
            } else {
                return (int) (res.getDisplayMetrics().widthPixels - (56 * res.getDisplayMetrics().density));
            }
        } else { // for devices without smallestScreenWidthDp reference calculate if device screen is over 600 dp
            if ((res.getDisplayMetrics().widthPixels / res.getDisplayMetrics().density) >= 600 || res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                return (int) (320 * res.getDisplayMetrics().density);
            else
                return (int) (res.getDisplayMetrics().widthPixels - (56 * res.getDisplayMetrics().density));
        }
    }

    public static boolean isTablet(Resources res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return res.getConfiguration().smallestScreenWidthDp >= 600;
        } else { // for devices without smallestScreenWidthDp reference calculate if device screen is over 600
            return (res.getDisplayMetrics().widthPixels / res.getDisplayMetrics().density) >= 600;

        }
    }

    public static int getScreenHeight(Activity act) {
        int height = 0;
        Display display = act.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            height = display.getHeight();  // deprecated
        }
        return height;
    }

    public static Point getUserPhotoSize(Resources res) {
        int size = (int) (64 * res.getDisplayMetrics().density);

        return new Point(size, size);
    }

    public static Point getBackgroundSize(Resources res) {
        int width = getDrawerWidth(res);

        int height = (9 * width) / 16;

        return new Point(width, height);
    }

    public static Bitmap getCroppedBitmapDrawable(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
            //return _bmp;
            return output;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public static Bitmap resizeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        try {
            return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void recycleDrawable(Drawable drawable) {
        try {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                bitmapDrawable.getBitmap().recycle();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public static boolean isRTL() {
        Locale defLocale = Locale.getDefault();
        final int directionality = Character.getDirectionality(defLocale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static void setAlpha(View v, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            v.setAlpha(alpha);
        } else {
            AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }
    }

}
