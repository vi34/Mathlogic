package Task8.Tree;

import Task8.Ordinal.Ordinal;

import static Task8.Ordinal.Ordinal.*;

/**
 * Created by izban on 30.05.2016.
 */
public class NodeExp extends NodeBinary {
    public NodeExp(Node res, Node node) {
        super(res, node);
    }

    @Override
    protected Ordinal f(Ordinal a, Ordinal b) {
        return exp(a, b);
    }

    @Override
    public NodeType type() {
        return NodeType.EXPONENT;
    }
}
