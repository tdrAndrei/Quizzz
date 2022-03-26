package server.api;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.service.ActivityEntryService;

import java.util.List;

@RestController
@RequestMapping(path = "api/admin/display")
public class ActivityEntryController {


    private final ActivityEntryService activityEntryService;

    @Autowired
    public ActivityEntryController(ActivityEntryService activityEntryService) {
        this.activityEntryService = activityEntryService;
    }

    @GetMapping
    public List<Activity> getEntries() {
        return activityEntryService.getAllEntries();
    }

//    @PostMapping
//    public ResponseEntity<LeaderboardEntry> addLeaderboardEntry(@RequestBody LeaderboardEntry leaderboardEntry) {
//        LeaderboardEntry insertedEntry = leaderBoardEntryService.insert(leaderboardEntry);
//        if (insertedEntry == null) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(insertedEntry);
//    }
}

