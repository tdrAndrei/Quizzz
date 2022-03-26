package server.service;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;
import java.util.List;

@Service
public class ActivityEntryService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityEntryService(ActivityRepository activityRepository){
        this.activityRepository = activityRepository;
    }

    public List<Activity> getAllEntries() {
        return activityRepository.findAll();
    }

//    public Optional<LeaderboardEntry> getById(Long id) {
//        if (id < 0 || !leaderboardRepo.existsById(id)) {
//            return Optional.empty();
//        } else {
//            return Optional.of(leaderboardRepo.findById(id).get());
//        }
//    }
//
//    public LeaderboardEntry insert(LeaderboardEntry entry) {
//        if (entry == null || entry.getName().isEmpty()){
//            return null;
//        }
//        return leaderboardRepo.save(entry);
//    }
}


