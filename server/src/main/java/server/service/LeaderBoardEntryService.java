package server.service;

import commons.LeaderboardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.LeaderboardEntryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LeaderBoardEntryService {

    private final LeaderboardEntryRepository leaderboardRepo;

    @Autowired
    public LeaderBoardEntryService(LeaderboardEntryRepository leaderboardRepo){
        this.leaderboardRepo = leaderboardRepo;
    }

    public List<LeaderboardEntry> getAllEntries() {
        return leaderboardRepo.findAll();
    }

    public Optional<LeaderboardEntry> getById(Long id) {
        if (id < 0 || !leaderboardRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return Optional.of(leaderboardRepo.findById(id).get());
        }
    }

    public LeaderboardEntry insert(LeaderboardEntry entry) {
        if (entry == null || entry.getName().isEmpty()){
            return null;
        }
        return leaderboardRepo.save(entry);
    }
}
