package primalounas.backend.primalounasbackend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import primalounas.backend.primalounasbackend.model.RestaurantCourse;
import primalounas.backend.primalounasbackend.model.RestaurantDay;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class Common {
    public static long CurrentWeekIdentifier(){
        LocalDate date = LocalDate.now();
        String yearString = Integer.toString(date.getYear());
        String weekNumberString = Integer.toString(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
        String weekIdentifier = yearString + weekNumberString;
        log.info("[COMMON] Current week identifier: " + weekIdentifier);
        return Long.parseLong(weekIdentifier);
    }
    public static long NextWeekIdentifier(){
        LocalDate date = LocalDate.now();
        date = date.plusWeeks(1);
        String yearString = Integer.toString(date.getYear());
        String weekNumberString = Integer.toString(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
        String weekIdentifier = yearString + weekNumberString;
        log.info("[COMMON] Next week identifier: " + weekIdentifier);
        return Long.parseLong(weekIdentifier);
    }
    public static long GenerateWeekIdentifier(String weekName){
        String week = weekName.split("(?=\\d*$)",2)[1];
        String year = Year.now().toString();
        long identifier = Long.parseLong(year + week);
        return identifier;
    }
    public static long GenerateDayIdentifier(String dayText){
        String[] split = dayText.split("\\.");
        String splitDay = split[0].replaceAll("\\D+","");
        String splitMonth = split[1].replaceAll("\\D+","");
        LocalDate date = LocalDate.of(Year.now().getValue(), Integer.parseInt(splitMonth), Integer.parseInt(splitDay));
        String day = String.valueOf(date.getDayOfYear());
        String year = Year.now().toString();
        long identifier = Long.parseLong(year + day);
        return identifier;
    }
    public static RestaurantWeek ParseWeekFromDocument(HWPFDocument document){
        // use these to determine if there's new data or not
        SummaryInformation si = document.getSummaryInformation();
        Date created = si.getCreateDateTime();
        Date lastSave = si.getLastSaveDateTime();

        RestaurantWeek week = new RestaurantWeek();
        List<RestaurantDay> days = new ArrayList<>();

        // get the latest date from database and compare if file is newer
        week.setSaveDate(lastSave);

        String text = document.getDocumentText();

        text = text.trim().replaceAll("\r+", "\t").replaceAll(" +", " ").replaceAll("\t{2,}|\t+ +\t+", "\t");
        String[] splits = text.split("\t");

        for (int i = 0; i < splits.length; i++) {
            splits[i] = splits[i].trim();
        }

        List<String> splitsList = new ArrayList<>(Arrays.asList(splits));
        splitsList.removeAll(Arrays.asList("", null));

        String companyName = splitsList.get(0);
        String saladPrice = splitsList.get(1);
        String restaurantName = splitsList.get(2);
        String foodPrice = splitsList.get(3);
        String phoneNumber = splitsList.get(4);
        String soupPrice = splitsList.get(5);
        String title = splitsList.get(6);

        week.setWeekName(title);
        week.setId(GenerateWeekIdentifier(week.getWeekName()));

        week.setFoodPrice(foodPrice.split(" ", 2)[1]);
        week.setSaladPrice(saladPrice.split(" ", 2)[1]);
        week.setSoupPrice(soupPrice.split(" ", 2)[1]);

        boolean[] bDays = {false, false, false, false, false};
        List<List<String>> dayMenu = new ArrayList<>();

        for (int i = 7; i < splitsList.size(); i++) {

            String curr = splitsList.get(i);

            if (curr.toLowerCase().startsWith("ma ")){
                bDays[0] = true;
                List<String> monday = new ArrayList<>();
                while (true){
                    String next = splitsList.get(i);
                    if (next.toLowerCase().startsWith("ti ")){
                        break;
                    }
                    monday.add(next);
                    i++;
                }
                dayMenu.add(monday);
            }
            curr = splitsList.get(i);
            if (curr.toLowerCase().startsWith("ti ") && bDays[0]){
                bDays[1] = true;
                List<String> tuesday = new ArrayList<>();
                while (true){
                    String next = splitsList.get(i);
                    if (next.toLowerCase().startsWith("ke ")){
                        break;
                    }
                    tuesday.add(next);
                    i++;
                }
                dayMenu.add(tuesday);
            }
            curr = splitsList.get(i);
            if (curr.toLowerCase().startsWith("ke ") && bDays[1]){
                bDays[2] = true;
                List<String> wednesday = new ArrayList<>();
                while (true){
                    String next = splitsList.get(i);
                    if (next.toLowerCase().startsWith("to ")){
                        break;
                    }
                    wednesday.add(next);
                    i++;
                }
                dayMenu.add(wednesday);
            }
            curr = splitsList.get(i);
            if ((curr.toLowerCase().startsWith("to ") || curr.toLowerCase().startsWith("t0")) && bDays[2]){
                bDays[3] = true;
                List<String> thursday = new ArrayList<>();
                while (true){
                    String next = splitsList.get(i);
                    if (next.toLowerCase().startsWith("pe ")){
                        break;
                    }
                    thursday.add(next);
                    i++;
                }
                dayMenu.add(thursday);
            }
            curr = splitsList.get(i);
            if (curr.toLowerCase().startsWith("pe ") && bDays[3]){
                bDays[4] = true;
                List<String> friday = new ArrayList<>();
                while (true){
                    String next = splitsList.get(i);
                    if (next.toLowerCase().startsWith("lisä")){
                        break;
                    }
                    friday.add(next);
                    i++;
                }
                dayMenu.add(friday);
            }
        }

        for (List<String> menu : dayMenu) {
            String[] array = menu.toArray(new String[0]);
            List<RestaurantCourse> courses = new ArrayList<>();
            String day = array[0];
            String type = "";

            for (int j = 1; j < array.length; j++) {
                String price = "?€";
                String name = array[j];
                if (name.contains("pöytä")) {
                    price = saladPrice;
                    type = "salad";
                } else if (name.contains("keitto")) {
                    price = soupPrice;
                    type = "soup";
                } else {
                    price = foodPrice;
                    type = "main";
                }

                String[] nameSplit = name.split(" ");
                String allergens = nameSplit[nameSplit.length - 1];
                String[] allergensSplit = {};

                boolean foundAllergens = true;
                for (int k = 0; k < allergens.length(); k++) {
                    if (Character.isLowerCase(allergens.charAt(k))) {
                        foundAllergens = false;
                    }
                }
                if (foundAllergens) {
                    allergensSplit = allergens.split(",");
                    name = name.replaceAll(" " + allergens, "");
                }
                name = name.trim();
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                name = name.replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");

                if (name.equals("Juuresosekeittoa")){
                    name = "Juuressosekeittoa";
                }

                List<String> tags = new ArrayList<>(Arrays.asList(allergensSplit));

                RestaurantCourse course = new RestaurantCourse();
                course.setName(name);
                course.setType(type);
                course.setTags(tags);

                courses.add(course);
            }
            RestaurantDay rDay = new RestaurantDay();
            rDay.setDay(day);
            rDay.setCourses(courses);
            rDay.setId(GenerateDayIdentifier(rDay.getDay()));

            days.add(rDay);
        }

        week.setDays(days);

        return week;
    }
}
