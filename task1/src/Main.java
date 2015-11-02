
import java.util.*;
import java.io.*;


public class Main {
    FastScanner in;
    PrintWriter out;
    ExpressionParser parser;

    public void solve() throws IOException {
        String s;
        Expression expr;
        int lineCounter = 0;
        ArrayList<Expression> proved = new ArrayList<Expression>();
        ArrayList<Integer> pLineNum = new ArrayList<Integer>();
        parser = new ExpressionParser();
        try {
            while (true) {
                s = in.nextLine();
                s = s.replace(" ", "");
                if(s.equals("")) {continue;}
                expr = parser.parse(s);

                String answer = "";
                // axiom 1
                try {
                    String a = expr.first.representation;
                    String b = expr.second.second.representation;
                    if(expr.second.oper.equals("->") && a.equals(b)) {
                        answer = "(Сх. акс. " + 1 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }
                } catch (Exception e) {}
                // axiom 2  (A->B)->(A->B->C)->(A->C)
                try {
                    boolean opers = true;
                    opers &= expr.first.oper.equals("->");
                    opers &= expr.oper.equals("->");
                    opers &= expr.second.oper.equals("->");
                    opers &= expr.second.first.oper.equals("->");
                    opers &= expr.second.first.second.oper.equals("->");
                    opers &= expr.second.second.oper.equals("->");

                    String a1 = expr.first.first.representation;
                    String a2 = expr.second.first.first.representation;
                    String a3 = expr.second.second.first.representation;
                    String b1 = expr.first.second.representation;
                    String b2 = expr.second.first.second.first.representation;
                    String c1 = expr.second.first.second.second.representation;
                    String c2 = expr.second.second.second.representation;

                    if(opers && a1.equals(a2) && a2.equals(a3) && b1.equals(b2) && c1.equals(c2)) {
                        answer = "(Сх. акс. " + 2 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}
                //axiom 3 A->B->A&B
                try {
                    boolean opers = true;
                    opers &= expr.oper.equals("->");
                    opers &= expr.second.oper.equals("->");
                    opers &= expr.second.second.oper.equals("&");

                    String a1 = expr.first.representation;
                    String a2 = expr.second.second.first.representation;
                    String b1 = expr.second.first.representation;
                    String b2 = expr.second.second.second.representation;

                    if(opers && a1.equals(a2) && b1.equals(b2)) {
                        answer = "(Сх. акс. " + 3 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}
                //axiom 4,5 A&B->A
                try {
                    boolean opers = true;
                    opers &= expr.first.oper.equals("&");
                    opers &= expr.oper.equals("->");

                    String a1 = expr.first.first.representation;
                    String a2 = expr.second.representation;
                    String b = expr.first.second.representation;

                    if(a1.equals(a2) && opers) {
                        answer = "(Сх. акс. " + 4 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    } else if(b.equals(a2) && opers) {
                        answer = "(Сх. акс. " + 5 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}
                //axiom 6,7 A->A|B
                try {
                    boolean opers = true;
                    opers &= expr.oper.equals("->");
                    opers &= expr.second.oper.equals("|");

                    String a1 = expr.first.representation;
                    String a2 = expr.second.first.representation;
                    String b = expr.second.second.representation;

                    if(opers && a1.equals(a2)) {
                        answer = "(Сх. акс. " + 6 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    } else if(opers && a1.equals(b)) {
                        answer = "(Сх. акс. " + 7 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}
                //axiom 8 (A->Q)->(B->Q)->(A|B->Q)
                try {
                    boolean opers = true;
                    opers &= expr.oper.equals("->");
                    opers &= expr.first.oper.equals("->");
                    opers &= expr.second.oper.equals("->");
                    opers &= expr.second.first.oper.equals("->");
                    opers &= expr.second.second.oper.equals("->");
                    opers &= expr.second.second.first.oper.equals("|");

                    String a1 = expr.first.first.representation;
                    String a2 = expr.second.second.first.first.representation;
                    String b1 = expr.second.first.first.representation;
                    String b2 = expr.second.second.first.second.representation;
                    String c1 = expr.first.second.representation;
                    String c2 = expr.second.first.second.representation;
                    String c3 = expr.second.second.second.representation;

                    if(opers && a1.equals(a2) && b1.equals(b2) && c1.equals(c2) && c2.equals(c3)) {
                        answer = "(Сх. акс. " + 8 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}
                //axiom 9 (A->B)->(A->!B)->!A
                try {
                    boolean opers = true;
                    opers &= expr.oper.equals("->");
                    opers &= expr.first.oper.equals("->");
                    opers &= expr.second.oper.equals("->");
                    opers &= expr.second.first.oper.equals("->");
                    opers &= expr.second.first.second.oper.equals("!");
                    opers &= expr.second.second.oper.equals("!");
                    String a1 = expr.first.first.representation;
                    String a2 = expr.second.first.first.representation;
                    String a3 = expr.second.second.first.representation;
                    String b1 = expr.first.second.representation;
                    String b2 = expr.second.first.second.first.representation;

                    if(opers && a1.equals(a2) && a2.equals(a3) && b1.equals(b2)) {
                        answer = "(Сх. акс. " + 9 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}
                // axiom 10
                try {
                    String a = expr.first.first.first.representation;
                    String b = expr.second.representation;
                    if(expr.first.oper.equals("!") && expr.first.first.oper.equals("!") && a.equals(b)) {
                        answer = "(Сх. акс. " + 10 + ")";
                        out.println("("+lineCounter++ +")" + s + answer);
                        proved.add(expr);
                        pLineNum.add(lineCounter - 1);
                        continue;
                    }

                } catch (Exception e) {}


                boolean prove = false;
                int mp1 = 0;
                int mp2 = 0;
                String left;
                for(int i = proved.size() - 1; i >= 0 && !prove; --i) {
                    if(proved.get(i).first == null || proved.get(i).second == null)
                        continue;
                    left = proved.get(i).first.representation;
                    if(proved.get(i).oper.equals("->") && proved.get(i).second.representation.equals(expr.representation)) {
                        for(int j = proved.size() - 1; j >= 0 && !prove; --j) {
                            if(proved.get(j).representation.equals(left)) {
                                mp1 = pLineNum.get(j);
                                mp2 = pLineNum.get(i);
                                prove = true;
                            }
                        }
                    }
                }
                if(prove) {
                    answer = "(M.P. " + mp1 + "," + mp2 + ")";
                    out.println("("+lineCounter++ +")" + s + answer);
                    proved.add(expr);
                    pLineNum.add(lineCounter - 1);
                    continue;
                }

                out.println("("+lineCounter++ +")" + s + "(Не доказано)");

            }
        } catch (NullPointerException e) {
        }

    }

    public void run() {
        try {
            in = new FastScanner(new File("axiom.in"));
            out = new PrintWriter(new File("axiom.out"),"UTF-8");

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        String nextLine() {
            String res = null;
            try {
                res = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    public static void main(String[] arg) {
        new Main().run();
    }
}