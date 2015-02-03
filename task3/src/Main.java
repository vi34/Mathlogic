import java.util.*;
import java.io.*;


public class Main {
    FastScanner in;
    PrintWriter out;
    ExpressionParser parser;

    Vector<Expression> implication11_01;
    Vector<Expression> implication10;
    Vector<Expression> implication00;
    Vector<Expression> contraposition;
    Vector<Expression> conjunction00_01;
    Vector<Expression> conjunction10;
    Vector<Expression> conjunction11;
    Vector<Expression> disjunction00;
    Vector<Expression> disjunction01;
    Vector<Expression> disjunction10_11;
    Vector<Expression> negate;

    public void solve() throws IOException {
        String s;
        Expression expr;
        parser = new ExpressionParser();
        s = in.nextLine();
        s = s.replace(" ", "");
        Vector<Expression> answer;
        initProve();
        expr = parser.parse(s);

        answer = assumptionProve(expr);
        if(answer == null)
            return;

        for (Expression anAnswer : answer) {
            out.println(anAnswer.representation);
        }
    }

    Vector<Expression> parseHelper(String[] strings) {
        Vector<Expression> res = new Vector<Expression>();
        for (String string : strings) {
            res.add(parser.parse(string));
        }
        return res;
    }

    public Vector<Expression> getVariables(Expression expression) {
        Set<String> s = new HashSet<String>();
        Vector<Expression> res = new Vector<Expression>();
        search(expression, s, res);

        return res;
    }

    void search(Expression expression, Set<String> s, Vector<Expression> res) {
        if(expression.first != null) {
            search(expression.first, s, res);
            if(expression.second != null) {
                search(expression.second, s, res);
            }
        } else {
            int size = s.size();
            s.add(expression.representation);
            if(s.size() != size) {
                Expression expr = new Expression();
                expr.representation = expression.representation;
                expr.inBraces = true;
                res.add(expr);
            }
        }
    }

    public Vector<Expression> assumptionProve(Expression expression){
        Vector<Expression> res = new Vector<Expression>();
        Vector<Expression> assumption = new Vector<Expression>();
        Vector<Expression> exclude1 = new Vector<Expression>();
        Vector<Expression> exclude2 = new Vector<Expression>();
        Vector<Expression> variables = getVariables(expression);

        int variablesCount = variables.size();
        for(int i = 0; i < (1 << variablesCount); ++i) {
            assumption.clear();
            Vector<Expression> tmpRes = new Vector<Expression>();

            for(int j = 0; j < variablesCount; ++j) {
                if(((1 << j) & i) != 0) {
                    assumption.add(variables.get(j));
                } else {
                    Expression etmp = new Expression();
                    etmp.first = variables.get(j);
                    etmp.oper = "!";
                    etmp.representation = "!" + variables.get(j).representation;
                    etmp.inBraces = true;
                    assumption.add(etmp);
                }
            }
            if(inductionOnStructure(expression,assumption,tmpRes)) {
                //res.addAll(tmpRes);
                res.addAll(deduction(assumption, tmpRes));
                exclude1.add(res.get(res.size() - 1));
            } else {
                out.print("Высказывание ложно при ");
                for(int j = 0; j < variablesCount - 1; ++j) {
                    out.print(variables.get(j).representation + "=");
                    if(((1 << j) & i) != 0)
                        out.print("И, ");
                    else
                        out.print("Л, ");
                }
                out.print(variables.get(variablesCount - 1).representation + "=");
                if(((1 << (variablesCount - 1)) & i) != 0)
                    out.print("И");
                else
                    out.print("Л");

                return null;
            }
        }

        for(int i = variablesCount - 1; i >= 0; --i) {
            Vector<Expression> forSubstitute = new Vector<Expression>();
            forSubstitute.add(variables.get(i));
            Expression etmp1 = parser.parse("1|!1").substitute(forSubstitute);
            forSubstitute.add(etmp1);
            res.add(parser.parse("1->1|!1").substitute(forSubstitute));
            for (Expression aContraposition : contraposition) {
                Expression etmp = new Expression(aContraposition);
                res.add(etmp.substitute(forSubstitute));
            }
            res.add(parser.parse("(!2->!1)").substitute(forSubstitute));
            forSubstitute.clear();
            Expression etmp = new Expression();
            etmp.first = variables.get(i);
            etmp.inBraces = true;
            etmp.oper = "!";
            etmp.representation = "!" + etmp.first.representation;
            forSubstitute.add(etmp);
            forSubstitute.add(etmp1);
            res.add(parser.parse("1->2").substitute(forSubstitute));
            for (Expression aContraposition : contraposition) {
                etmp = new Expression(aContraposition);
                res.add(etmp.substitute(forSubstitute));
            }
            res.add(parser.parse("(!2->!1)").substitute(forSubstitute));
            forSubstitute.clear();

            forSubstitute.add(variables.get(i));
            res.add(parser.parse("(!(1|!1)->!1)->(!(1|!1)->!!1)->(!!(1|!1))").substitute(forSubstitute));
            res.add(parser.parse("(!(1|!1)->!!1)->!!(1|!1)").substitute(forSubstitute));
            res.add(parser.parse("!!(1|!1)").substitute(forSubstitute));
            res.add(parser.parse("!!(1|!1)->(1|!1)").substitute(forSubstitute));
            res.add(parser.parse("1|!1").substitute(forSubstitute));


            for(int j = 0; j < exclude1.size() / 2; j ++) {
                forSubstitute.clear();
                Expression alpha = new Expression(exclude1.get(j).second);          // maybe slow?
                exclude2.add(alpha);
                forSubstitute.add(variables.get(i));
                forSubstitute.add(alpha);
                res.add(parser.parse("(1->2)->(!1->2)->(1|!1)->2").substitute(forSubstitute));
                res.add(parser.parse("(!1->2)->(1|!1->2)").substitute(forSubstitute));
                res.add(parser.parse("1|!1 -> 2").substitute(forSubstitute));
                res.add(parser.parse("2").substitute(forSubstitute));

            }
            Vector<Expression> tmp;
            tmp = exclude1;
            exclude1 = exclude2;
            exclude2 = tmp;
            exclude2.clear();

        }


        return res;
    }

