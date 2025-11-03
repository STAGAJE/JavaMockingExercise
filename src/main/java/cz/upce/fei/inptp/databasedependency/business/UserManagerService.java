package cz.upce.fei.inptp.databasedependency.business;

import com.google.inject.Inject;
import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;

public class UserManagerService {
    private final DAO<Person> personDao;

    @Inject
    public UserManagerService(DAO<Person> personDao) {
        this.personDao = personDao;
    }

    public Person createUser(int id, String name, String password) {
        Person person = new Person(id, name, AuthenticationService.encryptPassword(password));
        personDao.save(person);
        return person;
    }

    public boolean deleteUser(Person person) {
        return person != null && personDao.delete(person);
    }

    public boolean changePassword(Person person, String newPassword) {
        if (person == null) {
            return false;
        }

        person.setPassword(AuthenticationService.encryptPassword(newPassword));
        return personDao.save(person);
    }
}
