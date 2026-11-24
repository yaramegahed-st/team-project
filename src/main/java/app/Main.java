package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
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
                .addTemporaryWiring()
                .addSignupUseCase()
                .addLoginUseCase()
                .addChangePasswordUseCase()
                .addLogoutUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }


}