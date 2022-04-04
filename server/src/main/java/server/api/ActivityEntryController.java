package server.api;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ActivityRepository;
import server.service.ActivityEntryService;


import java.util.List;


@RestController
@RequestMapping(path = "api/admin")
public class ActivityEntryController {


    private final ActivityEntryService activityEntryService;
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityEntryController(ActivityEntryService activityEntryService, ActivityRepository activityRepository) {
        this.activityEntryService = activityEntryService;
        this.activityRepository = activityRepository;
    }

    @GetMapping(path = "/display")
    public List<Activity> getEntries() {
        return activityEntryService.getAllEntries();
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Activity> addActivity(@RequestBody Activity activity) {

        if (isNullOrEmpty(activity.getTitle())|| isNullOrEmpty(activity.getImage_path())
                || activity.getConsumption_in_wh() < 0
                || isNullOrEmpty(activity.getSource())) {
            return ResponseEntity.badRequest().build();
        }

        Activity saved = activityRepository.save(activity);
        return ResponseEntity.ok(saved);
    }

    @PutMapping(path = "/edit/{activityId}")
    public ResponseEntity<?> updateActivity(@PathVariable("activityId") Long activityId, @RequestParam String title, @RequestParam long consumption, @RequestParam String source){
        if (activityEntryService.updateById(activityId, title, consumption, source).isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/getById/{activityId}")
    public ResponseEntity<Activity> getById(@PathVariable("activityId") Long id){
        if (activityEntryService.findById(id) == null){
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(activityEntryService.findById(id));
        }
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

