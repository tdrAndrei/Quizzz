package server.api;

import commons.LeaderboardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.LeaderBoardEntryService;

import java.util.List;

@RestController
@RequestMapping(path = "api/scores")
public class LeaderBoardEntryController {

    private final LeaderBoardEntryService leaderBoardEntryService;

    @Autowired
    public LeaderBoardEntryController(LeaderBoardEntryService leaderBoardEntryService) {
        this.leaderBoardEntryService = leaderBoardEntryService;
    }

    @GetMapping
    public List<LeaderboardEntry> getEntries() {
        return leaderBoardEntryService.getAllEntries();
    }

    @PostMapping
    public ResponseEntity<LeaderboardEntry> registerNewUser(@RequestBody LeaderboardEntry leaderboardEntry) {
        LeaderboardEntry insertedEntry = leaderBoardEntryService.insert(leaderboardEntry);
        if (insertedEntry == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(insertedEntry);
    }
}