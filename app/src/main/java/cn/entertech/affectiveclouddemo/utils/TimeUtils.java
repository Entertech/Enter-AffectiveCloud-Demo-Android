package cn.entertech.affectiveclouddemo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cn.entertech.affectiveclouddemo.R;

public class TimeUtils {
    public static String getLocalDate(String GMTTime) {
        String gmtTime = GMTTime.replace("T", "").replace("Z", "");
        SimpleDateFormat inputFormat = new SimpleDateFormat
                ("yyyy-MM-ddHH:mm:ss", Locale.getDefault());
        String localDate = GMTTime;
        try {
            inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            Date date = inputFormat.parse(gmtTime);
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
            localDate = outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localDate;
    }

    public static String getFormatTime(long time, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(time);
    }

    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString.replace("T", " ").replace("Z", ""));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
    public static String second2FormatString(Context context, int totalSecond, boolean isShowSecondWhenZero) {
        int mins = totalSecond / 60;
        int seconds = totalSecond - mins * 60;
        if (seconds == 0 && !isShowSecondWhenZero){
            return mins + context.getString(R.string.min);
        }else{
            return mins + context.getString(R.string.min) + " " + seconds + context.getString(R.string.second);
        }
    }

    public static String second2FormatString(Context context, int totalSecond) {
        return second2FormatString(context, totalSecond,true);
    }
    public static String timeStampToMin(long time) {
        return time / 1000 / 60 + "";
    }

    public static String getDayOfWeek() {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String currentDay = myFormatter.format(System.currentTimeMillis());
            String[] dates = currentDay.split("-");
            String date = dates[0] + "-" + dates[1] + "-01";
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E", Locale.US);
            String str = formatter.format(myDate);
            dayOfweek = str;
        } catch (Exception e) {
            Log.d("TimeUtil", "get day week error " + e.toString());
        }
        return dayOfweek;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static int getDaysOfCurrentMonth() {
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
