package interface_adapter.logout;

import data_access.InMemoryUserDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginViewModel;
import org.junit.jupiter.api.Test;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputData;

import static org.junit.jupiter.api.Assertions.*;

class LogoutAdapterTest {

    @Test
    void presenter_clearsLoggedInUserAndShowsLoginView() {
        ViewManagerModel vmm = new ViewManagerModel();
        LoggedInViewModel loggedIn = new LoggedInViewModel();
        LoginViewModel login = new LoginViewModel();
        loggedIn.getState().setUsername("alice");
        int[] viewChanges = {0};
        vmm.addListener(() -> viewChanges[0]++);

        new LogoutPresenter(vmm, loggedIn, login).prepareSuccessView(new LogoutOutputData("alice"));

        assertEquals("", loggedIn.getState().getUsername());
        assertEquals("alice", login.getState().getUsername());
        assertEquals(LoginViewModel.VIEW_NAME, vmm.getCurrentViewName());
        assertEquals(1, viewChanges[0]);
    }

    @Test
    void controller_runsLogoutUseCase() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        dao.setCurrentUsername("alice");
        ViewManagerModel vmm = new ViewManagerModel();
        LogoutPresenter presenter = new LogoutPresenter(vmm, new LoggedInViewModel(), new LoginViewModel());

        new LogoutController(new LogoutInteractor(dao, presenter)).execute();

        assertNull(dao.getCurrentUsername());
        assertEquals(LoginViewModel.VIEW_NAME, vmm.getCurrentViewName());
    }
}
