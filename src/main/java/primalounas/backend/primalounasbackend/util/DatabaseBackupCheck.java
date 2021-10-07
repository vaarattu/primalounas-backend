package primalounas.backend.primalounasbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import primalounas.backend.primalounasbackend.model.CourseVote;
import primalounas.backend.primalounasbackend.model.RestaurantDay;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class DatabaseBackupCheck {

    @Autowired
    private RestaurantMenuService restaurantMenuService;

    @EventListener(ApplicationReadyEvent.class)
    private void ApplicationStarted(){
        ReadLocalFilesToDatabase();
    }

    private void ReadLocalFilesToDatabase() {
        log.info("[BACKUP] Reading local files.");
        String path = "./data_docs/";
        File folder = new File(path);
        List<String> fileNames = new ArrayList<>();
        for (File fileEntry : folder.listFiles()) {
            String fileName = fileEntry.getName();
            if (fileName.endsWith(".json")){
                fileNames.add(fileEntry.getName());
            }
        }
        log.info("[BACKUP] Found " + fileNames.size() + " files.");
        List<RestaurantWeek> weeks = new ArrayList<>();

        for (String fileName : fileNames) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                weeks.add(mapper.readValue(new File(path + fileName), RestaurantWeek.class));
            } catch (Exception e) {

            }
        }

        log.info("[BACKUP] Parsed " + weeks.size() + " files.");

        int counter = 0;
        for (RestaurantWeek week : weeks) {
            week.setId(Common.GenerateWeekIdentifier(week.getWeekName()));
            for(RestaurantDay day : week.getDays()){
                day.setId(Common.GenerateDayIdentifier(day.getDay()));
            }
            RestaurantWeek dbWeek = restaurantMenuService.getWeekById(Common.GenerateWeekIdentifier(week.getWeekName()));
            if (dbWeek == null){
                restaurantMenuService.addNewWeek(week);
                counter++;
            }
        }
        log.info("[BACKUP] Saved " + counter + " files to database.");

        fetchWeekMenu();
    }

    private void fetchWeekMenu(){
        log.info("[FETCH] Fetching current menu.");

        try {
            log.info("[FETCH] Downloading file from google drive.");
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

            log.info("[FETCH] Parsing week menu from document.");
            RestaurantWeek week = Common.ParseWeekFromDocument(document);

            if (NeedToSaveOrUpdateDatabase(Common.GenerateWeekIdentifier(week.getWeekName()), week.getSaveDate())){
                log.info("[FETCH] Saving week to database.");
                restaurantMenuService.addNewWeek(week);
            }

            if (!CurrentWeekSaved(Common.GenerateWeekIdentifier(week.getWeekName()))){
                log.info("[FETCH] Saving week doc and json files to folder.");
                SaveFiles(document, week);
            }

            con.disconnect();
            log.info("[FETCH] Successfully parsed week menu.");
        } catch (Exception e){
            log.error("[FETCH] Error parsing week menu, exception: " + e.getMessage(), e);
        }
    }

    private boolean NeedToSaveOrUpdateDatabase(long weekId, Date saveDate){
        log.info("[FETCH] Checking NeedToSaveOrUpdateDatabase.");
        RestaurantWeek week = restaurantMenuService.getWeekById(weekId);
        if (week == null){
            log.info("[FETCH] Week doesn't exist in database.");
            return true;
        }
        Date weekDate = week.getSaveDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        boolean areEqual = dateFormat.format(weekDate).equals(dateFormat.format(saveDate));
        log.info("[FETCH] Checking LastSavedChanged, are equal: " + areEqual);
        return !areEqual;
    }

    private boolean CurrentWeekSaved(long weekIdentifier){
        String folderPath = "./data_docs/";
        String jsonFilePath = folderPath + weekIdentifier + ".json";
        File file = new File(jsonFilePath);
        log.info("[FETCH] JSON file with path : " + jsonFilePath + " exists in folder: " + (file.isFile()));
        return file.isFile();
    }

    private void SaveFiles(HWPFDocument document, RestaurantWeek week) throws Exception {
        String folderPath = "./data_docs/";
        String jsonFilePath = folderPath + Common.GenerateWeekIdentifier(week.getWeekName());
        String docFilePath = folderPath + week.getWeekName() + "-" + Year.now();
        log.info("[FETCH] Checking if folder exists.");
        if (!Files.isDirectory(Path.of(folderPath))){
            log.info("[FETCH] Folder does not exist, creating folder.");
            Files.createDirectory(Path.of(folderPath));
        }
        log.info("[FETCH] Saving doc file.");
        FileOutputStream fos = new FileOutputStream(docFilePath + ".doc");
        document.write(fos);
        fos.close();
        log.info("[FETCH] Saving json file.");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(jsonFilePath + ".json"), week);
    }
}
