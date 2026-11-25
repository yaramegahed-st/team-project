package entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionTest {

    private StudySession sessionOf(Integer... ids) {
        return new StudySession(10, 20, 30, List.of(ids));
    }

    @Test
    void constructor_setsIdsAndStartsAtFirstCard() {
        StudySession s = sessionOf(1, 2, 3);
        assertEquals(10, s.getSessionId());
        assertEquals(20, s.getUserId());
        assertEquals(30, s.getDeckId());
        assertEquals(List.of(1, 2, 3), s.getCardIds());
        assertEquals(0, s.getCurrentCardIndex());
        assertFalse(s.isSessionComplete());
        assertEquals(0, s.getProgress());
    }

    @Test
    void constructor_rejectsEmptyCardList() {
        assertThrows(IllegalArgumentException.class, () -> new StudySession(1, 1, 1, List.of()));
    }

    @Test
    void constructor_rejectsNullCardList() {
        assertThrows(NullPointerException.class, () -> new StudySession(1, 1, 1, null));
    }

    @Test
    void constructor_makesDefensiveCopy() {
        List<Integer> ids = new ArrayList<>(List.of(1, 2));
        StudySession s = new StudySession(1, 1, 1, ids);
        ids.add(3);
        assertEquals(2, s.getCardIds().size());
    }

    @Test
    void cardIds_areUnmodifiable() {
        StudySession s = sessionOf(1, 2);
        assertThrows(UnsupportedOperationException.class, () -> s.getCardIds().add(5));
    }

    @Test
    void completeCurrentCard_advancesIndexAndProgress() {
        StudySession s = sessionOf(1, 2, 3, 4);
        s.completeCurrentCard();
        assertEquals(1, s.getCurrentCardIndex());
        assertEquals(25, s.getProgress());
        s.completeCurrentCard();
        assertEquals(50, s.getProgress());
    }

    @Test
    void progress_roundsDown() {
        StudySession s = sessionOf(1, 2, 3);
        s.completeCurrentCard();
        assertEquals(33, s.getProgress());
    }

    @Test
    void completingAllCards_marksSessionCompleteAt100Percent() {
        StudySession s = sessionOf(1, 2);
        s.completeCurrentCard();
        assertFalse(s.isSessionComplete());
        s.completeCurrentCard();
        assertTrue(s.isSessionComplete());
        assertEquals(100, s.getProgress());
    }

    @Test
    void completeCurrentCard_afterCompletion_throws() {
        StudySession s = sessionOf(1);
        s.completeCurrentCard();
        assertThrows(IllegalStateException.class, s::completeCurrentCard);
        assertEquals(1, s.getCurrentCardIndex());
    }
}
