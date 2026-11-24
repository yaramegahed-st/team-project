package use_case.logout;

import data_access.InMemoryUserDataAccessObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutInteractorTest {

    @Test
    void logout_clearsCurrentUserAndReportsWhoLoggedOut() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        dao.setCurrentUsername("alice");
        final LogoutOutputData[] captured = new LogoutOutputData[1];

        new LogoutInteractor(dao, d -> captured[0] = d).execute();

        assertNull(dao.getCurrentUsername());
        assertNotNull(captured[0]);
        assertEquals("alice", captured[0].getUsername());
    }

    @Test
    void logout_whenNobodyLoggedIn_stillSucceedsWithNullUsername() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        final LogoutOutputData[] captured = new LogoutOutputData[1];

        new LogoutInteractor(dao, d -> captured[0] = d).execute();

        assertNotNull(captured[0]);
        assertNull(captured[0].getUsername());
    }
}
