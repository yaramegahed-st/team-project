package interface_adapter.add_card;

import org.junit.jupiter.api.Test;
import use_case.add_card.AddCardInputData;
import use_case.add_card.AddCardOutputData;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddCardAdapterTest {

    @Test
    void controller_buildsInputDataAndCallsInteractor() {
        List<AddCardInputData> received = new ArrayList<>();
        new AddCardController(received::add).execute(3, "Q", "A");
        assertEquals(1, received.size());
        assertEquals(3, received.get(0).getDeckId());
        assertEquals("Q", received.get(0).getQuestion());
        assertEquals("A", received.get(0).getAnswer());
    }

    @Test
    void presenter_success_populatesViewModel() {
        AddCardViewModel vm = new AddCardViewModel();
        new AddCardPresenter(vm).prepareSuccessView(new AddCardOutputData(8, 2, "Q", "A"));
        assertEquals(8, vm.getCardId());
        assertEquals(2, vm.getDeckId());
        assertEquals("Q", vm.getQuestion());
        assertEquals("A", vm.getAnswer());
        assertEquals("Card added success", vm.getMessage());
        assertEquals("", vm.getError());
    }

    @Test
    void presenter_failure_setsErrorAndClearsMessage() {
        AddCardViewModel vm = new AddCardViewModel();
        AddCardPresenter p = new AddCardPresenter(vm);
        p.prepareSuccessView(new AddCardOutputData(1, 1, "Q", "A"));
        p.prepareFailView("Deck does not exist.");
        assertEquals("Deck does not exist.", vm.getError());
        assertEquals("", vm.getMessage());
    }

    @Test
    void viewModel_notifiesListenersOnSuccessAndError() {
        AddCardViewModel vm = new AddCardViewModel();
        List<PropertyChangeEvent> events = new ArrayList<>();
        vm.addPropertyChangeListener(events::add);

        vm.setSuccess(1, 1, "Q", "A", "ok");
        vm.setError("bad");

        assertEquals(2, events.size());
        assertEquals(AddCardViewModel.ERROR, events.get(1).getPropertyName());
    }

    @Test
    void viewModel_removedListenerIsNotNotified() {
        AddCardViewModel vm = new AddCardViewModel();
        List<PropertyChangeEvent> events = new ArrayList<>();
        java.beans.PropertyChangeListener l = events::add;
        vm.addPropertyChangeListener(l);
        vm.removePropertyChangeListener(l);
        vm.setError("x");
        assertTrue(events.isEmpty());
    }

    /**
     * AddCardViewModel declares CARD_ADDED but fires "success cardAdded" on success, so any view
     * that listens for CARD_ADDED (as EditCardView does for CARD_UPDATED) would never be notified.
     */
    @Test
    void viewModel_successFiresTheDeclaredCardAddedEvent() {
        AddCardViewModel vm = new AddCardViewModel();
        List<String> names = new ArrayList<>();
        vm.addPropertyChangeListener(e -> names.add(e.getPropertyName()));
        vm.setSuccess(1, 1, "Q", "A", "ok");
        assertEquals(List.of(AddCardViewModel.CARD_ADDED), names);
    }
}
