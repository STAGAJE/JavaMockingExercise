package cz.upce.fei.inptp.databasedependency.business;

import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;
import cz.upce.fei.inptp.databasedependency.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserRoleManagerServiceTest {

    private DAO<PersonRole> personRoleDao;
    private UserRoleManagerService service;
    private Person testPerson;

    @BeforeEach
    public void setUp() {
        personRoleDao = Mockito.mock();
        service = new UserRoleManagerService(personRoleDao);
        testPerson = new Person(1, "john", "hashedpwd");
    }

    @Test
    public void testAssignRole_NullUser() {
        Role newRole = new Role("/section", "rw", "sys");

        assertFalse(service.assignOrAppendRole(null, newRole));
        verify(personRoleDao, never()).save(any(PersonRole.class));
    }

    @Test
    public void testAssignRole_NewUser() {
        Role newRole = new Role("/section", "rw", "sys");

        when(personRoleDao.load("id = 1")).thenReturn(null);
        when(personRoleDao.save(any(PersonRole.class))).thenReturn(true);

        assertTrue(service.assignOrAppendRole(testPerson, newRole));
        verify(personRoleDao).save(any(PersonRole.class));
    }

    @Test
    public void testAssignRole_ExistingRoles() {
        Role existingRole = new Role("/section", "ro", "sys");
        Role newRole = new Role("/section/sub", "rw", "sys");

        PersonRole existing = new PersonRole(testPerson, new ArrayList<>(Collections.singletonList(existingRole)));
        when(personRoleDao.load("id = 1")).thenReturn(existing);
        when(personRoleDao.save(any(PersonRole.class))).thenReturn(true);

        assertTrue(service.assignOrAppendRole(testPerson, newRole));
        assertEquals(2, existing.getRoles().size());
        verify(personRoleDao).save(existing);
    }

    @Test
    public void testRemoveRole_NullUser() {
        Role role = new Role("/section", "rw", "sys");

        assertFalse(service.removeRoleIfExists(null, role));
        verify(personRoleDao, never()).save(any(PersonRole.class));
    }

    @Test
    public void testRemoveRole_Success() {
        Role role = new Role("/section", "rw", "sys");
        List<Role> roles = new ArrayList<>(Collections.singletonList(role));
        PersonRole existing = new PersonRole(testPerson, roles);

        when(personRoleDao.load("id = 1")).thenReturn(existing);
        when(personRoleDao.save(any(PersonRole.class))).thenReturn(true);

        assertTrue(service.removeRoleIfExists(testPerson, role));
        assertTrue(existing.getRoles().isEmpty());
        verify(personRoleDao).save(existing);
    }

    @Test
    public void testRemoveRole_NotFound() {
        Role role = new Role("/section", "rw", "sys");
        PersonRole existing = new PersonRole(testPerson, new ArrayList<Role>());

        when(personRoleDao.load("id = 1")).thenReturn(existing);

        assertFalse(service.removeRoleIfExists(testPerson, role));
        verify(personRoleDao, never()).save(any(PersonRole.class));
    }

    @Test
    public void testGetRoles() {
        Role r1 = new Role("/a", "ro", "x");
        Role r2 = new Role("/b", "rw", "x");
        PersonRole pr = new PersonRole(testPerson, Arrays.asList(r1, r2));

        when(personRoleDao.load("id = 1")).thenReturn(pr);

        PersonRole res = service.getRoles(testPerson);

        assertEquals(2, res.getRoles().size());
        assertEquals("rw", res.getRoles().get(1).getAccess());
        assertEquals(pr, res);
        verify(personRoleDao).load("id = 1");
    }

    @Test
    public void testGetRoles_Empty() {
        when(personRoleDao.load("id = 1")).thenReturn(null);
        PersonRole res = service.getRoles(testPerson);
        assertNull(res);
        verify(personRoleDao).load("id = 1");
    }

    @Test
    public void testGetRoles_NullUser() {
        PersonRole res = service.getRoles(null);
        assertNull(res);
        verify(personRoleDao, never()).load(anyString());
    }

    @Test
    public void testClearRoles() {
        Role r1 = new Role("/a", "ro", "x");
        PersonRole pr = new PersonRole(testPerson, new ArrayList<>(Collections.singletonList(r1)));

        when(personRoleDao.load("id = 1")).thenReturn(pr);
        when(personRoleDao.delete(any(PersonRole.class))).thenReturn(true);

        assertTrue(service.clearRoles(testPerson));
        assertTrue(pr.getRoles().isEmpty());
        verify(personRoleDao).delete(pr);
    }

    @Test
    public void testClearRoles_NoExisting() {
        when(personRoleDao.load("id = 1")).thenReturn(null);
        assertFalse(service.clearRoles(testPerson));
        verify(personRoleDao, never()).delete(any(PersonRole.class));
    }

    @Test
    public void testClearRoles_NullUser() {
        assertFalse(service.clearRoles(null));
        verify(personRoleDao, never()).delete(any(PersonRole.class));
    }
}
