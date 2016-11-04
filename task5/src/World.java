import java.util.Set;
import java.util.TreeSet;

/**
 * Created by vi34 on 12/09/15.
 */
public class World {
    Set<String> variables;

    World() {
        variables = new TreeSet<String>();
    }

    void forceVariable(Expression var) {
        variables.add(var.representation);
    }

    void forceVariable(String var) {
        variables.add(var);
    }

    boolean isForced(Expression var) {
        return variables.contains(var.representation);
    }

    boolean isSubset (World world) {
        return world.variables.containsAll(variables) && !variables.equals(world.variables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        World world = (World) o;

        return variables != null ? variables.equals(world.variables) : world.variables == null;

    }

    @Override
    public int hashCode() {
        return variables != null ? variables.hashCode() : 0;
    }
}
