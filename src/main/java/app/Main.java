package app;

import javax.swing.*;

public class Main {
//    public static void main(String[] args) {
//        AppBuilder appBuilder = new AppBuilder();
//        JFrame application = appBuilder
//                .addLoginView()
//                .addSignupView()
//                .addLoggedInView()
//                .addHomeView()
//                .addAccountsView()
//                .addSignupUseCase()
//                .addLoginUseCase()
//                .addChangePasswordUseCase()
//                .addLogoutUseCase()
//                .build();
//
//        application.pack();
//        application.setLocationRelativeTo(null);
//        application.setVisible(true);
//    }
    public static void main(String[] args) {
        AppBuilder2 appBuilder2 = new AppBuilder2();
        JFrame application = appBuilder2
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