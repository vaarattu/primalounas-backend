package primalounas.backend.primalounasbackend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import primalounas.backend.primalounasbackend.model.FoodTags;
import primalounas.backend.primalounasbackend.model.RestaurantCourse;
import primalounas.backend.primalounasbackend.model.RestaurantDay;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ScheduledService {

    @Autowired
    private RestaurantMenuService restaurantMenuService;

    @Scheduled(cron = "0 7 * * * ?", zone = "Europe/Helsinki")
    public void dailyFetchMenu(){
        log.info("[DAILY] Fetching current menu...");
    }

    @Scheduled(fixedDelay = 1000000)
    public void testFetchMenu(){
        log.info("[TEST] Fetching current menu...");

        try {
            RestaurantWeek week = new RestaurantWeek();
            List<RestaurantDay> items = new ArrayList<>();

            String stringUrl = "https://drive.google.com/uc?id=0B8nQh-fa3RbLMFN0X1QxaDFhYzQ&export=download";
            URL url = new URL(stringUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            con.setRequestProperty("Accept", "*/*");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            BufferedInputStream in = new BufferedInputStream(con.getInputStream());
            HWPFDocument document = new HWPFDocument(in);

            // use these to determine if there's new data or not
            SummaryInformation si = document.getSummaryInformation();
            Date created = si.getCreateDateTime();
            Date lastSave = si.getLastSaveDateTime();

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

            boolean[] days = {false, false, false, false, false};
            List<List<String>> dayMenu = new ArrayList<>();

            for (int i = 7; i < splitsList.size(); i++) {

                String curr = splitsList.get(i);

                if (curr.toLowerCase().startsWith("ma ")){
                    days[0] = true;
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
                if (curr.toLowerCase().startsWith("ti ") && days[0]){
                    days[1] = true;
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
                if (curr.toLowerCase().startsWith("ke ") && days[1]){
                    days[2] = true;
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
                if ((curr.toLowerCase().startsWith("to ") || curr.toLowerCase().startsWith("t0")) && days[2]){
                    days[3] = true;
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
                if (curr.toLowerCase().startsWith("pe ") && days[3]){
                    days[4] = true;
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

            for (int i = 0; i < dayMenu.size(); i++) {
                String[] array = dayMenu.get(i).toArray(new String[0]);
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

                    price = price.split(" ", 2)[1];

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
                        name = name.replaceAll(allergens, "");
                    }
                    name = name.trim();
                    
                    List<FoodTags> tags = new ArrayList<>();
                    for (String s : allergensSplit) {
                        tags.add(FoodTags.valueOf(s));
                    }

                    RestaurantCourse course = new RestaurantCourse();
                    course.setName(name);
                    course.setPrice(price);
                    course.setType(type);
                    //course.setFoodTags(tags.toArray(new FoodTags[0]));

                    courses.add(course);
                }
                RestaurantDay rDay = new RestaurantDay();
                rDay.setDay(day);
                rDay.setCourses(courses);
                items.add(rDay);

                week.setDays(items);

                restaurantMenuService.add(week);
            }
            con.disconnect();
            log.info("Successfully parsed week menu.");
        } catch (Exception e){
            log.error("Error parsing week menu, exception: " + e.getMessage(), e);
        }
    }
}
