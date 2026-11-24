package use_case.login;

import data_access.InMemoryUserDataAccessObject;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    private InMemoryUserDataAccessObject dao;
    private FakePresenter presenter;
    private LoginInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new InMemoryUserDataAccessObject();
        dao.save(new User("alice", "pw123"));
        presenter = new FakePresenter();
        interactor = new LoginInteractor(dao, presenter);
    }

    @Test
    void correctCredentials_succeedAndSetCurrentUser() {
        interactor.execute(new LoginInputData("alice", "pw123"));
        assertNull(presenter.error);
        assertEquals("alice", presenter.success.getUsername());
        assertEquals("alice", dao.getCurrentUsername());
    }

    @Test
    void unknownUser_failsWithAccountMessage() {
        interactor.execute(new LoginInputData("ghost", "pw"));
        assertEquals("ghost: Account does not exist.", presenter.error);
        assertNull(presenter.success);
        assertNull(dao.getCurrentUsername());
    }

    @Test
    void wrongPassword_failsAndDoesNotSetCurrentUser() {
        interactor.execute(new LoginInputData("alice", "nope"));
        assertEquals("Incorrect password for \"alice\".", presenter.error);
        assertNull(presenter.success);
        assertNull(dao.getCurrentUsername());
    }

    @Test
    void passwordIsCaseSensitive() {
        interactor.execute(new LoginInputData("alice", "PW123"));
        assertNotNull(presenter.error);
        assertNull(presenter.success);
    }

    private static class FakePresenter implements LoginOutputBoundary {
        LoginOutputData success;
        String error;
        public void prepareSuccessView(LoginOutputData d) { success = d; }
        public void prepareFailView(String e) { error = e; }
    }
}
