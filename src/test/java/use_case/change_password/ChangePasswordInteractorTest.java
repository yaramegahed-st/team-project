package use_case.change_password;

import data_access.InMemoryUserDataAccessObject;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordInteractorTest {

    private InMemoryUserDataAccessObject dao;
    private FakePresenter presenter;
    private ChangePasswordInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new InMemoryUserDataAccessObject();
        dao.save(new User("alice", "old"));
        presenter = new FakePresenter();
        interactor = new ChangePasswordInteractor(dao, presenter, new UserFactory());
    }

    // NOTE: ChangePasswordInputData's constructor order is (password, username).
    @Test
    void validChange_updatesStoredPasswordAndReportsSuccess() {
        interactor.execute(new ChangePasswordInputData("newpw", "alice"));
        assertNull(presenter.error);
        assertEquals("alice", presenter.success.getUsername());
        assertEquals("newpw", dao.get("alice").getPassword());
    }

    @Test
    void emptyPassword_isRejectedAndStoredPasswordUnchanged() {
        interactor.execute(new ChangePasswordInputData("", "alice"));
        assertEquals("New password cannot be empty", presenter.error);
        assertNull(presenter.success);
        assertEquals("old", dao.get("alice").getPassword());
    }

    private static class FakePresenter implements ChangePasswordOutputBoundary {
        ChangePasswordOutputData success;
        String error;
        public void prepareSuccessView(ChangePasswordOutputData d) { success = d; }
        public void prepareFailView(String e) { error = e; }
    }
}
