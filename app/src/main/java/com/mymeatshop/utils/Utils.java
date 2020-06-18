package com.mymeatshop.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mymeatshop.R;
import com.mymeatshop.constants.VerhoeffAlgorithm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String ITS_PREPAID = "Prepaid";
    public static final String ITS_POSTPAID = "Postpaid";

    public static boolean isEmailAddress(String text) {
        String pattern = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
        return text.matches(pattern);
    }

    public static boolean validatePan(String panNo) {

        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
        Matcher matcher = pattern.matcher(panNo);

        return matcher.matches();
    }

    public static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }


    public static boolean validateAadharNumber(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }

    public static String getDateOnly(String date) {
// "TravelDate": "24/04/2019",
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd", Locale.getDefault());
        return timeFormat.format(myDate);
    }

    public static String getMonthYear(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMM, yyyy", Locale.getDefault());
        return timeFormat.format(myDate);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public static Bitmap getCompressedBitmap(String imagePath) {

        float maxHeight = 700.0f;
        float maxWidth = 500.0f;
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

        byte[] byteArray = out.toByteArray();

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static String getTimeDifference(String dateStr1, String dateStr2) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        try {

            Date date1 = simpleDateFormat.parse(dateStr1);
            Date date2 = simpleDateFormat.parse(dateStr2);

            long different = date2.getTime() - date1.getTime();
            return GetFormattedInterval(different);

        } catch (ParseException e) {
            simpleDateFormat =
                    new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
            try {

                Date date1 = simpleDateFormat.parse(dateStr1);
                Date date2 = simpleDateFormat.parse(dateStr2);

                long different = date2.getTime() - date1.getTime();
                return GetFormattedInterval(different);

            } catch (ParseException e1) {
                e1.printStackTrace();
            }

        }
        return "";
    }

    public static String getChangeDate(String dateTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                myDate = dateFormat.parse(dateTime);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMM,dd", Locale.getDefault());
        return timeFormat.format(myDate);
    }

    public static String getDay(String dateTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                myDate = dateFormat.parse(dateTime);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return timeFormat.format(myDate);
    }

    private static String GetFormattedInterval(final long ms) {
        long millis = ms % 1000;
        long x = ms / 1000;
        long seconds = x % 60;

        x /= 60;
        long minutes = x % 60;
        x /= 60;
        long hours = x % 24;

        return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
    }

    public static String getTime(String dateTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                myDate = dateFormat.parse(dateTime);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(myDate);
    }

    public static String getDate(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("EEE d MMM", Locale.getDefault());
        return timeFormat.format(myDate);
    }


    public static String changeDateFormat(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return dateFormat.format(c.getTime());
    }

    public static String changeDateFormatYYYY_MM_DD(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.format(c.getTime());
    }

    public static String changeDateFormatSlash(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return dateFormat.format(c.getTime());
    }

    public static String changeDateFormatTheme(int day, int month, int year) {
//        2019-02-22
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.format(c.getTime());
    }

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTodayDatetime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static String changeDateFormat(String dateStr) {
        String datetime = "";
//        3/27/1970 12:00:00 AM
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat d = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        try {
            Date convertedDate = inputFormat.parse(dateStr);
            datetime = d.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }

    public static String changeDateFormatTravel(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return dateFormat.format(c.getTime());
    }

    public static String changeDateFormatDownline(String dateStr) {
        String datetime = "";
//        3/27/1970 12:00:00 AM
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat d = new SimpleDateFormat("MMMM\ndd,yyyy", Locale.ENGLISH);
        try {
            Date convertedDate = inputFormat.parse(dateStr);
            datetime = d.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }


    public static String getDateTimeFromTimeStamp(Long time, String mDateFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat, Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getDefault());
            Date dateTime = new Date(time);
            return dateFormat.format(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String changeDateFormatDDMMYYYY(String dateStr) {
        String datetime = "";
//        3/27/1970 12:00:00 AM
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {/**/
            Date convertedDate = inputFormat.parse(dateStr);
            datetime = d.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            View v = activity.getCurrentFocus();
            if (v != null) {
                IBinder binder = activity.getCurrentFocus()
                        .getWindowToken();
                if (binder != null) {
                    inputMethodManager.hideSoftInputFromWindow(binder, 0);
                }
            }
        }
    }

    public static Dialog createSimpleDialog1(Context context, String title,
                                             String msg, String btnLabel1, final Method method1) {
        SpannableString str = new SpannableString(title);
        str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.success)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str);
        builder.setMessage(msg);

        builder.setPositiveButton(btnLabel1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (method1 != null)
                            method1.execute();
                    }
                });

        Dialog d = builder.show();
        int dividerId = d.getContext().getResources()
                .getIdentifier("android:id/titleDivider", null, null);
        View divider = d.findViewById(dividerId);
        try {
            divider.setBackgroundColor(ContextCompat.getColor(context, R.color.success));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return d;

    }

    public static String replaceBackSlash(String message) {
        //Log.e("==========Final Result ", message.replaceAll("\\\\", ""));
        return message.replaceAll("\\\\", "");
    }

    public static <T> List<T> getList(String jsonArray, Class<T> clazz) {
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();
        return new Gson().fromJson(jsonArray, typeOfT);
    }

    public static JsonArray getArrayList(String jsonArray) {
        Type typeOfT = TypeToken.getParameterized(List.class).getType();
        return new Gson().fromJson(jsonArray, typeOfT);
    }

    public static String convertInto64(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String setDefault(String text, String defaultValue) {
        if (text == null || text.equalsIgnoreCase(""))
            return defaultValue;
        else {
            return text;
        }
    }

    public static String hashCal(String hashString) {
        byte[] hashseq = hashString.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    public interface Method {
        void execute();
    }


}