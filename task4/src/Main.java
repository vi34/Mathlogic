import com.sun.tools.javac.util.Pair;
import sun.rmi.runtime.Log;

import java.util.*;
import java.io.*;

/**
 * Created by vi34 on 08.12.14.
 */

public class Main {
    FastScanner in;
    PrintWriter out;
    ExpressionParser parser;

    public void solve() throws IOException {
        String s;
        Expression expr;
        Expression alphaExpr;
        Expression bettaExpr;
        Vector<Expression> proved = new Vector<Expression>();
        Vector<Expression> gamma = new Vector<Expression>();
        Vector<String> answer = new Vector<String>();
        parser = new ExpressionParser();

        s = in.nextLine();
        s = s.replace(" ", "");
        int comma = s.indexOf(',');
        while (comma != -1) {
            expr = parser.parse(s.substring(0,comma));
            gamma.add(expr);
            proved.add(expr);
            s = s.substring(comma + 1);
            comma = s.indexOf(',');
        }
        alphaExpr = parser.parse(s.substring(0, s.indexOf("|-")));
        s = s.substring(s.indexOf("|-") + 2);
        bettaExpr = parser.parse(s);
        String firstLine = "";
        for(int i = 0; i < gamma.size() - 1; ++i) {
            firstLine += gamma.get(i).representation + ",";
        }
        if(gamma.size() > 0) {
            firstLine += gamma.get(gamma.size() - 1).representation;
        }
        firstLine += "|-";
        firstLine += alphaExpr.representation + "->" + bettaExpr.representation;
        answer.add(firstLine);

        answer.addAll(deduction(gamma, alphaExpr));

        for(int i = 0; i < answer.size(); ++i) {
            out.println(answer.get(i));
        }
    }

    public Vector<String> deduction(Vector<Expression> gammaExpr, Expression alphaExpr) {
        Vector<String> res = new Vector<String>();
        Expression expr;
        Vector<Expression> proved = new Vector<Expression>();
        String s;
        try {
            while(true) {
                s = in.nextLine();
                s = s.replace(" ", "");
                if(s.equals("")) {continue;}
                expr = parser.parse(s);
                proved.add(expr);
                int axiom = check_axiom(expr);
                boolean exprType;
                if(axiom > 0) {
                    exprType = true;
                } else {
                    exprType = false;
                }

                if(!exprType) {
                    for(int i = 0; i < gammaExpr.size(); ++i) {
                        if(gammaExpr.get(i).representation.equals(expr.representation)) {
                            exprType = true;
                            break;
                        }
                    }
                }

                Vector<Expression> var = new Vector<Expression>();
                // expr is axiom or gamma
                if (exprType) {
                    res.add("(" + res.size() + ")" + expr.representation + "(cx.A" + axiom + ")");
                    Expression etmp = parser.parse("1->(2->1)");
                    var.add(expr);
                    var.add(alphaExpr);
                    etmp.substitute(var);
                    //res.add(etmp.representation);

                }

                if (!exprType && alphaExpr.representation.equals(expr.representation)) {
                    var.clear();
                    var.add(expr);
                    Expression etmp = new Expression(expr,expr,"->");
                    var.add(etmp);
                    //res.add(parser.parse("1->2").substitute(var).representation);
                    //res.add(parser.parse("(1->2)->(1->(2->1))->2").substitute(var).representation);
                    //res.add(parser.parse("(1->(2->1))->2").substitute(var).representation);
                    //res.add(parser.parse("1->(2->1)").substitute(var).representation);
                    //out.println(2);  //////
                    exprType = true;
                }

                boolean prove = false;
                if (!exprType) {
                    var.clear();
                    var.add(alphaExpr);

                    String left;
                    for(int i = proved.size() - 2; i >= 0 && !prove; --i) {
                        if(proved.get(i).first == null || proved.get(i).second == null)
                            continue;
                        left = proved.get(i).first.representation;
                        if(proved.get(i).oper.equals("->") && proved.get(i).second.representation.equals(expr.representation)) {
                            for(int j = proved.size() - 1; j >= 0 && !prove; --j) {
                                if(proved.get(j).representation.equals(left)) {
                                    var.add(proved.get(i).first);
                                    var.add(proved.get(i));
                                    var.add(expr);

                                    //res.add(parser.parse("(1->2)->((1->3)->(1->4))").substitute(var).representation);
                                    //res.add(parser.parse("((1->3)->(1->4))").substitute(var).representation);
                                    //out.println(3);/////
                                    res.add("(" + res.size() + ")" +expr.representation+ "(MP "+ j + "," + i + ")" );
                                    prove = true;
                                }
                            }
                        }
                    }
                }
                if(!prove && axiom == 0) {
                    res.add(expr.representation + " (не доказано)");
                }
                Expression etmp = new Expression(alphaExpr,expr, "->");
                //res.add(etmp.representation);

            }
        } catch (Exception e) {
        }

        return res;
    }


