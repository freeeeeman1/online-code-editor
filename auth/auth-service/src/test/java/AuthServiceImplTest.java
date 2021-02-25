import com.netcracker.edu.auth.api.AuthEntry;
import com.netcracker.edu.auth.api.AuthResult;
import com.netcracker.edu.auth.service.AuthService;
import com.netcracker.edu.auth.service.AuthServiceImpl;
import com.netcracker.edu.project.user.User;
import com.netcracker.edu.auth.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static com.netcracker.edu.auth.api.AuthResult.Codes.INCORRECT_PASSWORD_DIGITS;
import static com.netcracker.edu.auth.api.AuthResult.Codes.SUCCESSFUL_EMAIL_SENDING;
import static com.netcracker.edu.auth.api.AuthResult.Codes.SUCCESSFUL_LOGIN;
import static com.netcracker.edu.auth.api.AuthResult.Codes.SUCCESSFUL_REGISTRATION;
import static com.netcracker.edu.auth.api.AuthResult.Codes.UNCONFIRMED_EMAIL;
import static com.netcracker.edu.auth.api.AuthResult.Codes.WRONG_PASSWORD;
import static com.netcracker.edu.auth.api.AuthResult.Codes.WRONG_SIGN_IN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceImplTest {

    private UserRepository userRepository;

    @Before
    public void setUp() {
        this.userRepository = mock(UserRepository.class);
    }

    @Test
    public void testSingUpIncorrectPassword() {
        String login = "userLogin";
        AuthEntry authEntry = new AuthEntry(login, "weakPassword", "project.nc.edu@gmail.com");

        AuthResult authResult = new AuthServiceImpl(userRepository).signUp(authEntry);

        assertEquals(new AuthResult(login, INCORRECT_PASSWORD_DIGITS), authResult);
    }

    @Test
    public void testSignUpIncorrectEmail() {
        String login = "userLogin";
        AuthEntry authEntry = new AuthEntry(login, "wr!oOpa78ord", "pro*ject.nc.edu@gmail.com");

        AuthResult actualResult = new AuthServiceImpl(mock(UserRepository.class)).signUp(authEntry);

        assertEquals(new AuthResult(login, AuthResult.Codes.MALFORMED_EMAIL), actualResult);
    }

    @Test
    public void testSignUpSuccessful() {
        String login = "userLogin";
        AuthEntry authEntry = new AuthEntry(login, "wr!oOpa78ord", "project.nc.edu@gmail.com");

        AuthResult actualResult = new AuthServiceImpl(userRepository).signUp(authEntry);

        assertEquals(new AuthResult(login, SUCCESSFUL_REGISTRATION), actualResult);
    }

    @Test
    public void testSignInUnconfirmedEmail() {
        String userLogin = "userLogin";
        String userEmail = "project.nc.edu@gmail.com";
        String userPassword = "wr!oOpa78ord";
        User user = new User(userLogin, userPassword, userEmail);

        when(userRepository.getByLogin(userLogin)).thenReturn(user);
        AuthService service = new AuthServiceImpl(userRepository);

        AuthResult authResult = service.signIn(new AuthEntry(userLogin, userPassword, userEmail));

        assertEquals(new AuthResult(userLogin, UNCONFIRMED_EMAIL), authResult);
    }

    @Test
    public void testSignInWrongPassword() {
        String userLogin = "userLogin";
        String userEmail = "project.nc.edu@gmail.com";
        String userPassword = "wr!oOpa78ord";
        User user = new User(userLogin, userPassword, userEmail);
        user.activate();

        when(userRepository.getByLogin(userLogin)).thenReturn(user);
        AuthService service = new AuthServiceImpl(userRepository);

        String wrongPassword = "123wr!oO456";
        AuthResult actualResult = service.signIn(new AuthEntry(userLogin, wrongPassword, userEmail));

        assertEquals(new AuthResult(userLogin, WRONG_PASSWORD), actualResult);
    }

    @Test
    public void testSignInSuccessful() {
        String userLogin = "userLogin";
        String userEmail = "project.nc.edu@gmail.com";
        String userPassword = "wr!oOpa78ord";
        User user = new User(userLogin, userPassword, userEmail);
        user.activate();

        when(userRepository.getByLogin(userLogin)).thenReturn(user);
        AuthService service = new AuthServiceImpl(userRepository);

        AuthResult authResult = service.signIn(new AuthEntry(userLogin, userPassword, userEmail));

        assertEquals(new AuthResult(userLogin, SUCCESSFUL_LOGIN), authResult);
    }

    @Test
    public void testSignInWrongLogin() {
        String userLogin = "userLogin";
        String wrongLogin = "user";
        String userEmail = "project.nc.edu@gmail.com";
        String userPassword = "wr!oOpa78ord";
        User user = new User(userLogin, userPassword, userEmail);
        user.activate();

        when(userRepository.getByLogin(userLogin)).thenReturn(user);
        AuthService service = new AuthServiceImpl(userRepository);

        AuthResult authResult = service.signIn(new AuthEntry(wrongLogin, userPassword, userEmail));

        assertEquals(new AuthResult(wrongLogin, WRONG_SIGN_IN), authResult);
    }

    @Test
    public void resetPassword() {
        String userLogin = "userLogin";
        String userEmail = "project.nc.edu@yandex.ru";
        String userPassword = "wr!oOpa78ord";

        User user = new User(userLogin, userPassword, userEmail);
        user.activate();

        when(userRepository.getByEmail(Mockito.any())).thenReturn(user);
        AuthService service = new AuthServiceImpl(userRepository);

        AuthResult authResult = service.resetPassword(userEmail);

        assertEquals(new AuthResult(userLogin, SUCCESSFUL_EMAIL_SENDING), authResult);
    }

    @Test()
    public void testUpdatePasswordUnsuccessful() {
        AuthService service = new AuthServiceImpl(userRepository);
        String userEmail = "project.nc.edu@gmail.com";
        String newPassword = "wr!ogL45";
        AuthResult expectedResult = new AuthResult(userEmail, "Password can not be updated", AuthResult.Codes.UNCONFIRMED_EMAIL);

        AuthResult authResult = service.updatePassword(userEmail, newPassword, "aaaaa");

        assertEquals(expectedResult, authResult);
    }
}
