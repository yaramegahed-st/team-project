package interface_adapter.edit_card;

import org.junit.jupiter.api.Test;
import use_case.edit_card.EditCardInputData;
import use_case.edit_card.EditCardOutputData;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EditCardAdapterTest {

    @Test
    void controller_buildsInputDataAndCallsInteractor() {
        List<EditCardInputData> received = new ArrayList<>();
        new EditCardController(received::add).execute(1, 2, "Q", "A");
        assertEquals(1, received.size());
        assertEquals(1, received.get(0).getDeckId());
        assertEquals(2, received.get(0).getCardId());
        assertEquals("Q", received.get(0).getQuestion());
        assertEquals("A", received.get(0).getAnswer());
    }

    @Test
    void presenter_success_populatesViewModelAndFiresCardUpdated() {
        EditCardViewModel vm = new EditCardViewModel();
        List<PropertyChangeEvent> events = new ArrayList<>();
        vm.addPropertyChangeListener(events::add);

        new EditCardPresenter(vm).prepareSuccessView(new EditCardOutputData(1, 2, "Q", "A"));

        assertEquals(1, vm.getDeckId());
        assertEquals(2, vm.getCardId());
        assertEquals("Q", vm.getQuestion());
        assertEquals("A", vm.getAnswer());
        assertEquals("Card update success!", vm.getMessage());
        assertEquals("", vm.getError());
        assertEquals(1, events.size());
        assertEquals(EditCardViewModel.CARD_UPDATED, events.get(0).getPropertyName());
    }

    @Test
    void presenter_failure_setsErrorClearsMessageAndFiresError() {
        EditCardViewModel vm = new EditCardViewModel();
        List<PropertyChangeEvent> events = new ArrayList<>();
        EditCardPresenter p = new EditCardPresenter(vm);
        p.prepareSuccessView(new EditCardOutputData(1, 2, "Q", "A"));
        vm.addPropertyChangeListener(events::add);

        p.prepareFailView("Unable to update card");

        assertEquals("Unable to update card", vm.getError());
        assertEquals("", vm.getMessage());
        assertEquals(1, events.size());
        assertEquals(EditCardViewModel.ERROR, events.get(0).getPropertyName());
    }
}
