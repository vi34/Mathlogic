import java.util.*;
import java.io.*;

/**
 * Created by vi34 on 08.12.14.
 */

public class Main {
    FastScanner in;
    PrintWriter out;
    ExpressionParser parser;

    int lastErrorCode = 0;
    int errorCode = 0;
    Expression errTerm;
    Expression errExpr;
    String errVariable;

    ArrayList<Expression> implToConj;
    ArrayList<Expression> conjToImpl;
    ArrayList<Expression> implChange;

    ArrayList<Expression> parseHelper(String[] strings) {
        ArrayList<Expression> res = new ArrayList<Expression>();
        for (String string : strings) {
            res.add(parser.parse(string));
        }
        return res;
    }

    public void solve() throws IOException {
        String s;
        Expression expr;
        Expression alphaExpr;
        Expression bettaExpr;
        ArrayList<Expression> proved = new ArrayList<Expression>();
        ArrayList<Expression> gamma = new ArrayList<Expression>();
        ArrayList<String> answer = new ArrayList<String>();
        parser = new ExpressionParser();
        initProve();
        s = in.nextLine();
        while (s == null || s.equals("")) {
            s = in.nextLine();
        }

        s = s.replace(" ", "");

        if (s.contains("|-")) {
            int balance = 0;
            for (int i = 0; i < s.indexOf("|-"); ++i) {
                if (s.charAt(i) == '(') {
                    balance++;
                } else if (s.charAt(i) == ')') {
                    balance--;
                }
                if (s.charAt(i) == ',' && balance == 0) {
                    expr = parser.parse(s.substring(0, i));
                    gamma.add(expr);
                    proved.add(expr);
                    s = s.substring(i + 1);
                }
            }

            if (s.indexOf("|-") == 0) {
                alphaExpr = null;
            } else {
                alphaExpr = parser.parse(s.substring(0, s.indexOf("|-")));
            }
            // if prove without title. Is it correct input?
            s = s.substring(s.indexOf("|-") + 2);
            bettaExpr = parser.parse(s);
            String firstLine = "";
            for (int i = 0; i < gamma.size() - 1; ++i) {
                firstLine += gamma.get(i).representation + ",";
            }
            if (gamma.size() > 0) {
                firstLine += gamma.get(gamma.size() - 1).representation;
            }
            firstLine += "|-";
            if (alphaExpr != null) {
                firstLine += alphaExpr.representation + "->";
            }
            firstLine += bettaExpr.representation;
            answer.add(firstLine);
        } else {
            in = new FastScanner(new File("test.in"));
            alphaExpr = null;
        }
        ArrayList<String> deductionRes = deduction(gamma, alphaExpr);
        if (deductionRes != null) {
            answer.addAll(deductionRes);
            for (String anAnswer : answer) {
                out.println(anAnswer);
            }
        }
    }

