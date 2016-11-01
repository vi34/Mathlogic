package Tree;

/**
 * Created by izban on 30.05.2016.
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
