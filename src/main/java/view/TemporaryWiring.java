package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.folder.FolderViewModel;
import interface_adapter.new_deck.NewDeckViewModel;
import interface_adapter.study.StudyViewModel;
import view.components.TileButton;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TEMPORARY. This does what MyFrame used to do for features that are not use cases yet.
 * Delete each part as its use case (add_deck, add_folder, explore_decks, start_study,
 * ask_assistant) is implemented, with a controller called from the view instead.
 */
public class TemporaryWiring {
    private final int width;
    private final ViewManagerModel viewManagerModel;
    private final SlidingViewManager viewManager;
    private final NewDeckViewModel newDeckViewModel;
    private final MainPanel mainPanel;
    private final FreeAccessDecksPanel freeAccessPanel;
    private final NewDeckPanel newDeckPanel;
    private final ChatbotPanel chatbotPanel;
    private final StudyPanel studyPanel;

    private final Map<TileButton, String> folderViewNames = new HashMap<>();
    private final Map<String, FolderPanel> folderPanels = new HashMap<>();
    private int nextFolderId = 1;

    public TemporaryWiring(int width, ViewManagerModel viewManagerModel, SlidingViewManager viewManager,
                           NewDeckViewModel newDeckViewModel, MainPanel mainPanel,
                           FreeAccessDecksPanel freeAccessPanel, NewDeckPanel newDeckPanel,
                           ChatbotPanel chatbotPanel, StudyPanel studyPanel) {
        this.width = width;
        this.viewManagerModel = viewManagerModel;
        this.viewManager = viewManager;
        this.newDeckViewModel = newDeckViewModel;
        this.mainPanel = mainPanel;
        this.freeAccessPanel = freeAccessPanel;
        this.newDeckPanel = newDeckPanel;
        this.chatbotPanel = chatbotPanel;
        this.studyPanel = studyPanel;
    }

    public void install() {
        // home menu
        mainPanel.setOnMenuClicked(name -> {
            if (name.equals("Study Mode")) startStudyFlow();
            else System.out.println(name);                       // Import Deck / Export Deck for now
        });

        // folders
        mainPanel.setOnFolderOpened(this::openFolder);
        mainPanel.setOnNewFolderClicked(() -> {
            String name = JOptionPane.showInputDialog(mainPanel, "Name your new folder:", "New folder");
            if (name == null || name.trim().isEmpty()) return;      // cancelled: stay on the home screen
            openFolder(mainPanel.addFolder(name.trim()));
        });

        // new deck: add it where the user came from, and to the free page if public
        newDeckPanel.setOnDeckCreated(info -> {
            String origin = newDeckViewModel.getState().getOriginView();
            FolderPanel folder = folderPanels.get(origin);
            if (folder != null) folder.addDeck(info);
            else mainPanel.addDeck(info);

            if (info.isPublic) freeAccessPanel.addDeck(info.name);

            viewManagerModel.navigate(origin, false);
        });
        newDeckPanel.setOnAccessDeck(code ->
                System.out.println("Access deck with permission: " + code));   // replace once decks have data behind them

        // assistant
        chatbotPanel.setOnPromptSubmitted(text -> System.out.println("Prompt: " + text));
    }

    // one FolderPanel per folder tile, created on first open and registered under its own view name
    private void openFolder(TileButton tile) {
        String viewName = folderViewNames.computeIfAbsent(tile, t -> {
            String name = FolderViewModel.viewNameFor(nextFolderId++);
            FolderPanel panel = new FolderPanel(width, viewManagerModel, newDeckViewModel, name);
            viewManager.register(name, panel);
            folderPanels.put(name, panel);
            return name;
        });
        viewManagerModel.navigate(viewName, true);
    }

    // ----- study flow -----
    private void startStudyFlow() {
        List<String> decks = mainPanel.getDeckNames();
        if (decks.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Create a deck first.");
            return;
        }

        // step 1: pick a deck
        Object picked = JOptionPane.showInputDialog(mainPanel, "Choose a deck to study:", "Study Mode",
                JOptionPane.QUESTION_MESSAGE, null, decks.toArray(), decks.get(0));
        if (picked == null) return;
        String deck = picked.toString();

        int total = cardCountFor(deck);
        if (total <= 0) {
            JOptionPane.showMessageDialog(mainPanel, "\"" + deck + "\" has no cards yet.");
            return;
        }

        // step 2: standard mode or card select
        String[] modes = {"Standard", "Card select", "Cancel"};
        int mode = JOptionPane.showOptionDialog(mainPanel,
                "How do you want to study \"" + deck + "\"?", "Study mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
        if (mode == JOptionPane.CLOSED_OPTION || mode == 2) return;

        // step 3: card select asks which cards
        List<Integer> positions;
        if (mode == 0) {
            positions = new java.util.ArrayList<>();
            for (int i = 1; i <= total; i++) positions.add(i);       // standard: every card
        } else {
            positions = askCardPositions(total);
            if (positions == null) return;
        }

        // step 4: go to the study view
        studyPanel.startSession(deck, modes[mode], positions);
        viewManagerModel.navigate(StudyViewModel.VIEW_NAME, true);
    }

    private List<Integer> askCardPositions(int total) {
        String prompt = "Enter card positions (1-" + total + "), e.g. 1, 3, 5-8:";
        String input = "";
        while (true) {
            input = JOptionPane.showInputDialog(mainPanel, prompt, input);
            if (input == null) return null;
            List<Integer> parsed = parsePositions(input, total);
            if (parsed != null) return parsed;
            JOptionPane.showMessageDialog(mainPanel,
                    "Please enter numbers from 1 to " + total + ", like 1, 3, 5-8.",
                    "Invalid selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** "1, 3, 5-8" -> [1, 3, 5, 6, 7, 8]; null if anything is invalid or out of range. */
    static List<Integer> parsePositions(String input, int total) {
        java.util.Set<Integer> result = new java.util.LinkedHashSet<>();
        try {
            for (String part : input.split(",")) {
                part = part.trim();
                if (part.isEmpty()) continue;
                if (part.contains("-")) {
                    String[] r = part.split("-");
                    if (r.length != 2) return null;
                    int a = Integer.parseInt(r[0].trim());
                    int b = Integer.parseInt(r[1].trim());
                    if (a > b) { int t = a; a = b; b = t; }
                    if (a < 1 || b > total) return null;
                    for (int i = a; i <= b; i++) result.add(i);
                } else {
                    int n = Integer.parseInt(part);
                    if (n < 1 || n > total) return null;
                    result.add(n);
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return result.isEmpty() ? null : new java.util.ArrayList<>(result);
    }

    private int cardCountFor(String deckName) {
        return 20;   // placeholder: replace with the real number of cards in that deck
    }
}
