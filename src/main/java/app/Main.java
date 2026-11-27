package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                // The Views
                .addAccountsView()
                .addSignupView()
                .addLoginView()
                .addLoggedInView()
                .addLoadingView()
                .addHomeView()
                .addSettingsView()
                .addProfileView()
                .addAboutView()
                .addChatbotView()
                .addFreeAccessView()
                .addNewDeckView()
                .addStudyView()
                .addAddDeckView()

                // Temporary wiring for testing
                .addTemporaryWiring()

                // The Use Cases
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .addEditDeckFeature()
                .addEditCardFeature()
                .addCardUseCase()
                .addAddDeckUseCase()
                

                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }


}