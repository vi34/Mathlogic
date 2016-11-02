package com.vi34.parsing.tree;

/**
 * Created by vi34 on 29.10.2016.
 */
public enum Oper {
    VALUE,
    EXP {
        @Override
        public String toString() {
            return "^";
        }
    },
    MUL {
        @Override
        public String toString() {
            return "*";
        }
    },
    ADD {
        @Override
        public String toString() {
            return "+";
        }
    },
    SUB {
        @Override
        public String toString() {
            return "-";
        }
    }
}