    private boolean inductionOnStructure(Expression expression, Vector<Expression> assumption, Vector<Expression> res) {
        if(expression.first != null)
        {
            boolean f,s;
            Vector<Expression> forSubstitution = new Vector<Expression>();
            forSubstitution.add(expression.first);
            if(expression.representation.equals("!!B")) {
                int a = 5;
            }
            f = inductionOnStructure(expression.first,assumption,res);
            if(expression.second != null) {
                s = inductionOnStructure(expression.second,assumption,res);
                forSubstitution.add(expression.second);
                if(expression.oper.equals("->")) {
                    if(s) {
                        for (Expression anImplication11_01 : implication11_01) {
                            Expression etmp = new Expression(anImplication11_01);
                            res.add(etmp.substitute(forSubstitution));
                        }
                        return true;
                    }
                    if(f) {
                        for (Expression anImplication10 : implication10) {
                            Expression etmp = new Expression(anImplication10);
                            res.add(etmp.substitute(forSubstitution));
                        }
                        return false;
                    }

                    for (Expression anImplication00 : implication00) {
                        Expression etmp = new Expression(anImplication00);
                        res.add(etmp.substitute(forSubstitution));
                    }
                    return true;
                }
                if(expression.oper.equals("&")) {
                    if(!f) {
                        for (Expression aConjunction00_01 : conjunction00_01) {
                            Expression etmp = new Expression(aConjunction00_01);
                            res.add(etmp.substitute(forSubstitution));
                        }
                        return false;
                    }
                    if(!s) {
                        for (Expression aConjunction10 : conjunction10) {
                            Expression etmp = new Expression(aConjunction10);
                            res.add(etmp.substitute(forSubstitution));
                        }
                        return false;
                    }

                    for (Expression aConjunction11 : conjunction11) {
                        Expression etmp = new Expression(aConjunction11);
                        etmp.substitute(forSubstitution);
                        //etmp.representation += "ALLLLA";
                        res.add(etmp);
                    }
                    return true;
                }
                if(expression.oper.equals("|")) {
                    if(f) {
                        for (Expression aDisjunction10_11 : disjunction10_11) {
                            Expression etmp = new Expression(aDisjunction10_11);
                            res.add(etmp.substitute(forSubstitution));
                        }
                        return true;
                    }
                    if(s) {
                        for (Expression aDisjunction01 : disjunction01) {
                            Expression etmp = new Expression(aDisjunction01);
                            res.add(etmp.substitute(forSubstitution));
                        }
                        return true;
                    }

                    for (Expression aDisjunction00 : disjunction00) {
                        Expression etmp = new Expression(aDisjunction00);
                        res.add(etmp.substitute(forSubstitution));
                    }
                    return false;
                }
                return false;
            } else {
                if(!f) {

                    Expression etmp = new Expression(parser.parse("!1"));
                    res.add(etmp.substitute(forSubstitution));
                    return !f;
                }
                for (Expression aNegate : negate) {
                    Expression etmp = new Expression(aNegate);
                    res.add(etmp.substitute(forSubstitution));
                }
                return !f;
            }
        } else {
            for (Expression anAssumption : assumption) {
                if (anAssumption.representation.equals(expression.representation))
                    return true;
            }
            return false;
        }

    }

