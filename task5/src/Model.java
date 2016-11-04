import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by vi34 on 13/09/15.
 */
public class Model {
    Vector<Model> children;
    World world;
    boolean active;
    int subtree;
    HashMap<Expression, Boolean> cache = new HashMap<>();

    Model(World world) {
        children = new Vector<>();
        this.world = world;
        active = true;
        subtree = 0;
    }

    void addChild(World world) {
        children.add(new Model(world));
    }

    boolean checkExpression (Expression expr) {
        if (!active) {
            return true;
        }
        if (!cache.containsKey(expr)) {
            cache.put(expr, cachedCheck(expr));
        }
        return cachedCheck(expr);
    }

    void print(PrintWriter out, int shift) {
        for (int i = 0; i < shift; ++i) {
            out.print(" ");
        }
        out.print("* ");
        for (String variable: world.variables) {
            out.print(variable + " ");
        }
        out.println();
        for (Model child: children) {
            if (child.active) {
                child.print(out, shift + 2);
            }
        }
    }

    boolean cachedCheck(Expression expr) {
        if (expr.first == null) {
            return world.isForced(expr);
        }
        if (expr.oper.equals("&")) {
            return this.checkExpression(expr.first) && this.checkExpression(expr.second);
        }
        if (expr.oper.equals("|")) {
            return this.checkExpression(expr.first) || this.checkExpression(expr.second);
        }
        if (expr.oper.equals("!")) {
            if (this.checkExpression(expr.first)) {
                return false;
            }
            for (Model child: children) {
                if (child.active && child.checkExpression(expr.first)) {
                    return false;
                }
            }
            return true;
        }
        if (expr.oper.equals("->")) {
            if (this.checkExpression(expr.first)) {
                if (!this.checkExpression(expr.second)) {
                    return false;
                }
            }
            for (Model child: children) {
                if (child.active && !child.checkExpression(expr)) {
                    return false;
                }
            }
        }
        return true;
    }
}
