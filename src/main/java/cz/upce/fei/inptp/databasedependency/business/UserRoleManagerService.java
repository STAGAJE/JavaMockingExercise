package cz.upce.fei.inptp.databasedependency.business;

import com.google.inject.Inject;
import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;
import cz.upce.fei.inptp.databasedependency.entity.Role;

import java.util.ArrayList;
import java.util.List;

public class UserRoleManagerService {

    private final DAO<PersonRole> personRoleDao;

    @Inject
    public UserRoleManagerService(DAO<PersonRole> personRoleDao) {
        this.personRoleDao = personRoleDao;
    }

    public boolean assignOrAppendRole(Person person, Role role) {
        if (person == null) {
            return false;
        }

        PersonRole existing = personRoleDao.load("id = " + person.getId());

        if (existing == null) {
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            existing = new PersonRole(person, roles);
        } else {
            existing.getRoles().add(role);
        }

        return personRoleDao.save(existing);
    }

    public boolean removeRoleIfExists(Person person, Role role) {
        if (person == null) {
            return false;
        }

        PersonRole existing = personRoleDao.load("id = " + person.getId());
        return existing != null && existing.getRoles().remove(role) && personRoleDao.save(existing);
    }

    public PersonRole getRoles(Person person) {
        if (person == null) {
            return null;
        }

        return personRoleDao.load("id = " + person.getId());
    }

    public boolean clearRoles(Person person) {
        if (person == null) {
            return false;
        }

        PersonRole personRole = personRoleDao.load("id = " + person.getId());
        if (personRole == null) {
            return false;
        }

        personRole.getRoles().clear();
        return personRoleDao.delete(personRole);
    }
}
