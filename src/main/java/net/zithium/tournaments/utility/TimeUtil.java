/*
 * XLTournaments Plugin
 * Copyright (c) 2020 - 2022 Lewis D (ItsLewizzz). All rights reserved.
 */

package net.zithium.tournaments.utility;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
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

    public static String getStringBetweenDates(ZonedDateTime later) {
        String remaining = null;
        long l = ZonedDateTime.now(later.getZone()).toInstant().toEpochMilli();
        long l1 = later.toInstant().toEpochMilli();
        long rem = Math.abs(l1 - l);

        if (rem > 0) {

            int[] times = splitSeconds(new BigDecimal(rem / 1000));
            if (times[0] > 0) {
                remaining = times[0] + "d";
            }
            if (times[1] > 0) {
                if (remaining == null) {
                    remaining = times[1] + "h";
                } else {
                    remaining = remaining + " " + times[1] + "h";
                }
            }
            if (times[2] > 0) {
                if (remaining == null) {
                    remaining = times[2] + "m";
                } else {
                    remaining = remaining + " " + times[2] + "m";
                }
            }
            if (times[3] > 0) {
                if (remaining == null) {
                    remaining = times[3] + "s";
                } else {
                    remaining = remaining + " " + times[3] + "s";
                }
            }
        }

        if (remaining == null) {
            remaining = "None";
        }

        return remaining;
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
