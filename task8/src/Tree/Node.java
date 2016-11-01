package Tree;

import Ordinal.Ordinal;

/**
 * Created by izban on 30.05.2016.
 */
public abstract class Node {
    Node[] children;
    protected abstract NodeType type();
    public abstract Ordinal calcValue();
}
