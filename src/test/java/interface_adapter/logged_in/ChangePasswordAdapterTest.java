package interface_adapter.logged_in;

import data_access.InMemoryUserDataAccessObject;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputData;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordAdapterTest {

    private LoggedInViewModel vm;
    private ChangePasswordPresenter presenter;
    private int stateChanged;
    private int passwordChanged;

    @BeforeEach
    void setUp() {
        vm = new LoggedInViewModel();
        presenter = new ChangePasswordPresenter(vm);
        vm.addListener(new LoggedInViewModelListener() {
            public void stateChanged() { stateChanged++; }
            public void passwordChanged() { passwordChanged++; }
        });
    }

    @Test
    void presenter_success_clearsPasswordAndError() {
        vm.getState().setPassword("typed");
        vm.getState().setPasswordError("old error");

        presenter.prepareSuccessView(new ChangePasswordOutputData("alice"));

        assertEquals("", vm.getState().getPassword());
        assertNull(vm.getState().getPasswordError());
        assertEquals(1, passwordChanged);
        assertEquals(0, stateChanged);
    }

    @Test
    void presenter_failure_setsErrorAndNotifies() {
        presenter.prepareFailView("New password cannot be empty");
        assertEquals("New password cannot be empty", vm.getState().getPasswordError());
        assertEquals(1, passwordChanged);
    }

    @Test
    void loggedInState_copyConstructorCopiesAllFields() {
        LoggedInState s = new LoggedInState();
        s.setUsername("u");
        s.setPassword("p");
        s.setPasswordError("e");
        LoggedInState copy = new LoggedInState(s);
        assertEquals("u", copy.getUsername());
        assertEquals("p", copy.getPassword());
        assertEquals("e", copy.getPasswordError());
    }

    /**
     * The controller's signature is execute(password, username), so the user's new password must
     * end up stored for that username. ChangePasswordInputData's constructor takes (password,
     * username), but the controller passes (username, password), swapping the two.
     */
    @Test
    void controller_storesNewPasswordForTheGivenUsername() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        dao.save(new User("alice", "old"));
        ChangePasswordController controller =
                new ChangePasswordController(new ChangePasswordInteractor(dao, presenter, new UserFactory()));

        controller.execute("newpw", "alice");

        assertTrue(dao.existsByName("alice"), "alice should still exist");
        assertEquals("newpw", dao.get("alice").getPassword());
        assertFalse(dao.existsByName("newpw"), "a user named after the password must not be created");
    }
}
