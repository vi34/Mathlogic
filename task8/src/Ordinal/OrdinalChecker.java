package Task8.Ordinal;

/**
 * Created by izban on 30.05.2016.
 */
public class OrdinalChecker {
    public String check(String s) {
        String a = s.substring(0, s.indexOf("="));
        String b = s.substring(s.indexOf("=") + 1);
        Ordinal x = Ordinal.parseOrdinal(a);
        Ordinal y = Ordinal.parseOrdinal(b);
        if (Ordinal.cmpo(x, y) == 0) {
            return "Равны";
        }
        return "Не равны";
    }
}
