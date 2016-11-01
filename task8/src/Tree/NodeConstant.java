package Task8.Tree;

import Task8.Ordinal.Ordinal;

/**
 * Created by izban on 30.05.2016.
 */
public class NodeConstant extends Node {
    private final String s;

    public NodeConstant(String s) {
        this.s = s;
    }

    @Override
    public NodeType type() {
        return NodeType.VALUE;
    }

    @Override
    public Ordinal calcValue() {
        if (s.equals("w")) {
            return new Ordinal(new Ordinal(Ordinal.ONE, Ordinal.ONE), Ordinal.ZERO);
        }
        return new Ordinal(Long.parseLong(s));
    }
}
