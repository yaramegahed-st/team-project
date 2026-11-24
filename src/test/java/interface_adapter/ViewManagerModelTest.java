package interface_adapter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewManagerModelTest {

    @Test
    void defaults_emptyViewAndForward() {
        ViewManagerModel m = new ViewManagerModel();
        assertEquals("", m.getCurrentViewName());
        assertTrue(m.isForward());
    }

    @Test
    void setState_withName_keepsDirection() {
        ViewManagerModel m = new ViewManagerModel();
        m.setState("a", false);
        m.setState("b");
        assertEquals("b", m.getCurrentViewName());
        assertFalse(m.isForward());
    }

    @Test
    void navigate_setsStateAndNotifiesEveryListener() {
        ViewManagerModel m = new ViewManagerModel();
        int[] calls = {0, 0};
        m.addListener(() -> calls[0]++);
        m.addListener(() -> calls[1]++);

        m.navigate("home", false);

        assertEquals("home", m.getCurrentViewName());
        assertFalse(m.isForward());
        assertEquals(1, calls[0]);
        assertEquals(1, calls[1]);
    }

    @Test
    void setState_doesNotNotify() {
        ViewManagerModel m = new ViewManagerModel();
        int[] calls = {0};
        m.addListener(() -> calls[0]++);
        m.setState("x");
        assertEquals(0, calls[0]);
    }
}
