package com.vi34.parsing.tree;

import com.vi34.ordinals.CNFOrdinal;

/**
 * Created by vi34 on 29.10.2016.
 */
public interface Node {
    Oper oper();
    CNFOrdinal toCNF();
}
