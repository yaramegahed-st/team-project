package app;

import data_access.FileUserDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import data_access.InMemoryDeckRepository;

import entity.UserFactory;

import interface_adapter.ViewManagerModel;
import interface_adapter.about.AboutViewModel;
import interface_adapter.accounts.AccountsViewModel;
import interface_adapter.add_deck.AddDeckViewModel;
import interface_adapter.add_deck.AddDeckPresenter;
import interface_adapter.add_deck.AddDeckController;
import interface_adapter.chatbot.ChatbotViewModel;
import interface_adapter.edit_card.EditCardController;
import interface_adapter.edit_card.EditCardPresenter;
import interface_adapter.edit_card.EditCardViewModel;
import interface_adapter.edit_deck.EditDeckViewModel;
import interface_adapter.edit_deck.EditDeckPresenter;
import interface_adapter.edit_deck.EditDeckController;
import interface_adapter.free_access_decks.FreeAccessViewModel;
import interface_adapter.home.HomeViewModel;
import interface_adapter.loading.LoadingViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.new_deck.NewDeckViewModel;
import interface_adapter.profile.ProfileViewModel;
import interface_adapter.settings.SettingsViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.study.StudyViewModel;

import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.edit_card.EditCardInputBoundary;
import use_case.edit_card.EditCardInteractor;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.add_deck.AddDeckInputBoundary;
import use_case.add_deck.AddDeckInteractor;
import use_case.add_deck.AddDeckOutputBoundary;
import use_case.edit_deck.EditDeckInputBoundary;
import use_case.edit_deck.EditDeckInteractor;
import use_case.edit_deck.EditDeckOutputBoundary;
import use_case.session.CurrentUserAccess;

