package cz.upce.fei.inptp.databasedependency;

import cz.upce.fei.inptp.databasedependency.business.AuthorizationService;
import cz.upce.fei.inptp.databasedependency.business.AuthenticationService;
import cz.upce.fei.inptp.databasedependency.business.AccessOperationType;
import cz.upce.fei.inptp.databasedependency.dao.PersonRolesDAO;
import cz.upce.fei.inptp.databasedependency.dao.PersonDAO;
import cz.upce.fei.inptp.databasedependency.dao.Database;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.upce.fei.inptp.databasedependency.guice.AppModule;

/**
 *
 */
public class Main {

    /*
    TODO: Tasks:
     - Create required unit tests for AuthenticationService
     - Create required unit tests for AuthorizationService
     - Create service UserManagerService with methods:
      - Service MUST depend only on DAO objects, no specific code for DB
      - CreateUser(String name, String password) : Person
      - DeleteUser(Person p) : boolean
      - ChangePassword(Person p, String newPassword) : boolean
     - Create service UserRoleManagerService
      - ...
    */
    public static void main(String[] args) throws SQLException {
        Injector injector = Guice.createInjector(new AppModule());

        Database database = injector.getInstance(Database.class);
        database.open();

        PersonDAO personDAO = injector.getInstance(PersonDAO.class);
        PersonRolesDAO personRolesDAO = injector.getInstance(PersonRolesDAO.class);

        
        // create person
        Person person = new Person(10, "Peter", AuthenticationService.encryptPassword("rafanovsky"));
        personDAO.save(person);

        // load person
        person = personDAO.load("id = 10");
        System.out.println(person);

        // test authentication
        AuthenticationService authentication = injector.getInstance(AuthenticationService.class);
        System.out.println(authentication.authenticate("Peter", "rafa"));
        System.out.println(authentication.authenticate("Peter", "rafanovsky"));

        // check user roles
        PersonRole pr = personRolesDAO.load("name = 'yui'");
        System.out.println(pr);

        // test authorization
        person = personDAO.load("id = 2");
        AuthorizationService authorization = injector.getInstance(AuthorizationService.class);
        boolean authorizationResult = authorization.authorize(person, "/finance/report", AccessOperationType.Read);
        System.out.println(authorizationResult);
        
        
        // load all persons from db
        try {
            Statement statement = database.createStatement();
            ResultSet rs = statement.executeQuery("select * from person");
            while (rs.next()) {
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        database.close();
    }
}
