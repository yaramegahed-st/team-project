package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_storesUsernameAndPassword() {
        User user = new User("alice", "pw");
        assertEquals("alice", user.getUsername());
        assertEquals("pw", user.getPassword());
    }

    @Test
    void constructor_rejectsEmptyUsername() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new User("", "pw"));
        assertEquals("Username cannot be empty", e.getMessage());
    }

    @Test
    void constructor_rejectsEmptyPassword() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new User("alice", ""));
        assertEquals("Password cannot be empty", e.getMessage());
    }

    @Test
    void userFactory_createsUserWithGivenCredentials() {
        User user = new UserFactory().create("bob", "secret");
        assertEquals("bob", user.getUsername());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void userFactory_propagatesValidationErrors() {
        UserFactory factory = new UserFactory();
        assertThrows(IllegalArgumentException.class, () -> factory.create("", "pw"));
        assertThrows(IllegalArgumentException.class, () -> factory.create("bob", ""));
    }
}
