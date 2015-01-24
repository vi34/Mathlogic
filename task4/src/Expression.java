import java.util.*;

/**
 * Created by vi34 on 08.01.15.
 */
public class Expression {
    String representation;
    String oper;
    String rest;
    boolean inBraces = false;
    public Expression first, second;
    public List<Expression> terms;
    public Set<String> freeVariables;

    Expression() {

        freeVariables = new HashSet<String>();
    }

    Expression(Expression first, Expression second, String oper) {
        if(first != null) {
            freeVariables = new HashSet<String>();
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
        freeVariables = new HashSet<String>(other.freeVariables);
        this.representation = other.representation;
        this.oper = other.oper;
        this.inBraces = other.inBraces;
        if(other.first != null)
            this.first = new Expression(other.first);
        if(other.second != null)
            this.second = new Expression(other.second);
    }

    Expression substitute(Vector<Expression> vec){
        if(first != null) {
            first.substitute(vec);
            this.representation = first.representation;
            if(second != null) {
                second.substitute(vec);
                this.representation += oper + second.representation;
                if(inBraces)
                    this.representation = "(" + this.representation + ")";
            } else {
                this.representation = oper + first.representation;
            }
        } else {
            Expression expr = vec.get(Integer.parseInt(this.representation) - 1);
            this.first = expr.first;// hmm new?
            this.second = expr.second;
            this.oper = expr.oper;
            this.representation = expr.representation;
            this.inBraces = expr.inBraces;
        }
        return this;
    }
}
