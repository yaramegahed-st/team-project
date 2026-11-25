package data_access;

import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUserDataAccessObjectTest {

    @TempDir
    Path tempDir;

    private Path csv;
    private final UserFactory factory = new UserFactory();

    @BeforeEach
    void setUp() {
        csv = tempDir.resolve("users.csv");
    }

    @Test
    void newFile_isCreatedWithHeaderOnly() throws IOException {
        new FileUserDataAccessObject(csv.toString(), factory);
        List<String> lines = Files.readAllLines(csv);
        assertEquals(List.of("username,password"), lines);
    }

    @Test
    void save_persistsUserToDisk() throws IOException {
        FileUserDataAccessObject dao = new FileUserDataAccessObject(csv.toString(), factory);
        dao.save(new User("alice", "pw"));
        assertTrue(Files.readAllLines(csv).contains("alice,pw"));
        assertTrue(dao.existsByName("alice"));
        assertEquals("pw", dao.get("alice").getPassword());
    }

    @Test
    void existingFile_isLoadedByNewInstance() {
        FileUserDataAccessObject first = new FileUserDataAccessObject(csv.toString(), factory);
        first.save(new User("alice", "pw1"));
        first.save(new User("bob", "pw2"));

        FileUserDataAccessObject second = new FileUserDataAccessObject(csv.toString(), factory);
        assertTrue(second.existsByName("alice"));
        assertTrue(second.existsByName("bob"));
        assertEquals("pw2", second.get("bob").getPassword());
    }

    @Test
    void changePassword_isPersisted() {
        FileUserDataAccessObject dao = new FileUserDataAccessObject(csv.toString(), factory);
        dao.save(new User("alice", "old"));
        dao.changePassword(new User("alice", "new"));

        FileUserDataAccessObject reloaded = new FileUserDataAccessObject(csv.toString(), factory);
        assertEquals("new", reloaded.get("alice").getPassword());
    }

    @Test
    void unknownUser_doesNotExist() {
        FileUserDataAccessObject dao = new FileUserDataAccessObject(csv.toString(), factory);
        assertFalse(dao.existsByName("ghost"));
        assertNull(dao.get("ghost"));
    }

    @Test
    void wrongHeader_isRejected() throws IOException {
        Files.writeString(csv, "name,pass\nalice,pw\n");
        assertThrows(RuntimeException.class, () -> new FileUserDataAccessObject(csv.toString(), factory));
    }

    @Test
    void currentUsername_isInMemoryOnly() {
        FileUserDataAccessObject dao = new FileUserDataAccessObject(csv.toString(), factory);
        dao.setCurrentUsername("alice");
        assertEquals("alice", dao.getCurrentUsername());
        assertNull(new FileUserDataAccessObject(csv.toString(), factory).getCurrentUsername());
    }
}
