package cz.upce.fei.inptp.databasedependency.business;

import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserManagerServiceTest {

    private DAO<Person> personDaoMock;
    private UserManagerService service;

    @BeforeEach
    void setUp() {
        personDaoMock = Mockito.mock();
        service = new UserManagerService(personDaoMock);
    }

    @Test
    void testCreateUser() {
        Person p = service.createUser(1, "alice", "password");
        assertNotNull(p);
        assertEquals("alice", p.getName());
        verify(personDaoMock).save(any(Person.class));
    }

    @Test
    void testDeleteUser() {
        Person p = service.createUser(1, "bob", "x");
        when(personDaoMock.delete(any(Person.class))).thenReturn(true);
        assertTrue(service.deleteUser(p));
        verify(personDaoMock).delete(p);
    }

    @Test
    void testDeleteNullUser() {
        when(personDaoMock.delete(any(Person.class))).thenReturn(false);
        assertFalse(service.deleteUser(null));
        verify(personDaoMock, never()).delete(any(Person.class));
    }

    @Test
    void testChangePassword() {
        Person p = service.createUser(1, "carol", "oldpass");
        when(personDaoMock.save(any(Person.class))).thenReturn(true);
        assertTrue(service.changePassword(p, "newpass"));
        verify(personDaoMock, times(2)).save(p);
        assertNotEquals("oldpass", p.getPassword());
    }

    @Test
    void testChangePasswordOfNullUser() {
        when(personDaoMock.save(any(Person.class))).thenReturn(true);
        assertFalse(service.changePassword(null, "newpass"));
        verify(personDaoMock, never()).save(any(Person.class));
    }
}
