package at.gruene.platform.idm.api;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

/**
 * Special Principial for Gruene
 */
public class GruenPrincipal implements Principal {

    private String name;
    private String viewName;
    private List<String> orgUnits;

    // for proxying
    public GruenPrincipal() {
    }

    public GruenPrincipal(String name, String viewName, List<String> orgUnits) {
        this.name = name;
        this.viewName = viewName;
        this.orgUnits = orgUnits;
    }

    public String getName() {
        return name;
    }

    public String getViewName() {
        return viewName;
    }

    /**
     * @return unmodifiable List of organisational units. e.g. 'BO10', 'GrueneAndersrum', etc
     */
    public List<String> getOrgUnits() {
        return orgUnits;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }

        GruenPrincipal op = (GruenPrincipal) o;

        return op.getName().equals(this.name);
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
