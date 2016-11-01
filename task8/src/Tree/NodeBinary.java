package Task8.Tree;

import Task8.Ordinal.Ordinal;

/**
 * Created by izban on 30.05.2016.
 */
public abstract class NodeBinary extends Node {
    NodeBinary() {}

    NodeBinary(Node l, Node r) {
        children = new Node[2];
        children[0] = l;
        children[1] = r;
    }

    @Override
    public String toString() {
        return "(" + children[0].toString() + ")" + type().toString() + "(" + children[1].toString() + ")";
    }

    protected abstract Ordinal f(Ordinal a, Ordinal b);

    @Override
    public Ordinal calcValue() {
        return f(children[0].calcValue(), children[1].calcValue());
    }
}
