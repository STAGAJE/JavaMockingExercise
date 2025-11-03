package cz.upce.fei.inptp.databasedependency.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import cz.upce.fei.inptp.databasedependency.business.UserManagerService;
import cz.upce.fei.inptp.databasedependency.business.UserRoleManagerService;
import cz.upce.fei.inptp.databasedependency.dao.DAO;
import cz.upce.fei.inptp.databasedependency.dao.Database;
import cz.upce.fei.inptp.databasedependency.dao.PersonDAO;
import cz.upce.fei.inptp.databasedependency.dao.PersonRolesDAO;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // Bind interfaces to implementations
        bind(Database.class).asEagerSingleton();
        bind(PersonDAO.class);
        bind(PersonRolesDAO.class);
        bind(UserManagerService.class);
        bind(UserRoleManagerService.class);
        bind(new TypeLiteral<DAO<Person>>() {}).to(PersonDAO.class);
        bind(new TypeLiteral<DAO<PersonRole>>() {}).to(PersonRolesDAO.class);
    }
}
