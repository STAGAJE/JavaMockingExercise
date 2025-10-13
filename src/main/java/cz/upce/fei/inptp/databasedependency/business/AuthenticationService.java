package cz.upce.fei.inptp.databasedependency.business;

import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.dao.PersonDAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication service - used for authentication of users stored in db.
 * Authentication should success if login and password (hashed) matches.
 */
public class AuthenticationService {

    private final DAO<Person> personDAO;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthenticationService(DAO<Person> personDAO) {
        if (personDAO == null) {
            this.personDAO = new PersonDAO();
        } else {
            this.personDAO = personDAO;
        }
    }

    public boolean authenticate(String login, String password) {
        Person person = personDAO.load("name = '" + login + "'");
        if (person == null) {
            return false;
        }
        return checkPassword(password, person.getPassword());
    }

    public static String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

}
