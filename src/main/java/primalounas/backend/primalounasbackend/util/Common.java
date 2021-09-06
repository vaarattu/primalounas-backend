package primalounas.backend.primalounasbackend.util;

import java.util.Calendar;

public class Common {
    public static int CurrentWeekIdentifier(){
        Calendar calendar = Calendar.getInstance();
        String yearString = Integer.toString(calendar.get(Calendar.YEAR));
        String weekNumberString = Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
        return Integer.parseInt(yearString + weekNumberString);
    }
}
