package primalounas.backend.primalounasbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
import java.net.URLConnection;
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
        String path = "./doc_files/";

        if (Files.isDirectory(Path.of(path))){

        File folder = new File(path);
        List<String> fileNames = new ArrayList<>();
        for (File fileEntry : folder.listFiles()) {
            String fileName = fileEntry.getName();
            if (fileName.endsWith(".doc")){
                fileNames.add(fileEntry.getName());
            }
        }
        log.info("[BACKUP] Found " + fileNames.size() + " files.");
        List<RestaurantWeek> weeks = new ArrayList<>();

        for (String fileName : fileNames) {
            try {
                weeks.add(Common.ParseWeekFromDocument(new HWPFDocument(new POIFSFileSystem(new File(path + fileName)))));
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

        }
        fetchWeekMenu();
    }

    private void fetchWeekMenu(){
        log.info("[FETCH] Fetching current menu.");

        try {
            log.info("[FETCH] Downloading file from google drive.");

            String ck = "rtFa=QeqvMIRi5YHhldHm4UPY8dUfOn023VZ7xag5Mgr9FO0mRDc0QzQ2RjAtNUVGMS00RkVFLTkzQzktMDg2MUE0QTRBQTdFIzEzMjkyODgwMDg2MjQ1MTY0MiNCQkVEMkRBMC03MDQ5LTQwMDAtMTRGNi04NTIyQzg2MjQzRDEjSlVIQS5BTEEtUkFOVEFMQSU0MFBSSU1BUE9XRVIuQ09NXRETYtpbjNIDLCraE3WR0x89WBIIeHQVV3TFpjYXqlH4EgiekCWVfaAr1DXZLmai0w4YYTFYdxCwveUmkWs4uLJqJsGYFAsOf8ZcUVtPaVIHNcAlXwwCoYIaRfLxnzwlWzhSFxvgTm+6zKmr1/3sDXR9VsDycUG79931XxTkWm56QPKEdLp6WoVAQogEL+zwNL89F6MyXg3P3ESwVeEyOYGbIGsRop3HXyXaf16lSuQ7egebk05GlfZ7LTytcAF34REFVCuz7Shyx4QGnaR3xEMsornlcpVtUPLw4u9lE7b3dlZOYgg5XSj5qpnLQEp1o9Ten3XfYeALbMJYjTQCGZ8AAAA=;" +
                    "FedAuth=77u/PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U1A+VjEyLDBoLmZ8bWVtYmVyc2hpcHwxMDAzMjAwMTNjOWY3MWE2QGxpdmUuY29tLDAjLmZ8bWVtYmVyc2hpcHxqdWhhLmFsYS1yYW50YWxhQHByaW1hcG93ZXIuY29tLDEzMjkxNTQ1NDg1MDAwMDAwMCwxMzI5MDUzNDMzNTAwMDAwMDAsMTMyOTMzMTIwODYyMjk0NzcwLDYyLjI0OC4xMzMuMzIsNjcsZDc0YzQ2ZjAtNWVmMS00ZmVlLTkzYzktMDg2MWE0YTRhYTdlLCxiNWJhNjM0Zi1hM2JlLTQ4YjMtYmI3Ny03NTVhYjQxODhjNzcsYmJlZDJkYTAtNzA0OS00MDAwLTE0ZjYtODUyMmM4NjI0M2QxLGJiZWQyZGEwLTcwNDktNDAwMC0xNGY2LTg1MjJjODYyNDNkMSwsMCwxMzI5Mjk2NjQ4NjE5ODIzNDIsMTMyOTMxMzkyODYxOTgyMzQyLCwsZXlKNGJYTmZZMk1pT2lKYlhDSkRVREZjSWwwaUxDSjRiWE5mYzNOdElqb2lNU0lzSW5CeVpXWmxjbkpsWkY5MWMyVnlibUZ0WlNJNkltcDFhR0V1WVd4aExYSmhiblJoYkdGQWNISnBiV0Z3YjNkbGNpNWpiMjBpTENKMWRHa2lPaUpYTFdKMVZWRnJiakZyWlRGVFJsQkRialZLTUVGUkluMD0sMjY1MDQ2Nzc0Mzk5OTk5OTk5OSwxMzI5Mjg4MDA4NTAwMDAwMDAsYzQzYTY3YTItZGNkMC00YmEyLTlkMzUtN2U2ZTM2YWJhYmJkLCwsLCwsMCwsRFdjVndRRjBUR0FxZDQ5UEwrdmY2N3NQQ00xS3lBeTM4dXRYMURkSGNnMXAydnMrckorVWVhK0QrcXE5Wk5qdVlpWnJIODhWai9kZkpOZzhaT25obGNJd3RiVEFuTFJwZHZRVFRHWWVBNWFuN3dzRUpoSEdJYWJqelNyQmRNR0RSK1VyWVgwcHhxMjIzZ0htcGw0dWphYlFTQlR0bGZKMXlwOUxYMnZHNUdBc2NjYlZFVEFDemV0dXlpbkRnL1BJUXBtYUNhK2RwaXpnNWZhckt0NFRBSFJIZkNGRFRKL25YTjlxdEczZmgrTTM5WitBWG56TW5GMGVWQ2x0bUJoWFkzZ1QzUWkxa0YyK09sMEpHUzJyZGlveGVwSXZrOVU5MXgzSngxTjFJbEdqZE0ydmdyOEgydFdDTXE5QlVkMmtZYnhQR2ZCa0tGekwvclBjMmVvTzh3PT08L1NQPg==;";

            URL url2 = new URL("https://primacorporate.sharepoint.com/sites/FI-Tietohallinto/_layouts/15/download.aspx?UniqueId=%7B00994173%2D6152%2D4356%2D9a29%2De4f7bc8ddfbb%7D");
            URLConnection urlConnection = url2.openConnection();
            urlConnection.setRequestProperty("Cookie", ck);
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            XWPFDocument document = new XWPFDocument(in);

            log.info("[FETCH] Parsing week menu from document.");
            RestaurantWeek week = Common.ParseWeekFromDocument(document);

            if (NeedToSaveOrUpdateDatabase(Common.GenerateWeekIdentifier(week.getWeekName()), week.getSaveDate())){
                log.info("[FETCH] Saving week to database.");
                restaurantMenuService.addNewWeek(week);
            }

            if (!CurrentWeekSaved(week)){
                log.info("[FETCH] Saving week doc file to folder.");
                SaveDocFile(document, week);
            }

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

    private boolean CurrentWeekSaved(RestaurantWeek week){
        String folderPath = "./doc_files/";
        String docFilePath = folderPath + week.getWeekName() + "-" + Year.now() + ".doc";
        File file = new File(docFilePath);
        log.info("[FETCH] DOC file with path : " + docFilePath + " exists in folder: " + (file.isFile()));
        return file.isFile();
    }

    private void SaveDocFile(HWPFDocument document, RestaurantWeek week) throws Exception {
        String folderPath = "./doc_files/";
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
    }

    private void SaveDocFile(XWPFDocument document, RestaurantWeek week) throws Exception {
        String folderPath = "./doc_files/";
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
    }
}
