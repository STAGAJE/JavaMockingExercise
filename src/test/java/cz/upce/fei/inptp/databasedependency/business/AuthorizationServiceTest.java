package cz.upce.fei.inptp.databasedependency.business;

import cz.upce.fei.inptp.databasedependency.dao.PersonDAO;
import cz.upce.fei.inptp.databasedependency.dao.PersonRolesDAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;
import cz.upce.fei.inptp.databasedependency.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    private PersonDAO personDaoMock;
    private PersonRolesDAO personRolesDaoMock;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        personDaoMock = mock(PersonDAO.class);
        personRolesDaoMock = mock(PersonRolesDAO.class);
        authorizationService = new AuthorizationService(personDaoMock, personRolesDaoMock);
    }

    // Helper to mock DAO responses for a given person.
    private void mockPersonRoles(Person person, List<Role> roles) {
        when(personDaoMock.getRoleWhereStringFor(person)).thenReturn("WHERE person_id=" + person.getId());
        when(personRolesDaoMock.load("WHERE person_id=" + person.getId()))
                .thenReturn(new PersonRole(person, roles));
    }

    // ✅ 1. Exact section, rw -> rw -> PASS
    @Test
    void authorize_shouldPass_whenExactSectionAndSameAccess() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/section/subsection", "rw", "m1")));

        assertTrue(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 2. Exact section, rw requested but only ro -> FAIL
    @Test
    void authorize_shouldFail_whenExactSectionAndInsufficientAccess() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/section/subsection", "ro", "m1")));

        assertFalse(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 3. Parent section grants same rw -> PASS
    @Test
    void authorize_shouldPass_whenParentSectionHasRWAccess() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/section", "rw", "m1")));

        assertTrue(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 4. Parent section only ro -> FAIL
    @Test
    void authorize_shouldFail_whenParentSectionHasROAccess() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/section", "ro", "m1")));

        assertFalse(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 5. Root ("/") section with rw -> PASS
    @Test
    void authorize_shouldPass_whenRootSectionHasRWAccess() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/", "rw", "m1")));

        assertTrue(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 6. Root ("/") section with ro -> FAIL
    @Test
    void authorize_shouldFail_whenRootSectionHasROAccess() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/", "ro", "m1")));

        assertFalse(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 7–9. Admin at various levels -> always PASS
    @Test
    void authorize_shouldPass_whenExactSectionAdmin() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/section/subsection", "admin", "m1")));

        assertTrue(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    @Test
    void authorize_shouldPass_whenParentSectionAdmin() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/section", "admin", "m1")));

        assertTrue(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    @Test
    void authorize_shouldPass_whenRootAdmin() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.singletonList(new Role("/", "admin", "m1")));

        assertTrue(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 10. No roles at all -> FAIL
    @Test
    void authorize_shouldFail_whenUserHasNoRoles() {
        Person person = new Person(1, "user", "pass");
        mockPersonRoles(person, Collections.<Role>emptyList());

        assertFalse(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 11. PersonRolesDAO returns null -> FAIL
    @Test
    void authorize_shouldFail_whenPersonRolesIsNull() {
        Person person = new Person(1, "user", "pass");
        when(personDaoMock.getRoleWhereStringFor(person)).thenReturn("WHERE person_id=" + person.getId());
        when(personRolesDaoMock.load("WHERE person_id=" + person.getId())).thenReturn(null);

        assertFalse(authorizationService.authorize(person, "/section/subsection", AccessOperationType.Write));
    }

    // ✅ 12–15. getUpperLever() path tests (using reflection)
    @Test
    void getUpperLever_shouldReturnExpectedValues() throws Exception {
        Method method = AuthorizationService.class.getDeclaredMethod("getUpperLever", String.class);
        method.setAccessible(true);

        assertEquals("/section/subsection",
                (String) method.invoke(authorizationService, "/section/subsection/subsubsection"));
        assertEquals("/section",
                (String) method.invoke(authorizationService, "/section/subsection"));
        assertEquals("/",
                (String) method.invoke(authorizationService, "/section"));
        assertEquals("",
                (String) method.invoke(authorizationService, "/"));
    }
}
