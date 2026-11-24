package use_case.signup;

import data_access.InMemoryUserDataAccessObject;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    private InMemoryUserDataAccessObject dao;
    private FakePresenter presenter;
    private SignupInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new InMemoryUserDataAccessObject();
        presenter = new FakePresenter();
        interactor = new SignupInteractor(dao, presenter, new UserFactory());
    }

    @Test
    void validSignup_savesUserAndReportsSuccess() {
        interactor.execute(new SignupInputData("alice", "pw", "pw"));
        assertNull(presenter.error);
        assertEquals("alice", presenter.success.getUsername());
        assertTrue(dao.existsByName("alice"));
        assertEquals("pw", dao.get("alice").getPassword());
    }

    @Test
    void existingUser_isRejectedAndNotOverwritten() {
        dao.save(new User("alice", "original"));
        interactor.execute(new SignupInputData("alice", "new", "new"));
        assertEquals("User already exists.", presenter.error);
        assertNull(presenter.success);
        assertEquals("original", dao.get("alice").getPassword());
    }

    @Test
    void mismatchedPasswords_areRejected() {
        interactor.execute(new SignupInputData("alice", "pw1", "pw2"));
        assertEquals("Passwords don't match.", presenter.error);
        assertFalse(dao.existsByName("alice"));
    }

    @Test
    void emptyPassword_isRejected() {
        interactor.execute(new SignupInputData("alice", "", ""));
        assertEquals("New password cannot be empty", presenter.error);
        assertFalse(dao.existsByName("alice"));
    }

    @Test
    void emptyUsername_isRejected() {
        interactor.execute(new SignupInputData("", "pw", "pw"));
        assertEquals("Username cannot be empty", presenter.error);
        assertNull(presenter.success);
    }

    @Test
    void switchToLoginView_delegatesToPresenter() {
        interactor.switchToLoginView();
        assertTrue(presenter.switched);
    }

    private static class FakePresenter implements SignupOutputBoundary {
        SignupOutputData success;
        String error;
        boolean switched;
        public void prepareSuccessView(SignupOutputData d) { success = d; }
        public void prepareFailView(String e) { error = e; }
        public void switchToLoginView() { switched = true; }
    }
}
