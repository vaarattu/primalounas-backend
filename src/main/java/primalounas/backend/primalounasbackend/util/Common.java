package primalounas.backend.primalounasbackend.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

@Slf4j
public class Common {
    public static long CurrentWeekIdentifier(){
        LocalDate date = LocalDate.now();
        String yearString = Integer.toString(date.getYear());
        String weekNumberString = Integer.toString(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
        return Long.parseLong(yearString + weekNumberString);
    }
    public static long NextWeekIdentifier(){
        LocalDate date = LocalDate.now();
        date = date.plusWeeks(1);
        String yearString = Integer.toString(date.getYear());
        String weekNumberString = Integer.toString(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
        return Long.parseLong(yearString + weekNumberString);
    }
}
