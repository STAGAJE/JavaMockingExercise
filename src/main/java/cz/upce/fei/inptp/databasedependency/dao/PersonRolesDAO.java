package cz.upce.fei.inptp.databasedependency.dao;

import com.google.inject.Inject;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import cz.upce.fei.inptp.databasedependency.entity.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO object for PersonRole entity.
 */
public class PersonRolesDAO implements DAO<PersonRole> {

    private final Database database;
    private final DAO<Person> personDAO;

    @Inject
    public PersonRolesDAO(Database database, DAO<Person> personDAO) {
        this.database = database;
        this.personDAO = personDAO;
    }

    @Override
    public boolean save(PersonRole object) {
        try (Statement st = database.createStatement()) {
            st.execute("delete from role where id = " + object.getPerson().getId());

            for (Role role : object.getRoles()) {
                st.execute("insert into role values (" + object.getPerson().getId() + ", '" + role.getSection() + "', '" + role.getAccess() + "', '" + role.getModifier() + "')");
            }

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public PersonRole load(String parameters) {
        try (Statement st = database.createStatement()) {
            Person person = personDAO.load(parameters);
            if (person == null) {
                return null;
            }

            ResultSet rs = st.executeQuery("select * from role where id = " + person.getId());
            ArrayList<Role> roles = new ArrayList<>();

            while (rs.next()) {
                roles.add(new Role(
                        rs.getString("section"),
                        rs.getString("access"), 
                        rs.getString("modifier")
                ));
            }

            return new PersonRole(person, roles);
        } catch (SQLException ex) {
            Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public boolean delete(PersonRole object) {
        try (Statement st = database.createStatement()) {
            st.execute("delete from role where id = " + object.getPerson().getId());
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
