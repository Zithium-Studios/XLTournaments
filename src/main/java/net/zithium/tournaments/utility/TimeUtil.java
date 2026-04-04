package net.zithium.tournaments.utility;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeUtil {

    public static boolean isWaiting(ZonedDateTime start) {
        return ZonedDateTime.now(start.getZone()).isBefore(start);
    }

    public static boolean isEnded(ZonedDateTime end) {
        return ZonedDateTime.now(end.getZone()).isAfter(end);
    }

    public static Date getDateFromString(String str) {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            date = format.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatTime(long seconds) {
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;

        if(days > 0) return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, sec);
        else if(hours > 0) return String.format("%02dh %02dm %02ds", hours, minutes, sec);
        else if(minutes > 0) return String.format("%02dm %02ds", minutes, sec);
        return String.format("%02ds", sec);
    }

    /**
     * Returns a human-readable string representing the time remaining until the given date.
     * <p>
     * The result is formatted as a space-separated string of time components, e.g.
     * {@code "2d 4h 30m 15s"}, omitting any components with a zero value. Returns
     * {@code "None"} if the given date is in the past or if no time components are non-zero.
     *
     * @param later the future {@link ZonedDateTime} to calculate remaining time until
     * @return a formatted string of the remaining time, or {@code "None"} if the date has passed
     */
    public static String getStringBetweenDates(ZonedDateTime later) {
        long now = ZonedDateTime.now(later.getZone()).toInstant().toEpochMilli();
        long then = later.toInstant().toEpochMilli();
        long rem = then - now;

        if (rem <= 0) return "None";

        int[] times = splitSeconds(new BigDecimal(rem / 1000));

        List<String> parts = new ArrayList<>();
        if (times[0] > 0) parts.add(times[0] + "d");
        if (times[1] > 0) parts.add(times[1] + "h");
        if (times[2] > 0) parts.add(times[2] + "m");
        if (times[3] > 0) parts.add(times[3] + "s");

        return parts.isEmpty() ? "None" : String.join(" ", parts);
    }

    public static int[] splitSeconds(BigDecimal seconds) {
        long longVal = seconds.longValue();
        int days = (int) longVal / 86400;
        int remainder = (int) longVal - (days * 86400);
        int hours = remainder / 3600;
        remainder = remainder - (hours * 3600);
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {days, hours, mins, secs};
        return ints;
    }

    public static ZonedDateTime getStartTime(Timeline timeline, ZoneId zoneId) {
        ZonedDateTime time = ZonedDateTime.now(zoneId);
        switch (timeline) {
            case WEEKLY:
                time = ZonedDateTime.of(LocalDateTime.of(time.with(DayOfWeek.MONDAY).toLocalDate(),
                        time.toLocalDate().atStartOfDay().toLocalTime()), zoneId).plusMinutes(2);
                break;
            case MONTHLY:
                time = ZonedDateTime.of(LocalDateTime.of(time.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate(),
                        time.toLocalDate().atStartOfDay().toLocalTime()), zoneId).plusMinutes(2);
                break;
            case DAILY:
                time = ZonedDateTime.of(LocalDateTime.of(time.toLocalDate(),
                        time.toLocalDate().atStartOfDay().toLocalTime()), zoneId).plusMinutes(2);
                break;
            case HOURLY:
                time = time.withMinute(0).withSecond(0).withNano(0);
                break;
        }
        return time;
    }

    public static ZonedDateTime getEndTime(Timeline timeline, ZoneId zoneId) {
        ZonedDateTime time = ZonedDateTime.now(zoneId);
        switch (timeline) {
            case WEEKLY:
                time = ZonedDateTime.of(LocalDateTime.of(time.with(DayOfWeek.SUNDAY).toLocalDate(),
                        LocalTime.of(23, 59, 59)), zoneId);
                break;
            case MONTHLY:
                time = ZonedDateTime.of(LocalDateTime.of(time.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate(),
                        LocalTime.of(23, 59, 59)), zoneId);
                break;
            case DAILY:
                time = ZonedDateTime.of(LocalDateTime.of(time.toLocalDate(), LocalTime.of(23, 59, 59)), zoneId);
                break;
            case HOURLY:
                time = time.withMinute(59).withSecond(59).withNano(999999999);
                break;
        }
        return time;
    }


    public static String getMonthName(ZonedDateTime date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    }

    public static String getMonthNumber(ZonedDateTime date) {
        return String.valueOf(date.getMonth().getValue());
    }

    public static String getDay(ZonedDateTime date) {
        return String.valueOf(date.getDayOfMonth());
    }

}
