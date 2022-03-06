package server.service;

import commons.LeaderboardEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LeaderboardEntryServiceTest {
    private TestLeaderboardEntryRepo repo;
    private LeaderBoardEntryService sut;

    @BeforeEach
    public void setup() {
        repo = new TestLeaderboardEntryRepo();
        sut = new LeaderBoardEntryService(repo);
    }

    private static LeaderboardEntry getEntry(String name, int score) {
        return new LeaderboardEntry(name, score);
    }

    @Test
    public void cannotAddNullEntry(){
        LeaderboardEntry entry = sut.insert(null);
        assertNull(entry);
    }

    @Test
    public void cannotAddNoNameEntry(){
        LeaderboardEntry entry = sut.insert(getEntry("", 10));
        assertNull(entry);
    }


    @Test
    public void insertUsesDatabase(){
        LeaderboardEntry entry = sut.insert(getEntry("test", 5));
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    public void insertSavesValidEntry(){
        LeaderboardEntry entry = sut.insert(getEntry("test", 5));
        assertEquals(entry.getName(), repo.entries.get(0).getName());
    }

    @Test
    public void getAllEntriesUsesDatabase(){
        List<LeaderboardEntry> entries = sut.getAllEntries();
        assertTrue(repo.calledMethods.contains("findAll"));
    }

    @Test
    public void getAllEntriesEmptyDB(){
        List<LeaderboardEntry> entries = sut.getAllEntries();
        assertEquals(entries, new ArrayList<LeaderboardEntry>());
    }

    @Test
    public void getAllEntriesTwoEntries(){
        sut.insert(getEntry("test1", 5));
        sut.insert(getEntry("test2", 10));
        List<LeaderboardEntry> entries = sut.getAllEntries();
        assertEquals(entries.get(0).getName(), "test1");
        assertEquals(entries.get(0).getScore(), 5);
        assertEquals(entries.get(1).getName(), "test2");
        assertEquals(entries.get(1).getScore(), 10);
    }

    @Test
    public void getByIdUsesDBWithInvalidId(){
        sut.getById((long) 0);
        assertTrue(repo.calledMethods.contains("existsById"));
    }

    @Test
    public void getByIdUsesDBWithValidId(){
        LeaderboardEntry entry = sut.insert(getEntry("testEntry", 5));
        sut.getById((long) 1);
        assertTrue(repo.calledMethods.contains("existsById"));
        assertTrue(repo.calledMethods.contains("findById"));
    }

    @Test
    public void getByIdFindsExistingEntry(){
        LeaderboardEntry entry = sut.insert(getEntry("testEntry", 5));
        Optional<LeaderboardEntry> optionalEntry = sut.getById((long) 1);
        assertTrue(optionalEntry.isPresent());
        assertEquals(optionalEntry.get().getName(), entry.getName());
        assertEquals(optionalEntry.get().getScore(), entry.getScore());
    }

    @Test
    public void getByIdFindsNonExistingEntry(){
        Optional<LeaderboardEntry> optionalEntry = sut.getById((long) 1);
        assertFalse(optionalEntry.isPresent());
    }
}
