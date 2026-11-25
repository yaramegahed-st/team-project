package integration;

import data_access.InMemoryDeckRepository;
import data_access.InMemoryUserDataAccessObject;
import entity.Card;
import entity.Deck;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.add_card.AddCardController;
import interface_adapter.add_card.AddCardPresenter;
import interface_adapter.add_card.AddCardViewModel;
import interface_adapter.edit_card.EditCardController;
import interface_adapter.edit_card.EditCardPresenter;
import interface_adapter.edit_card.EditCardViewModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import org.junit.jupiter.api.Test;
import use_case.add_card.AddCardInteractor;
import use_case.edit_card.EditCardInteractor;
import use_case.login.LoginInteractor;
import use_case.logout.LogoutInteractor;
import use_case.signup.SignupInteractor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Wires real interactors, presenters and data access together, the way AppBuilder does. */
class WorkflowIntegrationTest {

    @Test
    void signup_logout_login_flow() {
        InMemoryUserDataAccessObject dao = new InMemoryUserDataAccessObject();
        ViewManagerModel vmm = new ViewManagerModel();
        LoggedInViewModel loggedIn = new LoggedInViewModel();
        LoginViewModel loginVm = new LoginViewModel();
        SignupViewModel signupVm = new SignupViewModel();

        SignupController signup = new SignupController(
                new SignupInteractor(dao, new SignupPresenter(vmm, signupVm, loginVm), new UserFactory()));
        LoginPresenter loginPresenter = new LoginPresenter(vmm, loggedIn, loginVm);
        LoginController login = new LoginController(new LoginInteractor(dao, loginPresenter));
        LogoutController logout = new LogoutController(
                new LogoutInteractor(dao, new LogoutPresenter(vmm, loggedIn, loginVm)));

        signup.execute("alice", "pw", "pw");
        assertTrue(dao.existsByName("alice"));

        login.execute("alice", "pw");
        assertEquals("alice", dao.getCurrentUsername());
        assertEquals("alice", loggedIn.getState().getUsername());
        assertEquals(HomeViewModel.VIEW_NAME, vmm.getCurrentViewName());

        logout.execute();
        assertNull(dao.getCurrentUsername());
        assertEquals("", loggedIn.getState().getUsername());
        assertEquals(LoginViewModel.VIEW_NAME, vmm.getCurrentViewName());

        login.execute("alice", "wrong");
        assertNull(dao.getCurrentUsername());
        assertNotNull(loginVm.getState().getLoginError());
    }

    @Test
    void addCard_thenEditCard_flow() {
        Deck deck = new Deck(1, "Geo", "alice");
        InMemoryDeckRepository repo = new InMemoryDeckRepository(List.of(deck));

        AddCardViewModel addVm = new AddCardViewModel();
        EditCardViewModel editVm = new EditCardViewModel();
        AddCardController add = new AddCardController(new AddCardInteractor(repo, new AddCardPresenter(addVm)));
        EditCardController edit = new EditCardController(new EditCardInteractor(repo, new EditCardPresenter(editVm)));

        add.execute(1, "Capital of Peru?", "Lima");
        assertEquals("", addVm.getError());
        assertEquals(1, deck.getCards().size());
        int cardId = addVm.getCardId();

        edit.execute(1, cardId, "Capital of Peru", "Lima, on the Pacific coast");
        assertEquals("", editVm.getError());
        Card stored = deck.getCards().get(0);
        assertEquals(cardId, stored.getId());
        assertEquals("Lima, on the Pacific coast", stored.getAnswer());
    }

    @Test
    void failedAddAndEdit_leaveDeckUntouchedAndReportErrors() {
        Deck deck = new Deck(1, "Geo", "alice");
        InMemoryDeckRepository repo = new InMemoryDeckRepository(List.of(deck));
        AddCardViewModel addVm = new AddCardViewModel();
        EditCardViewModel editVm = new EditCardViewModel();
        AddCardController add = new AddCardController(new AddCardInteractor(repo, new AddCardPresenter(addVm)));
        EditCardController edit = new EditCardController(new EditCardInteractor(repo, new EditCardPresenter(editVm)));

        add.execute(1, "", "A");
        assertEquals("Question cannot be blank.", addVm.getError());
        add.execute(2, "Q", "A");
        assertEquals("Deck does not exist.", addVm.getError());
        assertTrue(deck.getCards().isEmpty());

        edit.execute(1, 123, "Q", "A");
        assertEquals("Card does not exist in this deck", editVm.getError());
    }

    @Test
    void cardIdsStayUniqueAcrossDecks() {
        Deck d1 = new Deck(1, "A", "o");
        Deck d2 = new Deck(2, "B", "o");
        InMemoryDeckRepository repo = new InMemoryDeckRepository(List.of(d1, d2));
        AddCardViewModel vm = new AddCardViewModel();
        AddCardController add = new AddCardController(new AddCardInteractor(repo, new AddCardPresenter(vm)));

        add.execute(1, "q1", "a1");
        add.execute(2, "q2", "a2");

        assertEquals("", vm.getError());
        assertNotEquals(d1.getCards().get(0).getId(), d2.getCards().get(0).getId());
    }
}