    public ArrayList<String> deduction(ArrayList<Expression> gammaExpr, Expression alphaExpr) {
        ArrayList<String> res = new ArrayList<String>();
        Expression expr;
        ArrayList<Expression> proved = new ArrayList<Expression>();
        String s;
        int lineNumber = -1;
        try {
            while (true) {
                s = in.nextLine();
                s = s.replace(" ", "");
                if (s.equals("")) {
                    continue;
                }
                lineNumber++;
                if (lineNumber == 60) {
                    int a = 5;
                }
                expr = parser.parse(s);
                proved.add(expr);
                int axiom = check_axiom(expr);
                boolean prove;
                prove = axiom > 0;

                if (!prove) {
                    for (Expression aGammaExpr : gammaExpr) {
                        if (aGammaExpr.representation.equals(expr.representation)) {
                            prove = true;
                            break;
                        }
                    }
                }

                ArrayList<Expression> var = new ArrayList<Expression>();
                // expr is axiom or gamma
                if (prove) {
                    res.add(expr.representation);
                    if (alphaExpr != null) {
                        Expression etmp = parser.parse("1->(2->1)");
                        var.add(expr);
                        var.add(alphaExpr);
                        etmp.substitute(var);
                        res.add(etmp.representation);
                    }

                }

                if (!prove && alphaExpr != null && alphaExpr.representation.equals(expr.representation)) {
                    var.clear();
                    var.add(expr);
                    Expression etmp = new Expression(expr, expr, "->");
                    var.add(etmp);
                    res.add(parser.parse("1->2").substitute(var).representation);
                    res.add(parser.parse("(1->2)->(1->(2->1))->2").substitute(var).representation);
                    res.add(parser.parse("(1->(2->1))->2").substitute(var).representation);
                    res.add(parser.parse("1->(2->1)").substitute(var).representation);
                    prove = true;
                }


                if (!prove) {
                    var.clear();
                    var.add(alphaExpr);

                    String left;
                    for (int i = proved.size() - 2; i >= 0 && !prove; --i) {
                        if (proved.get(i).first == null || proved.get(i).second == null)
                            continue;
                        left = proved.get(i).first.representation;
                        if (proved.get(i).oper.equals("->") && proved.get(i).second.representation.equals(expr.representation)) {
                            for (int j = proved.size() - 1; j >= 0 && !prove; --j) {
                                if (proved.get(j).representation.equals(left)) {
                                    var.add(proved.get(i).first);
                                    var.add(proved.get(i));
                                    var.add(expr);

                                    if (alphaExpr != null) {
                                        res.add(parser.parse("(1->2)->((1->3)->(1->4))").substitute(var).representation);
                                        res.add(parser.parse("((1->3)->(1->4))").substitute(var).representation);
                                    } else {
                                        res.add(expr.representation);
                                    }
                                    prove = true;
                                }
                            }
                        }
                    }

                    if (!prove && expr.oper.equals("->") && expr.second.oper.equals("@")) {// rule for (φ) → ∀x(ψ)
                        for (int i = proved.size() - 1; i >= 0 && !prove; --i) {
                            if (proved.get(i).first == null || proved.get(i).second == null)
                                continue;
                            if (proved.get(i).first.representation.equals(expr.first.representation) &&
                                    proved.get(i).second.representation.equals(expr.second.second.representation)) {
                                if (expr.first.freeVariables.contains(expr.second.first.representation)) { // x is free in φ
                                    errorCode = 2;
                                    errVariable = expr.second.first.representation;
                                    errExpr = expr.first;
                                } else if (alphaExpr != null && alphaExpr.freeVariables.contains(expr.second.first.representation)) {
                                    errorCode = 3;
                                    errVariable = expr.second.first.representation;
                                    errExpr = alphaExpr;
                                } else {
                                    if (alphaExpr != null) {
                                        var.clear();
                                        var.add(alphaExpr);
                                        var.add(expr.first);
                                        var.add(expr.second.second);
                                        for (Expression expression : implToConj) {
                                            Expression tmp = new Expression(expression);
                                            res.add(tmp.substitute(var).representation);
                                        }
                                        res.add(parser.parse("1&2->3").substitute(var).representation);
                                        var.set(2, expr.second);
                                        res.add(parser.parse("1&2->3").substitute(var).representation);
                                        for (Expression expression : conjToImpl) {
                                            Expression tmp = new Expression(expression);
                                            res.add(tmp.substitute(var).representation);
                                        }
                                    }

                                    prove = true;
                                }
                            }
                        }
                    }

                    if (!prove && expr.oper.equals("->") && expr.first.oper.equals("?")) {// rule for ∃x(ψ) → (φ)
                        for (int i = proved.size() - 1; i >= 0 && !prove; --i) {
                            if (proved.get(i).first == null || proved.get(i).second == null)
                                continue;
                            if (proved.get(i).first.representation.equals(expr.first.second.representation) &&
                                    proved.get(i).second.representation.equals(expr.second.representation)) {
                                if (expr.second.freeVariables.contains(expr.first.first.representation)) { // x is free in φ
                                    errorCode = 2;
                                    errVariable = expr.first.first.representation;
                                    errExpr = expr.second;
                                } else if (alphaExpr != null && alphaExpr.freeVariables.contains(expr.first.first.representation)) {
                                    errorCode = 3;
                                    errVariable = expr.second.first.representation;
                                    errExpr = alphaExpr;
                                } else {
                                    if (alphaExpr != null) {
                                        var.clear();
                                        var.add(alphaExpr);
                                        var.add(expr.first.second);
                                        var.add(expr.second);
                                        for (Expression expression : implChange) {
                                            Expression tmp = new Expression(expression);
                                            res.add(tmp.substitute(var).representation);
                                        }
                                        res.add(parser.parse("2->1->3").substitute(var).representation);
                                        var.clear();
                                        var.add(expr.first);
                                        var.add(alphaExpr);
                                        var.add(expr.second);
                                        res.add(parser.parse("1->2->3").substitute(var).representation);
                                        for (Expression expression : implChange) {
                                            Expression tmp = new Expression(expression);
                                            res.add(tmp.substitute(var).representation);
                                        }

                                    }
                                    prove = true;
                                }
                            }
                        }
                    }
                }
                if (!prove) {
                    out.print("Вывод некорректен начиная с формулы номер " + (lineNumber + 1));
                    switch (errorCode) {
                        case 1:
                            out.print(": терм " + errTerm.representation + " не свободен для подстановки в формулу " + errExpr.representation + " вместо переменной " + errVariable + ".");
                            break;
                        case 2:
                            out.print(": переменная " + errVariable + " входит свободно в формулу " + errExpr.representation + ".");
                            break;
                        case 3:
                            out.print(": используется правило с квантором по переменной " + errVariable + ", входящей свободно в допущение " + errExpr.representation + ".");
                            break;
                    }


                    return null;
                }
                if (alphaExpr != null) {
                    Expression etmp = new Expression(alphaExpr, expr, "->");
                    res.add(etmp.representation);
                }

            }
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
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

        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
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

        if (expr.representation.equals("((a=b)->(a'=b'))")) return 13;
        if (expr.representation.equals("((a=b)->((a=c)->(b=c)))")) return 14;
        if (expr.representation.equals("((a'=b')->(a=b))")) return 15;
        if (expr.representation.equals("!(a'=0)")) return 16;
        if (expr.representation.equals("((a+b')=(a+b)')")) return 17;
        if (expr.representation.equals("((a+0)=a)")) return 18;
        if (expr.representation.equals("((a*0)=0)")) return 19;
        if (expr.representation.equals("((a*b')=((a*b)+a))")) return 20;

        // induction (ψ[x := 0])&∀x((ψ) → (ψ)[x := x']) → (ψ)
        try {
            opers = true;
            opers &= expr.oper.equals("->");
            opers &= expr.first.oper.equals("&");
            opers &= expr.first.second.oper.equals("@");
            opers &= expr.first.second.second.oper.equals("->");
            Expression psi0 = expr.first.first;
            Expression psi1 = expr.first.second.second.first;
            Expression psi2 = expr.first.second.second.second;
            Expression psi3 = expr.second;
            String x = expr.first.second.first.representation;
            if (opers && psi1.representation.equals(psi3.representation) && compareOnSubstitution(psi1, psi0, x)
                    && theta.representation.equals("0") && compareOnSubstitution(psi1, psi2, x) && theta.representation.equals(x + "'")) {// check substitution error
                return 21;
            }

        } catch (Exception ignored) {
        }
        // axiom 11 ∀x(ψ)->(ψ[x := θ])
        try {
            lastErrorCode = 0;
            opers = true;
            opers &= expr.oper.equals("->");
            opers &= expr.first.oper.equals("@");
            compareOnSubstitution(expr.first.second, expr.second, expr.first.first.representation);
            if (opers && correct) {// check substitution error
                return 11;
            }
            if (lastErrorCode == 1) {
                errorCode = 1;
                errVariable = expr.first.first.representation;
                errExpr = expr.first.second;
                errTerm = theta;
            }

        } catch (Exception ignored) {
        }
        // axiom 12 (ψ[x := θ]) → ∃x(ψ)
        try {
            lastErrorCode = 0;
            opers = true;
            opers &= expr.oper.equals("->");
            opers &= expr.second.oper.equals("?");
            compareOnSubstitution(expr.second.second, expr.first, expr.second.first.representation);
            if (opers && correct) {// check substitution error
                return 12;
            }
            if (lastErrorCode == 1) {
                errorCode = lastErrorCode;
                errVariable = expr.first.first.representation;
                errExpr = expr.first.second;
                errTerm = theta;
            }

        } catch (Exception ignored) {
        }

        return 0;
    }

    Expression theta = null;
    boolean correct = true;

    boolean compareOnSubstitution(Expression a, Expression b, String x) {
        theta = null;
        correct = true;

        try {
            dfs(a, b, x);
        } catch (Exception e) {
            correct = false;
        }
        if (correct && (theta != null || a.representation.equals(b.representation) )) {
            return true;
        }
        correct = false;
        return false;
    }

    void dfs(Expression a, Expression b, String x) {
        if (!a.freeVariables.contains(x)) {
            return;
        }
        if (a.first != null) {
            if (a.first.freeVariables.contains(x)) {
                dfs(a.first, b.first, x);
            } else if (!a.first.representation.equals(b.first.representation)) {
                throw null;
            }

            if (a.second != null) {
                if (a.second.freeVariables.contains(x)) {
                    dfs(a.second, b.second, x);
                } else if (!a.second.representation.equals(b.second.representation)) {
                    throw null;
                }
            }
            if (a.oper.equals("@") || a.oper.equals("?")) {
                if (!b.freeVariables.containsAll(theta.freeVariables)) {
                    lastErrorCode = 1;
                    throw null;// not free for substitution
                }
            }
        }

        if (a.terms != null) {
            for (int i = 0; i < a.terms.size(); ++i) {
                if (a.terms.get(i).freeVariables.contains(x)) {
                    dfs(a.terms.get(i), b.terms.get(i), x);
                } else if (!a.terms.get(i).representation.equals(b.terms.get(i).representation)) {
                    throw null;
                }

            }
        }

        if (a.oper.equals(x) && a.terms == null) {
            if (theta == null) {
                theta = b;
            } else if (!theta.representation.equals(b.representation)) {
                correct = false;
                throw null;
            }

        }
    }

    public void run() {
        try {
            in = new FastScanner(new File("test.in"));
            out = new PrintWriter(new File("test.out"),"UTF-8");

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

    void initProve() {
        implToConj = parseHelper(new String[]{
                "((1&2)->2)->(((1&2)->(2->3))->((1&2)->3))",
                "(((1&2)->2)->(((1&2)->(2->3))->((1&2)->3)))->((1->(2->3))->(((1&2)->2)->(((1&2)->(2->3))->((1&2)->3))))",
                "(1->(2->3))->(((1&2)->2)->(((1&2)->(2->3))->((1&2)->3)))",
                "(1&2)->2",
                "((1&2)->2)->((1->(2->3))->((1&2)->2))",
                "(1->(2->3))->((1&2)->2)",
                "((1->(2->3))->((1&2)->2))->(((1->(2->3))->(((1&2)->2)->(((1&2)->(2->3))->((1&2)->3))))->((1->(2->3))->(((1&2)->(2->3))->((1&2)->3))))",
                "((1->(2->3))->(((1&2)->2)->(((1&2)->(2->3))->((1&2)->3))))->((1->(2->3))->(((1&2)->(2->3))->((1&2)->3)))",
                "(1->(2->3))->(((1&2)->(2->3))->((1&2)->3))",
                "((1&2)->1)->(((1&2)->(1->(2->3)))->((1&2)->(2->3)))",
                "(((1&2)->1)->(((1&2)->(1->(2->3)))->((1&2)->(2->3))))->((1->(2->3))->(((1&2)->1)->(((1&2)->(1->(2->3)))->((1&2)->(2->3)))))",
                "(1->(2->3))->(((1&2)->1)->(((1&2)->(1->(2->3)))->((1&2)->(2->3))))",
                "(1&2)->1",
                "((1&2)->1)->((1->(2->3))->((1&2)->1))",
                "(1->(2->3))->((1&2)->1)",
                "((1->(2->3))->((1&2)->1))->(((1->(2->3))->(((1&2)->1)->(((1&2)->(1->(2->3)))->((1&2)->(2->3)))))->((1->(2->3))->(((1&2)->(1->(2->3)))->((1&2)->(2->3)))))",
                "((1->(2->3))->(((1&2)->1)->(((1&2)->(1->(2->3)))->((1&2)->(2->3)))))->((1->(2->3))->(((1&2)->(1->(2->3)))->((1&2)->(2->3))))",
                "(1->(2->3))->(((1&2)->(1->(2->3)))->((1&2)->(2->3)))",
                "(1->(2->3))->((1&2)->(1->(2->3)))",
                "((1->(2->3))->((1&2)->(1->(2->3))))->(((1->(2->3))->(((1&2)->(1->(2->3)))->((1&2)->(2->3))))->((1->(2->3))->((1&2)->(2->3))))",
                "((1->(2->3))->(((1&2)->(1->(2->3)))->((1&2)->(2->3))))->((1->(2->3))->((1&2)->(2->3)))",
                "(1->(2->3))->((1&2)->(2->3))",
                "((1->(2->3))->((1&2)->(2->3)))->(((1->(2->3))->(((1&2)->(2->3))->((1&2)->3)))->((1->(2->3))->((1&2)->3)))",
                "((1->(2->3))->(((1&2)->(2->3))->((1&2)->3)))->((1->(2->3))->((1&2)->3))",
                "(1->(2->3))->((1&2)->3)"
        });
        conjToImpl = parseHelper(new String[]{
                "(1->(2->((1&2)->3)))->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3)))",
                "((1->(2->((1&2)->3)))->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3))))->(((1&2)->3)->((1->(2->((1&2)->3)))->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3)))))",
                "((1&2)->3)->((1->(2->((1&2)->3)))->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3))))",
                "(1->((1&2)->3))->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3))))",
                "((1->((1&2)->3))->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3)))))->(((1&2)->3)->((1->((1&2)->3))->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3))))))",
                "((1&2)->3)->((1->((1&2)->3))->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3)))))",
                "((1&2)->3)->(1->((1&2)->3))",
                "(((1&2)->3)->(1->((1&2)->3)))->((((1&2)->3)->((1->((1&2)->3))->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3))))))->(((1&2)->3)->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3))))))",
                "(((1&2)->3)->((1->((1&2)->3))->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3))))))->(((1&2)->3)->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3)))))",
                "((1&2)->3)->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3))))",
                "(((1&2)->3)->(2->((1&2)->3)))->(1->(((1&2)->3)->(2->((1&2)->3))))",
                "((((1&2)->3)->(2->((1&2)->3)))->(1->(((1&2)->3)->(2->((1&2)->3)))))->(((1&2)->3)->((((1&2)->3)->(2->((1&2)->3)))->(1->(((1&2)->3)->(2->((1&2)->3))))))",
                "((1&2)->3)->((((1&2)->3)->(2->((1&2)->3)))->(1->(((1&2)->3)->(2->((1&2)->3)))))",
                "((1&2)->3)->(2->((1&2)->3))",
                "(((1&2)->3)->(2->((1&2)->3)))->(((1&2)->3)->(((1&2)->3)->(2->((1&2)->3))))",
                "((1&2)->3)->(((1&2)->3)->(2->((1&2)->3)))",
                "(((1&2)->3)->(((1&2)->3)->(2->((1&2)->3))))->((((1&2)->3)->((((1&2)->3)->(2->((1&2)->3)))->(1->(((1&2)->3)->(2->((1&2)->3))))))->(((1&2)->3)->(1->(((1&2)->3)->(2->((1&2)->3))))))",
                "(((1&2)->3)->((((1&2)->3)->(2->((1&2)->3)))->(1->(((1&2)->3)->(2->((1&2)->3))))))->(((1&2)->3)->(1->(((1&2)->3)->(2->((1&2)->3)))))",
                "((1&2)->3)->(1->(((1&2)->3)->(2->((1&2)->3))))",
                "(((1&2)->3)->(1->(((1&2)->3)->(2->((1&2)->3)))))->((((1&2)->3)->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3)))))->(((1&2)->3)->(1->(2->((1&2)->3)))))",
                "(((1&2)->3)->((1->(((1&2)->3)->(2->((1&2)->3))))->(1->(2->((1&2)->3)))))->(((1&2)->3)->(1->(2->((1&2)->3))))",
                "((1&2)->3)->(1->(2->((1&2)->3)))",
                "(((1&2)->3)->(1->(2->((1&2)->3))))->((((1&2)->3)->((1->(2->((1&2)->3)))->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3)))))->(((1&2)->3)->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3)))))",
                "(((1&2)->3)->((1->(2->((1&2)->3)))->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3)))))->(((1&2)->3)->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3))))",
                "((1&2)->3)->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3)))",
                "(1->(2->(1&2)))->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3))))",
                "((1->(2->(1&2)))->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3)))))->(((1&2)->3)->((1->(2->(1&2)))->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3))))))",
                "((1&2)->3)->((1->(2->(1&2)))->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3)))))",
                "1->(2->(1&2))",
                "(1->(2->(1&2)))->(((1&2)->3)->(1->(2->(1&2))))",
                "((1&2)->3)->(1->(2->(1&2)))",
                "(((1&2)->3)->(1->(2->(1&2))))->((((1&2)->3)->((1->(2->(1&2)))->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3))))))->(((1&2)->3)->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3))))))",
                "(((1&2)->3)->((1->(2->(1&2)))->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3))))))->(((1&2)->3)->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3)))))",
                "((1&2)->3)->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3))))",
                "((2->(1&2))->((2->((1&2)->3))->(2->3)))->(1->((2->(1&2))->((2->((1&2)->3))->(2->3))))",
                "(((2->(1&2))->((2->((1&2)->3))->(2->3)))->(1->((2->(1&2))->((2->((1&2)->3))->(2->3)))))->(((1&2)->3)->(((2->(1&2))->((2->((1&2)->3))->(2->3)))->(1->((2->(1&2))->((2->((1&2)->3))->(2->3))))))",
                "((1&2)->3)->(((2->(1&2))->((2->((1&2)->3))->(2->3)))->(1->((2->(1&2))->((2->((1&2)->3))->(2->3)))))",
                "(2->(1&2))->((2->((1&2)->3))->(2->3))",
                "((2->(1&2))->((2->((1&2)->3))->(2->3)))->(((1&2)->3)->((2->(1&2))->((2->((1&2)->3))->(2->3))))",
                "((1&2)->3)->((2->(1&2))->((2->((1&2)->3))->(2->3)))",
                "(((1&2)->3)->((2->(1&2))->((2->((1&2)->3))->(2->3))))->((((1&2)->3)->(((2->(1&2))->((2->((1&2)->3))->(2->3)))->(1->((2->(1&2))->((2->((1&2)->3))->(2->3))))))->(((1&2)->3)->(1->((2->(1&2))->((2->((1&2)->3))->(2->3))))))",
                "(((1&2)->3)->(((2->(1&2))->((2->((1&2)->3))->(2->3)))->(1->((2->(1&2))->((2->((1&2)->3))->(2->3))))))->(((1&2)->3)->(1->((2->(1&2))->((2->((1&2)->3))->(2->3)))))",
                "((1&2)->3)->(1->((2->(1&2))->((2->((1&2)->3))->(2->3))))",
                "(((1&2)->3)->(1->((2->(1&2))->((2->((1&2)->3))->(2->3)))))->((((1&2)->3)->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3)))))->(((1&2)->3)->(1->((2->((1&2)->3))->(2->3)))))",
                "(((1&2)->3)->((1->((2->(1&2))->((2->((1&2)->3))->(2->3))))->(1->((2->((1&2)->3))->(2->3)))))->(((1&2)->3)->(1->((2->((1&2)->3))->(2->3))))",
                "((1&2)->3)->(1->((2->((1&2)->3))->(2->3)))",
                "(((1&2)->3)->(1->((2->((1&2)->3))->(2->3))))->((((1&2)->3)->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3))))->(((1&2)->3)->(1->(2->3))))",
                "(((1&2)->3)->((1->((2->((1&2)->3))->(2->3)))->(1->(2->3))))->(((1&2)->3)->(1->(2->3)))",
                "((1&2)->3)->(1->(2->3))"
        });
        implChange = parseHelper(new String[]{
                "(2->(1->(2->3)))->((2->((1->(2->3))->(1->3)))->(2->(1->3)))",
                "((2->(1->(2->3)))->((2->((1->(2->3))->(1->3)))->(2->(1->3))))->((1->(2->3))->((2->(1->(2->3)))->((2->((1->(2->3))->(1->3)))->(2->(1->3)))))",
                "(1->(2->3))->((2->(1->(2->3)))->((2->((1->(2->3))->(1->3)))->(2->(1->3))))",
                "(1->(2->3))->(2->(1->(2->3)))",
                "((1->(2->3))->(2->(1->(2->3))))->(((1->(2->3))->((2->(1->(2->3)))->((2->((1->(2->3))->(1->3)))->(2->(1->3)))))->((1->(2->3))->((2->((1->(2->3))->(1->3)))->(2->(1->3)))))",
                "((1->(2->3))->((2->(1->(2->3)))->((2->((1->(2->3))->(1->3)))->(2->(1->3)))))->((1->(2->3))->((2->((1->(2->3))->(1->3)))->(2->(1->3))))",
                "(1->(2->3))->((2->((1->(2->3))->(1->3)))->(2->(1->3)))",
                "(2->(1->2))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3))))",
                "((2->(1->2))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3)))))->((1->(2->3))->((2->(1->2))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3))))))",
                "(1->(2->3))->((2->(1->2))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3)))))",
                "2->(1->2)",
                "(2->(1->2))->((1->(2->3))->(2->(1->2)))",
                "(1->(2->3))->(2->(1->2))",
                "((1->(2->3))->(2->(1->2)))->(((1->(2->3))->((2->(1->2))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3))))))->((1->(2->3))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3))))))",
                "((1->(2->3))->((2->(1->2))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3))))))->((1->(2->3))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3)))))",
                "(1->(2->3))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3))))",
                "((1->2)->((1->(2->3))->(1->3)))->(2->((1->2)->((1->(2->3))->(1->3))))",
                "(((1->2)->((1->(2->3))->(1->3)))->(2->((1->2)->((1->(2->3))->(1->3)))))->((1->(2->3))->(((1->2)->((1->(2->3))->(1->3)))->(2->((1->2)->((1->(2->3))->(1->3))))))",
                "(1->(2->3))->(((1->2)->((1->(2->3))->(1->3)))->(2->((1->2)->((1->(2->3))->(1->3)))))",
                "(1->2)->((1->(2->3))->(1->3))",
                "((1->2)->((1->(2->3))->(1->3)))->((1->(2->3))->((1->2)->((1->(2->3))->(1->3))))",
                "(1->(2->3))->((1->2)->((1->(2->3))->(1->3)))",
                "((1->(2->3))->((1->2)->((1->(2->3))->(1->3))))->(((1->(2->3))->(((1->2)->((1->(2->3))->(1->3)))->(2->((1->2)->((1->(2->3))->(1->3))))))->((1->(2->3))->(2->((1->2)->((1->(2->3))->(1->3))))))",
                "((1->(2->3))->(((1->2)->((1->(2->3))->(1->3)))->(2->((1->2)->((1->(2->3))->(1->3))))))->((1->(2->3))->(2->((1->2)->((1->(2->3))->(1->3)))))",
                "(1->(2->3))->(2->((1->2)->((1->(2->3))->(1->3))))",
                "((1->(2->3))->(2->((1->2)->((1->(2->3))->(1->3)))))->(((1->(2->3))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3)))))->((1->(2->3))->(2->((1->(2->3))->(1->3)))))",
                "((1->(2->3))->((2->((1->2)->((1->(2->3))->(1->3))))->(2->((1->(2->3))->(1->3)))))->((1->(2->3))->(2->((1->(2->3))->(1->3))))",
                "(1->(2->3))->(2->((1->(2->3))->(1->3)))",
                "((1->(2->3))->(2->((1->(2->3))->(1->3))))->(((1->(2->3))->((2->((1->(2->3))->(1->3)))->(2->(1->3))))->((1->(2->3))->(2->(1->3))))",
                "((1->(2->3))->((2->((1->(2->3))->(1->3)))->(2->(1->3))))->((1->(2->3))->(2->(1->3)))",
                "(1->(2->3))->(2->(1->3))"
        });
    }

}
