package interface_adapter.signup;

import data_access.InMemoryUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.login.LoginViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputData;

import static org.junit.jupiter.api.Assertions.*;

class SignupAdapterTest {

    private ViewManagerModel viewManager;
    private SignupViewModel signupVm;
    private LoginViewModel loginVm;
    private SignupPresenter presenter;
    private int viewChanges;

    @BeforeEach
    void setUp() {
        viewManager = new ViewManagerModel();
        signupVm = new SignupViewModel();
        loginVm = new LoginViewModel();
        presenter = new SignupPresenter(viewManager, signupVm, loginVm);
        viewManager.addListener(() -> viewChanges++);
    }

    @Test
    void presenter_success_prefillsLoginUsernameAndNavigatesHome() {
        presenter.prepareSuccessView(new SignupOutputData("alice"));
        assertEquals("alice", loginVm.getState().getUsername());
        assertEquals(HomeViewModel.VIEW_NAME, viewManager.getCurrentViewName());
        assertEquals(1, viewChanges);
    }

    @Test
    void presenter_failure_setsUsernameError() {
        presenter.prepareFailView("User already exists.");
        assertEquals("User already exists.", signupVm.getState().getUsernameError());
        assertEquals("", viewManager.getCurrentViewName());
    }

    @Test
    void presenter_switchToLogin_changesView() {
        presenter.switchToLoginView();
        assertEquals(LoginViewModel.VIEW_NAME, viewManager.getCurrentViewName());
        assertEquals(1, viewChanges);
    }

    @Test
    void controller_executeRunsSignupUseCase() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        SignupController c = new SignupController(new SignupInteractor(dao, presenter, new UserFactory()));
        c.execute("alice", "pw", "pw");
        assertTrue(dao.existsByName("alice"));
        assertEquals(HomeViewModel.VIEW_NAME, viewManager.getCurrentViewName());
    }

    @Test
    void controller_mismatchedPasswordsSurfaceError() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        SignupController c = new SignupController(new SignupInteractor(dao, presenter, new UserFactory()));
        c.execute("alice", "pw", "other");
        assertEquals("Passwords don't match.", signupVm.getState().getUsernameError());
        assertFalse(dao.existsByName("alice"));
    }

    @Test
    void controller_switchToLoginView_isForwarded() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        SignupController c = new SignupController(new SignupInteractor(dao, presenter, new UserFactory()));
        c.switchToLoginView();
        assertEquals(LoginViewModel.VIEW_NAME, viewManager.getCurrentViewName());
    }
}
