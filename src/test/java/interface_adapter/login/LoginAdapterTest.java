package interface_adapter.login;

import data_access.InMemoryUserDataAccessObject;
import entity.User;
import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputData;

import static org.junit.jupiter.api.Assertions.*;

class LoginAdapterTest {

    private ViewManagerModel viewManager;
    private LoggedInViewModel loggedIn;
    private LoginViewModel loginVm;
    private LoginPresenter presenter;
    private int loginNotifications;
    private int loggedInNotifications;

    @BeforeEach
    void setUp() {
        viewManager = new ViewManagerModel();
        loggedIn = new LoggedInViewModel();
        loginVm = new LoginViewModel();
        presenter = new LoginPresenter(viewManager, loggedIn, loginVm);
        loginVm.addListener(() -> loginNotifications++);
        loggedIn.addListener(new interface_adapter.logged_in.LoggedInViewModelListener() {
            public void stateChanged() { loggedInNotifications++; }
            public void passwordChanged() { }
        });
    }

    @Test
    void presenter_success_updatesLoggedInStateClearsLoginFormAndNavigatesHome() {
        loginVm.getState().setUsername("alice");
        loginVm.getState().setPassword("pw");

        presenter.prepareSuccessView(new LoginOutputData("alice"));

        assertEquals("alice", loggedIn.getState().getUsername());
        assertEquals("", loginVm.getState().getUsername());
        assertEquals("", loginVm.getState().getPassword());
        assertNull(loginVm.getState().getLoginError());
        assertEquals(HomeViewModel.VIEW_NAME, viewManager.getCurrentViewName());
        assertTrue(viewManager.isForward());
        assertEquals(1, loggedInNotifications);
        assertEquals(1, loginNotifications);
    }

    @Test
    void presenter_failure_setsErrorAndNotifiesWithoutNavigating() {
        presenter.prepareFailView("Incorrect password");
        assertEquals("Incorrect password", loginVm.getState().getLoginError());
        assertEquals(1, loginNotifications);
        assertEquals("", viewManager.getCurrentViewName());
    }

    @Test
    void controller_passesCredentialsThroughToInteractor() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        dao.save(new User("alice", "pw"));
        LoginController controller = new LoginController(new LoginInteractor(dao, presenter));

        controller.execute("alice", "pw");

        assertEquals("alice", dao.getCurrentUsername());
        assertEquals(HomeViewModel.VIEW_NAME, viewManager.getCurrentViewName());
    }

    @Test
    void controller_wrongPasswordSurfacesErrorInViewModel() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        dao.save(new User("alice", "pw"));
        new LoginController(new LoginInteractor(dao, presenter)).execute("alice", "bad");
        assertEquals("Incorrect password for \"alice\".", loginVm.getState().getLoginError());
        assertNull(dao.getCurrentUsername());
    }

    @Test
    void viewModel_hasExpectedViewName() {
        assertEquals("log in", LoginViewModel.VIEW_NAME);
    }
}
