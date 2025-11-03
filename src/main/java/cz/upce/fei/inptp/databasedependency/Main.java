package cz.upce.fei.inptp.databasedependency;

import cz.upce.fei.inptp.databasedependency.business.*;
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

    public static void main(String[] args) throws SQLException {
        Injector injector = Guice.createInjector(new AppModule());

        Database database = injector.getInstance(Database.class);
        database.open();

        PersonDAO personDAO = injector.getInstance(PersonDAO.class);
        PersonRolesDAO personRolesDAO = injector.getInstance(PersonRolesDAO.class);

        UserManagerService userManager = injector.getInstance(UserManagerService.class);
        UserRoleManagerService userRoleManager = injector.getInstance(UserRoleManagerService.class);

        // create person
        userManager.createUser(10, "Peter", AuthenticationService.encryptPassword("rafanovsky"));

        // load person
        Person person = personDAO.load("id = 10");
        System.out.println(person);

        // test authentication
        AuthenticationService authentication = injector.getInstance(AuthenticationService.class);
        System.out.println(authentication.authenticate("Peter", "rafa"));
        System.out.println(authentication.authenticate("Peter", "rafanovsky"));

        // check user roles
        person = personDAO.load("name = 'yui'");
        PersonRole pr = userRoleManager.getRoles(person);
        System.out.println(pr);

        // test authorization
        AuthorizationService authorization = injector.getInstance(AuthorizationService.class);
        boolean authorizationResult = authorization.authorize(person, "/finance/report", AccessOperationType.Read);
        System.out.println(authorizationResult);
        
        
        // load all persons from db
        try (Statement st = database.createStatement()) {
            ResultSet rs = st.executeQuery("select * from person");
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
