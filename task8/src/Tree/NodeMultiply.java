package Task8.Tree;

import Task8.Ordinal.Ordinal;

import static Task8.Ordinal.Ordinal.*;

/**
 * Created by izban on 30.05.2016.
 */
public class NodeMultiply extends NodeBinary {
    public NodeMultiply(Node res, Node node) {
        super(res, node);
    }

    @Override
    protected Ordinal f(Ordinal a, Ordinal b) {
        return multiply(a, b);
    }

    @Override
    public NodeType type() {
        return NodeType.MULTIPLY;
    }
}
