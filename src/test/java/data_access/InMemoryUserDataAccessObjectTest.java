package data_access;

import entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserDataAccessObjectTest {

    @Test
    void saveThenGet_roundTrips() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        User u = new User("alice", "pw");
        dao.save(u);
        assertTrue(dao.existsByName("alice"));
        assertSame(u, dao.get("alice"));
    }

    @Test
    void unknownUser_doesNotExistAndGetReturnsNull() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        assertFalse(dao.existsByName("nobody"));
        assertNull(dao.get("nobody"));
    }

    @Test
    void changePassword_replacesUser() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        dao.save(new User("alice", "old"));
        dao.changePassword(new User("alice", "new"));
        assertEquals("new", dao.get("alice").getPassword());
    }

    @Test
    void currentUsername_canBeSetAndCleared() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        assertNull(dao.getCurrentUsername());
        dao.setCurrentUsername("alice");
        assertEquals("alice", dao.getCurrentUsername());
        dao.setCurrentUsername(null);
        assertNull(dao.getCurrentUsername());
    }
}
