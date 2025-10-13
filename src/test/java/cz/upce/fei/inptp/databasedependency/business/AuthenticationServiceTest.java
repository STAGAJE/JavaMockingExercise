package cz.upce.fei.inptp.databasedependency.business;

import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private DAO<Person> personDao;
    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        personDao = Mockito.mock();
        authService = new AuthenticationService(personDao);
    }

    @Test
    void authenticate_shouldReturnTrue_whenCredentialsMatch() {
        String hashed = AuthenticationService.encryptPassword("secret");
        Person person = new Person(1, "user", hashed);
        when(personDao.load("name = 'user'")).thenReturn(person);

        assertTrue(authService.authenticate("user", "secret"));
    }

    @Test
    void authenticate_shouldReturnFalse_whenPasswordInvalid() {
        String hashed = AuthenticationService.encryptPassword("correct");
        Person person = new Person(1, "user", hashed);
        when(personDao.load("name = 'user'")).thenReturn(person);

        assertFalse(authService.authenticate("user", "wrong"));
    }

    @Test
    void authenticate_shouldReturnFalse_whenUserNotFound() {
        when(personDao.load("name = 'user'")).thenReturn(null);

        assertFalse(authService.authenticate("user", "anything"));
    }
}