import view.AboutPanel;
import view.AccountsView;
import view.ChatbotPanel;
import view.AddDeckView;
import view.EditCardView;
import view.EditDeckView;
import view.FreeAccessDecksPanel;
import view.LoadingPanel;
import view.LoggedInView;
import view.LoginView;
import view.MainPanel;
import view.NewDeckPanel;
import view.ProfilePanel;
import view.SettingsPanel;
import view.SignupView;
import view.SlidingViewManager;
import view.StudyPanel;
import view.TemporaryWiring;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppBuilder {
    private static final int W = 1000, H = 605;

    private final JPanel container = new JPanel(null);          // null layout: SlidingViewManager positions the views
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final SlidingViewManager viewManager = new SlidingViewManager(container, viewManagerModel, W, H);
    final NewDeckViewModel newDeckViewModel = new NewDeckViewModel();

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using local file storage
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginView loginView;
    private LoginViewModel loginViewModel;
    private LoggedInView loggedInView;
    private LoggedInViewModel loggedInViewModel;
    private AccountsView accountsView;
    private AddDeckView addDeckView;
    private AddDeckViewModel addDeckViewModel;
    private EditDeckView editDeckView;
    private EditDeckViewModel editDeckViewModel;
    private EditCardView editCardView;
    private EditCardViewModel editCardViewModel;

    private LoadingPanel loadingPanel;
    private MainPanel mainPanel;
    private FreeAccessDecksPanel freeAccessPanel;
    private NewDeckPanel newDeckPanel;
    private ChatbotPanel chatbotPanel;
    private StudyPanel studyPanel;

    private final InMemoryDeckRepository deckRepository = new InMemoryDeckRepository(List.of());

    public AppBuilder() {
        container.setPreferredSize(new Dimension(W, H));
        container.setBackground(Color.decode("#004369"));
    }

    // ----- existing views -----
    public AppBuilder addAccountsView() {
        accountsView = new AccountsView(viewManagerModel);
        viewManager.register(AccountsViewModel.VIEW_NAME, accountsView);
        return this;
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel, viewManagerModel);
        viewManager.register(SignupViewModel.VIEW_NAME, signupView);
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel);
        viewManager.register(LoginViewModel.VIEW_NAME, loginView);
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        viewManager.register(LoggedInViewModel.VIEW_NAME, loggedInView);
        return this;
    }

    // ----- new views (the FlashShare screens) -----
    public AppBuilder addLoadingView() {
        loadingPanel = new LoadingPanel(W);
        viewManager.register(LoadingViewModel.VIEW_NAME, loadingPanel);
        return this;
    }

    /** MainPanel is the home screen: it replaces the old HomeView. */
    public AppBuilder addHomeView() {
        mainPanel = new MainPanel(W, viewManagerModel, newDeckViewModel);
        viewManager.register(HomeViewModel.VIEW_NAME, mainPanel);
        return this;
    }

    public AppBuilder addSettingsView() {
        viewManager.register(SettingsViewModel.VIEW_NAME, new SettingsPanel(W, viewManagerModel));
        return this;
    }

    public AppBuilder addProfileView() {
        viewManager.register(ProfileViewModel.VIEW_NAME, new ProfilePanel(W, viewManagerModel));
        return this;
    }

    public AppBuilder addAboutView() {
        viewManager.register(AboutViewModel.VIEW_NAME, new AboutPanel(W, viewManagerModel));
        return this;
    }

    public AppBuilder addChatbotView() {
        chatbotPanel = new ChatbotPanel(W, viewManagerModel);
        viewManager.register(ChatbotViewModel.VIEW_NAME, chatbotPanel);
        return this;
    }

    public AppBuilder addFreeAccessView() {
        freeAccessPanel = new FreeAccessDecksPanel(W, viewManagerModel);
        viewManager.register(FreeAccessViewModel.VIEW_NAME, freeAccessPanel);
        return this;
    }

    public AppBuilder addNewDeckView() {
        newDeckPanel = new NewDeckPanel(W, viewManagerModel, newDeckViewModel);
        viewManager.register(NewDeckViewModel.VIEW_NAME, newDeckPanel);
        return this;
    }

    public AppBuilder addStudyView() {
        studyPanel = new StudyPanel(W, viewManagerModel);
        viewManager.register(StudyViewModel.VIEW_NAME, studyPanel);
        return this;
    }

    public AppBuilder addAddDeckView() {
        addDeckViewModel = new AddDeckViewModel();
        addDeckView = new AddDeckView(addDeckViewModel);

        viewManager.register(AddDeckViewModel.VIEW_NAME, addDeckView);
        return this;
    }

    public AppBuilder addEditCardFeature() {
        editCardViewModel = new EditCardViewModel();

        EditCardPresenter presenter =
            new EditCardPresenter(editCardViewModel);

        EditCardInputBoundary interactor =
            new EditCardInteractor(deckRepository, presenter);

        EditCardController controller =
            new EditCardController(interactor);

        editCardView = new EditCardView(
            controller,
            editCardViewModel
        );

        viewManager.register(
            EditCardViewModel.VIEW_NAME,
            editCardView
        );

        return this;
    }

    /** TEMPORARY: connects features that are not use cases yet. Remove as each one becomes a use case. */
    public AppBuilder addTemporaryWiring() {
        new TemporaryWiring(W, viewManagerModel, viewManager, newDeckViewModel,
                mainPanel, freeAccessPanel, newDeckPanel, chatbotPanel, studyPanel).install();
        return this;
    }

    // ----- existing use cases -----
    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        return this;
    }

    public AppBuilder addAddDeckUseCase() {
        if (addDeckView == null) {
            throw new IllegalStateException(
                    "Call addAddDeckView() before addAddDeckUseCase()."
            );
        }

        AddDeckOutputBoundary presenter =
                new AddDeckPresenter(addDeckViewModel);

        CurrentUserAccess currentUserAccess =
                userDataAccessObject::getCurrentUsername;

        AddDeckInputBoundary interactor = new AddDeckInteractor(
                deckRepository,
                presenter,
                currentUserAccess
        );

        addDeckView.setAddDeckController(
                new AddDeckController(interactor)
        );

        return this;
    }

    public AppBuilder addEditDeckFeature() {
        EditDeckViewModel viewModel = new EditDeckViewModel();

        EditDeckOutputBoundary presenter =
                new EditDeckPresenter(viewModel);

        CurrentUserAccess session =
                userDataAccessObject::getCurrentUsername;

        EditDeckInputBoundary interactor = new EditDeckInteractor(
                deckRepository,
                presenter,
                session
        );

        EditDeckController controller =
                new EditDeckController(interactor);

        editDeckView = new EditDeckView(controller, viewModel);

        viewManager.register(
                EditDeckViewModel.VIEW_NAME,
                editDeckView
        );

        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        loggedInView.setLogoutController(logoutController);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("FlashShare");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setResizable(false);
        application.add(container);

        // first screen: the loading animation, then the Accounts screen (sign up / log in)
        viewManagerModel.navigate(LoadingViewModel.VIEW_NAME, true);
        SwingUtilities.invokeLater(() -> {
            loadingPanel.startAnimation();
            javax.swing.Timer toHome = new javax.swing.Timer(1500,
                    e -> viewManagerModel.navigate(AccountsViewModel.VIEW_NAME, true));
            toHome.setRepeats(false);
            toHome.start();
        });

        return application;
    }
}
