package net.frozenorb.foxtrot.koth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

@AllArgsConstructor
public class KOTHScheduledTime {

    @Getter private int hour;
    @Getter private int minutes;

    public static KOTHScheduledTime parse(String input) {
        String[] split = input.split(":");
        int hour = Integer.parseInt(split[0]);
        int minutes = split.length > 1 ? Integer.parseInt(split[1]) : 0;

        return (new KOTHScheduledTime(hour, minutes));
    }

    public static KOTHScheduledTime parse(Date input) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(input);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return (new KOTHScheduledTime(hour, minutes));
    }

    public Date toDate() {
        Calendar activationTime = Calendar.getInstance();

        activationTime.set(Calendar.HOUR_OF_DAY, hour);
        activationTime.set(Calendar.MINUTE, minutes);
        activationTime.set(Calendar.SECOND, 0);
        activationTime.set(Calendar.MILLISECOND, 0);

        return (activationTime.getTime());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof KOTHScheduledTime) {
            KOTHScheduledTime other = (KOTHScheduledTime) object;

            return (other.hour == this.hour && other.minutes == this.minutes);
        }

        return (false);
    }

    @Override
    public int hashCode() {
        return (hour ^ minutes);
    }

}