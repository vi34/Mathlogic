import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by vi34 on 13/09/15.
 */
public class Model {
    Vector<Model> children;
    World world;
    private boolean active;
    int subtree;
    Model parent;
    HashMap<Expression, Boolean> cache = new HashMap<>();

    Model(World world) {
        children = new Vector<>();
        this.world = world;
        setActive(true);
        subtree = 0;
    }

    void addChild(World world) {
        Model model = new Model(world);
        model.parent = this;
        children.add(model);
    }

    boolean checkExpression (Expression expr) {
        if (!isActive()) {
            return true;
        }
        if (!cache.containsKey(expr)) {
            cache.put(expr, cachedCheck(expr));
        }
        return cache.get(expr);
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
            if (child.isActive()) {
                child.print(out, shift + 2);
            }
        }
    }

    boolean deepCheck(Expression expr) {
        boolean res = false;
        res |= checkExpression(expr);
        for (Model child: children) {
            if (child.isActive()) {
                res |= child.deepCheck(expr);
            }
        }
        return res;
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
                if (child.isActive() && child.deepCheck(expr.first)) {
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
                if (child.isActive() && !child.checkExpression(expr)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (active != this.active) {
            invalidateCaches(this);
        }
        this.active = active;
    }

    public void invalidateCaches(Model model) {
        model.cache.clear();
        if (model.parent != null) {
           invalidateCaches(model.parent);
        }
    }
}
