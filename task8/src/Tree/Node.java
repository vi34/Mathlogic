package Task8.Tree;

import Task8.Ordinal.Ordinal;

/**
 * Created by izban on 30.05.2016.
 */
public abstract class Node {
    Node[] children;
    protected abstract NodeType type();
    public abstract Ordinal calcValue();
}
