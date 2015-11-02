import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Created by vi34 on 05.02.15.
 */
public class Main {
    FastScanner in;
    PrintWriter out;
    ExpressionParser parser;

    public void solve() throws IOException {
        String s;
        Expression expr;
        Vector<String> answer = new Vector<String>();
        parser = new ExpressionParser();
        s = in.nextLine();
        while (s == null || s.equals("")) {
            s = in.nextLine();
        }
        s = s.replace(" ", "");
        expr = parser.parse(s);


    }

    Vector<Expression> parseHelper(String[] strings) {
        Vector<Expression> res = new Vector<Expression>();
        for (String string : strings) {
            res.add(parser.parse(string));
        }
        return res;
    }

    public void run() {
        try {
            in = new FastScanner(new File("test.in"));
            out = new PrintWriter(new File("test.out"));

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