    int check_axiom(Expression expr) {
        boolean opers;
        // axiom 1
        try {
            String a = expr.first.representation;
            String b = expr.second.second.representation;
            if (expr.second.oper.equals("->") && a.equals(b)) {
                return 1;
            }
        } catch (Exception e) {
        }
        // axiom 2  (A->B)->(A->B->C)->(A->C)
            try {
                opers = true;
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
                if (opers && a1.equals(a2) && a2.equals(a3) && b1.equals(b2) && c1.equals(c2)) {
                    return 2;
                }
            } catch (Exception e) {
            }
        //axiom 3 A->B->A&B
            try {
                opers = true;
                opers &= expr.oper.equals("->");
                opers &= expr.second.oper.equals("->");
                opers &= expr.second.second.oper.equals("&");

                String a1 = expr.first.representation;
                String a2 = expr.second.second.first.representation;
                String b1 = expr.second.first.representation;
                String b2 = expr.second.second.second.representation;

                if (opers && a1.equals(a2) && b1.equals(b2)) {
                    return 3;
                }

            } catch (Exception e) {
            }
        //axiom 4,5 A&B->A
            try {
                opers = true;
                opers &= expr.first.oper.equals("&");
                opers &= expr.oper.equals("->");
                String a1 = expr.first.first.representation;
                String a2 = expr.second.representation;
                String b = expr.first.second.representation;
                if (opers && (a1.equals(a2) || b.equals(a2))) {
                    return 4;
                }
            } catch (Exception e) {
            }
        //axiom 6,7 A->A|B
            try {
                opers = true;
                opers &= expr.oper.equals("->");
                opers &= expr.second.oper.equals("|");
                String a1 = expr.first.representation;
                String a2 = expr.second.first.representation;
                String b = expr.second.second.representation;

                if (opers && (a1.equals(a2) || a1.equals(b))) {
                   return 6;
                }
            } catch (Exception e) {
            }
        //axiom 8 (A->Q)->(B->Q)->(A|B->Q)
            try {
                opers = true;
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

                if (opers && a1.equals(a2) && b1.equals(b2) && c1.equals(c2) && c2.equals(c3)) {
                    return 8;
                }
            } catch (Exception e) {
            }
        //axiom 9 (A->B)->(A->!B)->!A
            try {
                opers = true;
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

                if (opers && a1.equals(a2) && a2.equals(a3) && b1.equals(b2)) {
                   return 9;
                }
            } catch (Exception ignored) {
            }
        // axiom 10
            try {
                String a = expr.first.first.first.representation;
                String b = expr.second.representation;
                if (expr.first.oper.equals("!") && expr.first.first.oper.equals("!") && a.equals(b)) {
                    return 10;
                }

            } catch (Exception ignored) {
            }
        // axiom 11 ∀x(ψ)->(ψ[x := θ])
            try {
                opers = true;
                opers &= expr.oper.equals("->");
                opers &= expr.first.oper.equals("@");
                compareOnSubstitution(expr.first.second, expr.second, expr.first.first.representation);

            } catch (Exception ignored) {
            }
        return 0;
    }

    Expression theta = null;
    boolean correct = true;

    Expression compareOnSubstitution(Expression a, Expression b, String x) {
        theta = null;
        correct = true;

        try {

        } catch (Exception e) {
            correct = false;
        }


        return theta;
    }

    Expression dfs(Expression a, Expression b, String x) {
        if(!a.freeVariables.contains(x)) {
            return a;
        }
        if(a.first != null) {
            if(a.first.freeVariables.contains(x)) {
                dfs(a.first,b.first,x);
            }
        }

        if(a.oper.equals(x) && a.terms == null) {
            if(theta == null) {
                theta = b;
            } else if(!theta.representation.equals(b.representation)) {
                correct = false;
            }
            return b;
        }
        return null;//
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
