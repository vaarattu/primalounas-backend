package primalounas.backend.primalounasbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DatabaseBackupCheck {

    @Autowired
    private RestaurantMenuService restaurantMenuService;

    @EventListener(ApplicationReadyEvent.class)
    public void ReadLocalFilesToDatabase() {
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
            RestaurantWeek dbWeek = restaurantMenuService.getWeekById(Common.GenerateWeekIdentifier(week.getWeekName()));
            if (dbWeek == null){
                restaurantMenuService.addNewWeek(week);
                counter++;
            }
        }
        log.info("[BACKUP] Saved " + counter + " files to database.");
    }
}
