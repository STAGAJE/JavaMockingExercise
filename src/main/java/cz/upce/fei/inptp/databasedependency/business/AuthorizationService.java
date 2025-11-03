package cz.upce.fei.inptp.databasedependency.business;

import com.google.inject.Inject;
import cz.upce.fei.inptp.databasedependency.dao.PersonRolesDAO;
import cz.upce.fei.inptp.databasedependency.dao.PersonDAO;
import cz.upce.fei.inptp.databasedependency.entity.PersonRole;
import cz.upce.fei.inptp.databasedependency.entity.Person;
import cz.upce.fei.inptp.databasedependency.entity.Role;

/**
 * Authorization service. User is authorized to access specified part of system,
 * if he has required access directly to specified part, or to upper level part.
 */
public class AuthorizationService {

    private final PersonDAO personDAO;
    private final PersonRolesDAO personRolesDAO;

    @Inject
    public AuthorizationService(PersonDAO personDAO, PersonRolesDAO personRolesDAO) {
        this.personDAO = personDAO;
        this.personRolesDAO = personRolesDAO;
    }

    public boolean authorize(Person person, String section, AccessOperationType operationType) {
        String roleWhere = personDAO.getRoleWhereStringFor(person);

        PersonRole roles = personRolesDAO.load(roleWhere);
        if (roles == null) {
            return false;
        }

        do {
            for (Role role : roles.getRoles()) {
                if (role.getSection().equals(section)) {
                    if (role.getAccess().equals(operationType.getOp())) {
                        return true;
                    }

                    return role.getAccess().equals("admin");
                }
            }

            section = getUpperLever(section);
            //System.out.println("newsection " + section);
        } while (!section.isEmpty());

        return false;
    }

    private String getUpperLever(String section) {
        if (section.equals("/")) {
            return "";
        }

        String ret = section.substring(0, section.lastIndexOf("/") + 1);
        return (ret.length() > 1) ? ret.substring(0, ret.length() - 1) : ret;
    }

}
