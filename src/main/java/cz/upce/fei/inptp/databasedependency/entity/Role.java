package cz.upce.fei.inptp.databasedependency.entity;

import java.util.Objects;

/**
 * Role entity
 */
public class Role {
    
    private String section;
    private String access;
    private String modifier;

    public Role() {
    }

    public Role(String section, String access, String modifier) {
        this.section = section;
        this.access = access;
        this.modifier = modifier;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return "Role{" + "section=" + section + ", access=" + access + ", modifier=" + modifier + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(section, role.section) && Objects.equals(access, role.access) && Objects.equals(modifier, role.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, access, modifier);
    }
}
