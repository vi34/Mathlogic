import java.util.Vector;

/**
 * Created by vi34 on 08.12.14.
 */
public class Expression {
    String representation;
    String oper;
    String rest;
    boolean inBraces = false;
    public Expression first, second;

    Expression() {

    }

    Expression(Expression first, Expression second, String oper) {
        if(first != null) {
            this.first = new Expression(first);
            this.representation = first.representation;
            this.inBraces = first.inBraces;
            this.oper = oper;
            if(second != null) {
                this.second = new Expression(second);
                this.representation = "(" + this.first.representation + oper + this.second.representation + ")";
                this.inBraces = true;
            } else {
                this.representation = oper + this.representation;
            }
        }

    }

    Expression(Expression other) {
        this.representation = other.representation;
        this.oper = other.oper;
        this.inBraces = other.inBraces;
        if(other.first != null)
            this.first = new Expression(other.first);
        if(other.second != null)
            this.second = new Expression(other.second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expression that = (Expression) o;

        return representation != null ? representation.equals(that.representation) : that.representation == null;

    }

    @Override
    public int hashCode() {
        return representation != null ? representation.hashCode() : 0;
    }
}