    public Vector<Expression> deduction(Vector<Expression> alphaExpr, Vector<Expression> proveIn) {
        Vector<Expression> res = null;
        Expression expr;
        Vector<Expression> proved;
        for(int l = 0; l < alphaExpr.size(); ++l) {
            proved = new Vector<Expression>();
            res = new Vector<Expression>();
            for (int k = 0; k < proveIn.size(); ++k) {
                expr = proveIn.elementAt(k);
                proved.add(expr);
                boolean opers;
                boolean exprType = false;
                // axiom 1
                try {
                    String a = expr.first.representation;
                    String b = expr.second.second.representation;
                    if (expr.second.oper.equals("->") && a.equals(b)) {
                        exprType = true;
                    }
                } catch (Exception ignored) {
                }
                // axiom 2  (A->B)->(A->B->C)->(A->C)

                if (!exprType) {
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
                            exprType = true;
                        }

                    } catch (Exception ignored) {
                    }
                }
                //axiom 3 A->B->A&B
                if (!exprType) {
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
                            exprType = true;
                        }

                    } catch (Exception ignored) {
                    }
                }
                //axiom 4,5 A&B->A
                if (!exprType) {
                    try {
                        opers = true;
                        opers &= expr.first.oper.equals("&");
                        opers &= expr.oper.equals("->");
                        String a1 = expr.first.first.representation;
                        String a2 = expr.second.representation;
                        String b = expr.first.second.representation;
                        if (opers && (a1.equals(a2) || b.equals(a2))) {
                            exprType = true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                //axiom 6,7 A->A|B
                if (!exprType) {
                    try {
                        opers = true;
                        opers &= expr.oper.equals("->");
                        opers &= expr.second.oper.equals("|");
                        String a1 = expr.first.representation;
                        String a2 = expr.second.first.representation;
                        String b = expr.second.second.representation;

                        if (opers && (a1.equals(a2) || a1.equals(b))) {
                            exprType = true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                //axiom 8 (A->Q)->(B->Q)->(A|B->Q)
                if (!exprType) {
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
                            exprType = true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                //axiom 9 (A->B)->(A->!B)->!A
                if (!exprType) {
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
                            exprType = true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                // axiom 10
                if (!exprType) {
                    try {
                        String a = expr.first.first.first.representation;
                        String b = expr.second.representation;
                        if (expr.first.oper.equals("!") && expr.first.first.oper.equals("!") && a.equals(b)) {
                            exprType = true;
                        }

                    } catch (Exception ignored) {
                    }
                }
                if(!exprType) {
                    for(int i = l + 1; i < alphaExpr.size(); ++i) {
                        if(alphaExpr.get(i).representation.equals(expr.representation)) {
                            exprType = true;
                            break;
                        }
                    }
                }

                Vector<Expression> var = new Vector<Expression>();
                // expr is axiom
                if (exprType) {
                    res.add(expr);
                    Expression etmp = parser.parse("1->(2->1)");
                    var.add(expr);
                    var.add(alphaExpr.get(l));
                    etmp.substitute(var);
                    res.add(etmp);

                }

                if (!exprType && alphaExpr.get(l).representation.equals(expr.representation)) {
                    var.clear();
                    var.add(expr);
                    Expression etmp = new Expression(expr,expr,"->");
                    var.add(etmp);
                    res.add(parser.parse("1->2").substitute(var));
                    res.add(parser.parse("(1->2)->(1->(2->1))->2").substitute(var));
                    res.add(parser.parse("(1->(2->1))->2").substitute(var));
                    res.add(parser.parse("1->(2->1)").substitute(var));
                    //out.println(2);  //////
                    exprType = true;
                }


                if (!exprType) {
                    var.clear();
                    var.add(alphaExpr.get(l));
                    boolean prove = false;
                    String left;
                    for(int i = proved.size() - 1; i >= 0 && !prove; --i) {
                        if(proved.get(i).first == null || proved.get(i).second == null)
                            continue;
                        left = proved.get(i).first.representation;
                        if(proved.get(i).oper.equals("->") && proved.get(i).second.representation.equals(expr.representation)) {
                            for(int j = proved.size() - 1; j >= 0 && !prove; --j) {
                                if(proved.get(j).representation.equals(left)) {
                                    var.add(proved.get(i).first);
                                    var.add(proved.get(i));
                                    var.add(expr);

                                    res.add(parser.parse("(1->2)->((1->3)->(1->4))").substitute(var));
                                    res.add(parser.parse("((1->3)->(1->4))").substitute(var));
                                    //out.println(3);/////
                                    prove = true;
                                }
                            }
                        }
                    }
                }

                Expression etmp = new Expression(alphaExpr.get(l),expr, "->");
                res.add(etmp);


            }
            proveIn = res;
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
    }

    public static void main(String[] arg) {
        new Main().run();
    }

    void initProve() {
        implication11_01 = parseHelper(new String[] {
                "2",
                "2->1->2",
                "1->2"
        });
        implication10 = parseHelper(new String[] {
                "((1->2)->2)->((1->2)->!2)->!(1->2)",
                "1",
                "1->(1->2)->1",
                "(1->2)->1",
                "(1->2)->(1->2)->(1->2)",
                "((1->2)->(1->2)->(1->2))->((1->2)->((1->2)->(1->2))->(1->2))->((1->2)->(1->2))",
                "((1->2)->((1->2)->(1->2))->(1->2))->((1->2)->(1->2))",
                "(1->2)->((1->2)->(1->2))->(1->2)",
                "((1->2)->(1->2))",
                "((1->2)->1)->((1->2)->(1->2))->((1->2)->2)",
                "((1->2)->(1->2))->((1->2)->2)",
                "(1->2)->2",
                "!2",
                "!2->(1->2)->!2",
                "(1->2)->!2",
                "((1->2)->!2)->!(1->2)",
                "!(1->2)"
        });
        implication00 = parseHelper(new String[]{
                "!1",
                "!1->1->!1",
                "1->!1",
                "!2",
                "!2->1->!2",
                "1->!2",
                "1->1->1",
                "(1->1->1)->(1->(1->1)->1)->1->1",
                "(1->(1->1)->1)->1->1",
                "(1->(1->1)->1)",
                "1->1",
                "(1 -> !2   -> 1)",
                "(1 -> !2 -> 1)->1->(1 -> !2 -> 1)",
                "1->(1 -> !2 -> 1)",
                "(1->1)->(1->1->(!2->1))->1->(!2->1)",
                "(1->1->(!2->1))->1->(!2->1)",
                "1->(!2->1)",
                "(!1 -> !2 -> !1)",
                "(!1 -> !2 -> !1)->1->(!1 -> !2 -> !1)",
                "1->(!1 -> !2 -> !1)",
                "(1->!1)->(1->!1->(!2->!1))->1->(!2->!1)",
                "(1->!1->(!2->!1))->1->(!2->!1)",
                "1->(!2->!1)",
                "((!2 -> 1) -> (!2 -> !1) -> !!2)",
                "((!2 -> 1) -> (!2 -> !1) -> !!2)->1->((!2 -> 1) -> (!2 -> !1) -> !!2)",
                "1->((!2 -> 1) -> (!2 -> !1) -> !!2)",
                "(1->(!2->1))->(1->(!2->1)->((!2 -> !1) -> !!2))->1->((!2 -> !1) -> !!2)",
                "(1->(!2->1)->((!2 -> !1) -> !!2))->1->((!2 -> !1) -> !!2)",
                "1->((!2->!1)->!!2)",
                "(1->(!2->!1))->(1->(!2->!1)->!!2)->1->!!2",
                "(1->(!2->!1)->!!2)->1->!!2",
                "1->!!2",
                "(!!2->2)",
                "(!!2->2)->1->(!!2->2)",
                "1->(!!2->2)",
                "(1->!!2)->(1->!!2->2)->1->2",
                "(1->!!2->2)->1->2",
                "1->2"
        });
        contraposition = parseHelper(new String[] {
                "((1->2))->((1->2))->((1->2))",
                "(((1->2))->((1->2))->((1->2)))->(((1->2))->(((1->2))->((1->2)))->((1->2)))->((1->2))->((1->2))",
                "(((1->2))->(((1->2))->((1->2)))->((1->2)))->((1->2))->((1->2))",
                "(((1->2))->(((1->2))->((1->2)))->((1->2)))",
                "((1->2))->((1->2))",
                "((1->2)->(1->!2)->!1)",
                "((1->2)->(1->!2)->!1)->((1->2))->((1->2)->(1->!2)->!1)",
                "((1->2))->((1->2)->(1->!2)->!1)",
                "(((1->2))->(1->2))->(((1->2))->(1->2)->((1->!2)->!1))->((1->2))->((1->!2)->!1)",
                "(((1->2))->(1->2)->((1->!2)->!1))->((1->2))->((1->!2)->!1)",
                "((1->2))->((1->!2)->!1)",
                "((!2->(1->!2)) ->(!2->(1->!2)->!1)->(!2->!1))",
                "((!2->(1->!2)) ->(!2->(1->!2)->!1)->(!2->!1))->((1->2))->((!2->(1->!2)) ->(!2->(1->!2)->!1)->(!2->!1))",
                "((1->2))->((!2->(1->!2)) ->(!2->(1->!2)->!1)->(!2->!1))",
                "(!2->1->!2)",
                "(!2->1->!2)->((1->2))->(!2->1->!2)",
                "((1->2))->(!2->1->!2)",
                "(((1->2))->(!2->1->!2))->(((1->2))->(!2->1->!2)->((!2->(1->!2)->!1)->(!2->!1)))->((1->2))->((!2->(1->!2)->!1)->(!2->!1))",
                "(((1->2))->(!2->1->!2)->((!2->(1->!2)->!1)->(!2->!1)))->((1->2))->((!2->(1->!2)->!1)->(!2->!1))",
                "((1->2))->((!2->(1->!2)->!1)->(!2->!1))",
                "(((1->!2)->!1)->!2->((1->!2)->!1))",
                "(((1->!2)->!1)->!2->((1->!2)->!1))->((1->2))->(((1->!2)->!1)->!2->((1->!2)->!1))",
                "((1->2))->(((1->!2)->!1)->!2->((1->!2)->!1))",
                "(((1->2))->((1->!2)->!1))->(((1->2))->((1->!2)->!1)->(!2->((1->!2)->!1)))->((1->2))->(!2->((1->!2)->!1))",
                "(((1->2))->((1->!2)->!1)->(!2->((1->!2)->!1)))->((1->2))->(!2->((1->!2)->!1))",
                "((1->2))->(!2->((1->!2)->!1))",
                "(((1->2))->(!2->((1->!2)->!1)))->(((1->2))->(!2->((1->!2)->!1))->(!2->!1))->((1->2))->(!2->!1)",
                "(((1->2))->(!2->((1->!2)->!1))->(!2->!1))->((1->2))->(!2->!1)",
                "((1->2))->(!2->!1)"
        });
        conjunction00_01 = parseHelper(new String[]{
                "!1",
                "(1 & 2 -> 1) -> (1 & 2 -> !1) -> !(1 & 2)",
                "1 & 2 -> 1",
                "(1 & 2 -> !1) -> !(1 & 2)",
                "!1 -> 1 & 2 -> !1",
                "1 & 2 -> !1",
                "!(1 & 2)"
        });
        conjunction10 = parseHelper(new String[]{
                "!2",
                "(1 & 2 -> 2) -> (1 & 2 -> !2) -> !(1 & 2)",
                "1 & 2 -> 2",
                "(1 & 2 -> !2) -> !(1 & 2)",
                "!2 -> 1 & 2 -> !2",
                "1 & 2 -> !2",
                "!(1 & 2)"
        });
        conjunction11 = parseHelper(new String[] {
                "1",
                "2",
                "1 -> 2 -> 1 & 2",
                "2 -> 1 & 2",
                "1 & 2"
        });

        disjunction00 = parseHelper(new String[]{
                "!1",
                "!2",
                "(1|2->1)->(1|2->!1)->!(1|2)",
                "(1->1)->(2->1)->(1|2->1)",
                "1->1->1",
                "(1->1->1)->(1->(1->1)->1)->(1->1)",
                "(1->(1->1)->1)->(1->1)",
                "1->(1->1)->1",
                "1->1",
                "(2->1)->(1|2->1)",
                "!2->2->!2",
                "2->!2",
                "2->2->2",
                "(2->2->2)->(2->(2->2)->2)->2->2",
                "(2->(2->2)->2)->2->2",
                "(2->(2->2)->2)",
                "2->2",
                "(!!1->1)",
                "(!!1->1)->2->(!!1->1)",
                "2->(!!1->1)",
                "((!1->2)->(!1->!2)->!!1)",
                "((!1->2)->(!1->!2)->!!1)->2->((!1->2)->(!1->!2)->!!1)",
                "2->((!1->2)->(!1->!2)->!!1)",
                "(2->!1->2)",
                "(2->!1->2)->2->(2->!1->2)",
                "2->(2->!1->2)",
                "(2->2)->(2->2->(!1->2))->2->(!1->2)",
                "(2->2->(!1->2))->2->(!1->2)",
                "2->(!1->2)",
                "(2->(!1->2))->(2->(!1->2)->((!1->!2)->!!1))->2->((!1->!2)->!!1)",
                "(2->(!1->2)->((!1->!2)->!!1))->2->((!1->!2)->!!1)",
                "2->((!1->!2)->!!1)",
                "(!2->!1->!2)",
                "(!2->!1->!2)->2->(!2->!1->!2)",
                "2->(!2->!1->!2)",
                "(2->!2)->(2->!2->(!1->!2))->2->(!1->!2)",
                "(2->!2->(!1->!2))->2->(!1->!2)",
                "2->(!1->!2)",
                "(2->(!1->!2))->(2->(!1->!2)->!!1)->2->!!1",
                "(2->(!1->!2)->!!1)->2->!!1",
                "2->!!1",
                "(2->!!1)->(2->!!1->1)->2->1",
                "(2->!!1->1)->2->1",
                "2->1",
                "1|2->1",
                "(1|2->!1)->!(1|2)",
                "!1->1|2->!1",
                "1|2->!1",
                "!(1|2)"
        });
        disjunction01 = parseHelper(new String[]{
                "2",
                "2->1 | 2",
                "1 | 2"
        });
        disjunction10_11 = parseHelper(new String[]{
                "1",
                "1->1 | 2",
                "1 | 2"
        });
        negate = parseHelper(new String[]{
                "1",
                "(!1->1)->(!1->!1)->!!1",
                "1->!1->1",
                "!1->1",
                "(!1->!1)->!!1",
                "!1->!1->!1",
                "(!1->!1->!1)->(!1->(!1->!1)->!1)->(!1->!1)",
                "(!1->(!1->!1)->!1)->(!1->!1)",
                "!1->(!1->!1)->!1",
                "!1->!1",
                "!!1"
        });


    }
}
