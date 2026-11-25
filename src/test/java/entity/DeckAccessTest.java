package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckAccessTest {

    @Test
    void constructor_storesIdsAndRole() {
        AccessRole role = new AccessRole();
        DeckAccess access = new DeckAccess(1, 2, role);
        assertEquals(1, access.getDeckId());
        assertEquals(2, access.getUserId());
        assertSame(role, access.getRole());
    }

    @Test
    void constructor_requiresRole() {
        assertThrows(NullPointerException.class, () -> new DeckAccess(1, 2, null));
    }

    @Test
    void changeRole_replacesRole() {
        DeckAccess access = new DeckAccess(1, 2, new AccessRole());
        AccessRole other = new AccessRole();
        access.changeRole(other);
        assertSame(other, access.getRole());
    }

    @Test
    void changeRole_requiresRole() {
        DeckAccess access = new DeckAccess(1, 2, new AccessRole());
        assertThrows(NullPointerException.class, () -> access.changeRole(null));
    }
}
