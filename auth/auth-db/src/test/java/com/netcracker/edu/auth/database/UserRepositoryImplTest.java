package com.netcracker.edu.auth.database;

import com.netcracker.edu.project.user.User;
import com.netcracker.edu.auth.repository.UserRepository;
import com.netcracker.edu.auth.repository.UserRepositoryImpl;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserRepositoryImplTest {

    private static Server server;
    private UserRepository userRepository;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
        DatabaseSchema.createUsersTable();
    }

    @Before
    public void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    public void itShouldAddUser() {
        User user = new User("login1", "password1", "email1");

        userRepository.add(user);

        User actualUser = userRepository.getByLogin("login1");
        assertEquals(user, actualUser);
    }

    @Test
    public void itShouldReturnUserByLogin() {
        User user = new User("login2", "password2", "email2");
        userRepository.add(user);

        User actualUser = userRepository.getByLogin("login2");

        assertEquals(user, actualUser);
    }

    @Test
    public void itShouldReturnUserByEmail() {
        User user = new User("login3", "password3", "email3");
        userRepository.add(user);

        User actualUser = userRepository.getByEmail("email3");

        assertEquals(user, actualUser);
    }

    @Test
    public void itShouldReturnTrue_IsContains() {
        User user = new User("login3", "password3", "email3");
        userRepository.add(user);

        assertTrue(userRepository.contains("email3"));
    }

    @Test
    public void itShouldReturnFalse_IsContains() {
        assertFalse(userRepository.contains("email3"));
    }

    @Test
    public void itShouldChangePassword() {
        User user = new User("login4", "password4", "email4");
        User updateUser = new User("login4", "newPassword4", "email4");
        userRepository.add(user);

        userRepository.updatePassword(updateUser);

        User actualUser = userRepository.getByEmail("email4");
        assertEquals(updateUser.getPassword(), actualUser.getPassword());
    }

    @Test
    public void itShouldRemoveUser() {
        User user = new User("login5", "password5", "email5");
        userRepository.add(user);

        userRepository.remove(user);

        User actualUser = userRepository.getByEmail("email5");
        assertNull(actualUser);
    }

    @After
    public void tearDown() {
        userRepository.clear();
    }

    @AfterClass
    public static void afterClass() {
        DatabaseSchema.deleteUsersTable();
        server.stop();
    }
}
