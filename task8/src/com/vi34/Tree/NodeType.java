package com.vi34.Tree;

/**
 * Created by vi34 on 29.10.2016.
 */
public enum NodeType {
    VALUE,
    EXPONENT {
        @Override
        public String toString() {
            return "^";
        }
    },
    MULTIPLY {
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
    SUBTRACT {
        @Override
        public String toString() {
            return "-";
        }
    }
}
