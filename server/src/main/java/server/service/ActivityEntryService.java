package server.service;

import commons.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;
import java.util.List;
import java.util.Optional;

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

    public Optional<Activity> updateById(long id, String title, long consumption, String source){
        Optional<Activity> toUpdate = activityRepository.findById(id);

        if (toUpdate.isEmpty()) {
            return toUpdate;
        } else {
            toUpdate.get().setTitle(title);
            toUpdate.get().setConsumption_in_wh(consumption);
            toUpdate.get().setSource(source);
            activityRepository.save(toUpdate.get());
            return toUpdate;
        }
    }

    public Activity findById(long id){
        if (activityRepository.findById(id).isEmpty()) {
            return null;
        } else {
            return activityRepository.findById(id).get();
        }
    }
}


