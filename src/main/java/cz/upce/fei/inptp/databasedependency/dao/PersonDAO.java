package cz.upce.fei.inptp.databasedependency.dao;

import com.google.inject.Inject;
import cz.upce.fei.inptp.databasedependency.entity.Person;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO object for Person entity.
 */
public class PersonDAO implements DAO<Person> {

    private final Database database;

    @Inject
    public PersonDAO(Database database) {
        this.database = database;
    }

    @Override
    public boolean save(Person object) {
        try (Statement st = database.createStatement()) {
            st.execute("delete from person where id = " + object.getId());
            st.execute("insert into person values (" + object.getId() + ", '" + object.getName() + "', '" + object.getPassword() + "')");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Person load(String parameters) {
        try (Statement st = database.createStatement()) {
            ResultSet rs = st.executeQuery("select * from person where " + parameters);
            if (!rs.next()) {
                return null;
            }
            
            Person p = new Person(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("password")
            );
            
            return p;
        } catch (SQLException ex) {
            Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public boolean delete(Person object) {
        try (Statement st = database.createStatement()) {
            st.execute("delete from person where id = " + object.getId());
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PersonDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public String getRoleWhereStringFor(Person person) {
        return "id = " + person.getId();
    }

}
