import java.util.ArrayList;

public class ExpressionParser {
    Expression parse (String s) {
        try {
            s = s.replace(" ", "");
            Expression res = disjunction(s);
            while (res.rest.length() != 0) {
                String oper = res.rest.substring(0,1);
                if(!oper.equals("-")) {
                    return res;
                }
                String next = res.rest.substring(2);
                Expression right = parse(next);
                if(!right.inBraces) {
                    right.representation = "(" + right.representation + ")";
                    right.inBraces = true;
                }

                Expression left = res;
                res = new Expression();
                res.first = left;
                res.second = right;
                res.rest = right.rest;
                res.oper = "->";
                res.representation = res.first.representation + "->" + res.second.representation;
                res.freeVariables.addAll(res.first.freeVariables);
                res.freeVariables.addAll(res.second.freeVariables);

            }
            if (!res.inBraces) {
                res.representation = "(" + res.representation + ")";
                res.inBraces = true;
            }
            return res;
        } catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Expression disjunction(String s) {
        Expression res = conjunction(s);
        while (res.rest.length() != 0) {
            String oper = res.rest.substring(0,1);
            if(!oper.equals("|")) {
                return res;
            }
            String next = res.rest.substring(1);
            Expression right = conjunction(next);

            Expression left = res;
            res = new Expression();
            res.first = left;
            res.second = right;
            res.rest = right.rest;
            res.oper = "|";
            res.freeVariables.addAll(res.first.freeVariables);
            res.freeVariables.addAll(res.second.freeVariables);
            res.representation = "(" + res.first.representation + "|" + res.second.representation + ")";
            res.inBraces = true;

        }


        return res;
    }

    public Expression conjunction(String s) {
        Expression res = unary(s);

        while (res.rest.length() != 0) {
            String oper = res.rest.substring(0,1);
            if(!oper.equals("&")) {
                return res;
            }
            String next = res.rest.substring(1);
            Expression right = unary(next);

            Expression left = res;
            res = new Expression();
            res.first = left;
            res.second = right;
            res.rest = right.rest;
            res.oper = "&";
            res.freeVariables.addAll(res.first.freeVariables);
            res.freeVariables.addAll(res.second.freeVariables);
            res.representation = "(" + res.first.representation + "&" + res.second.representation + ")";
            res.inBraces = true;

        }
        return res;
    }


    public Expression unary(String s) {
        if(s.charAt(0) == '!') {
            Expression res = new Expression();
            res.first = unary(s.substring(1));
            res.representation = "!" + res.first.representation;
            res.oper = "!";
            res.rest = res.first.rest;
            res.freeVariables = res.first.freeVariables;
            res.inBraces = res.first.inBraces;
            return res;
        } else if(s.charAt(0) == '@' || s.charAt(0) == '?') {
            Expression res = new Expression();
            int i = 2;
            while(i < s.length() && s.charAt(i) > 47 && s.charAt(i) < 58)
            {
                i++;
            }
            res.oper = Character.toString(s.charAt(0));
            res.first = variable(s.substring(1));
            res.second = unary(res.first.rest);
            res.representation = "(" + res.oper + res.first.representation + res.second.representation + ")";
            res.inBraces = true;
            res.rest = res.second.rest;
            res.freeVariables.addAll(res.second.freeVariables);
            res.freeVariables.remove(res.first.representation);
            return res;
        } else if(s.charAt(0) == '(') {
            int i = 1;
            int countBrace = 1;
            for(;countBrace != 0;i++) {
                if(s.charAt(i) == '(') {
                    countBrace++;
                }
                if(s.charAt(i) == ')') {
                    countBrace--;
                }
            }
            int j = i;          // need to guess term = term or (expression) ?
            while(j < s.length()) {
                if(s.charAt(j) == '(') {
                    countBrace++;
                }
                if(s.charAt(j) == ')') {
                    countBrace--;
                }
                Character c =s.charAt(j);
                if(c != '+' && c != '*' && c != '\'' && c != '(' && c != ')' && !(c > 47 && c < 58) && !(c >= 'a' && c <= 'z') && c != ',' && c != '=') {
                    break;
                }
                if(s.charAt(j) == '=' && countBrace == 0)
                    return predicate(s);
                ++j;
            }
            String test = s.substring(i);
            Expression res = parse(s.substring(1, i - 1));
            res.rest = s.substring(i);
            if(!res.inBraces) {
                res.representation = "(" + res.representation + ")";
                res.inBraces = true;
            }
            return res;
        }
        return predicate(s);
    }

    public Expression predicate(String s) {
        Expression res = new Expression();                      // for schemes
        if(s.charAt(0) >= 'A' && s.charAt(0) <= 'Z' || (s.charAt(0) > 48 && s.charAt(0) < 58)) {
            int i = 1;
            while(i < s.length() && s.charAt(i) > 47 && s.charAt(i) < 58)
            {
                i++;
            }
            res.oper = s.substring(0,i);
            res.representation = res.oper;
            res.rest = s.substring(i);
            if(i < s.length() && s.charAt(i) == '(') {
                res.terms = new ArrayList<Expression>();
               do {
                   res.terms.add(term(res.rest.substring(1)));
                   res.freeVariables.addAll(res.terms.get(res.terms.size() - 1).freeVariables);
                   res.rest = res.terms.get(res.terms.size() - 1).rest;
                }  while(res.rest != null && (res.rest.charAt(0) == ','));
                res.rest = res.rest.substring(1);
                res.representation += "(";
                for(int j = 0; j < res.terms.size() - 1; ++j) {
                    res.representation += res.terms.get(j).representation + ",";

                }
                res.representation += res.terms.get(res.terms.size() - 1).representation + ")";
            }
            res.inBraces = true;
            return res;
        }
        res.first = term(s);
        res.oper = "=";
        res.second = term(res.first.rest.substring(1));
        res.rest = res.second.rest;
        res.freeVariables.addAll(res.first.freeVariables);
        res.freeVariables.addAll(res.second.freeVariables);
        res.representation = "(" + res.first.representation + res.oper + res.second.representation + ")";
        res.inBraces = true;
        return res;
    }

    public Expression term(String s) {
        Expression res = summand(s);
        while (res.rest.length() != 0) {
            String oper = res.rest.substring(0,1);
            if(!oper.equals("+")) {
                return res;
            }
            String next = res.rest.substring(1);
            Expression right = summand(next);
            Expression left = res;
            res = new Expression();
            res.first = left;
            res.second = right;
            res.rest = right.rest;
            res.oper = "+";
            res.freeVariables.addAll(res.first.freeVariables);
            res.freeVariables.addAll(res.second.freeVariables);
            res.representation = "(" + res.first.representation + "+" + res.second.representation + ")";
            res.inBraces = true;
        }
        return res;
    }
    public Expression summand(String s) {
        Expression res = multiplier(s);
        while (res.rest.length() != 0) {
            String oper = res.rest.substring(0,1);
            if(!oper.equals("*")) {
                return res;
            }
            String next = res.rest.substring(1);
            Expression right = multiplier(next);
            Expression left = res;
            res = new Expression();
            res.first = left;
            res.second = right;
            res.rest = right.rest;
            res.oper = "*";
            res.freeVariables.addAll(res.first.freeVariables);
            res.freeVariables.addAll(res.second.freeVariables);
            res.representation = "(" + res.first.representation + "*" + res.second.representation + ")";
            res.inBraces = true;

        }
        return res;
    }
    public Expression multiplier(String s) {
        Expression res = new Expression();

        if(s.charAt(0) == '0') {
            res.representation = "0";
            res.rest = s.substring(1);
            res.inBraces = true;
        } else if (s.charAt(0) == '(') {
            res = term(s.substring(1));
            res.rest = res.rest.substring(1);
            if(!res.inBraces) {
                res.inBraces = true;

                res.representation = "(" + res.representation + ")";
            }
        } else { // variable or function
            int i = 1;
            while(i < s.length() && s.charAt(i) > 47 && s.charAt(i) < 58)
            {
                i++;
            }

            res.representation = s.substring(0,i);
            res.oper = res.representation;// f
            res.rest = s.substring(i);
            res.inBraces = true;
            if(i < s.length() && s.charAt(i) == '(') {

                res.terms = new ArrayList<Expression>();
                do {
                    res.terms.add(term(res.rest.substring(1)));
                    res.freeVariables.addAll(res.terms.get(res.terms.size() - 1).freeVariables);
                    res.rest = res.terms.get(res.terms.size() - 1).rest;
                }  while(res.rest != null && (res.rest.charAt(0) == ','));
                res.rest = res.rest.substring(1);
                res.representation += "(";
                for(int j = 0; j < res.terms.size() - 1; ++j) {
                    res.representation += res.terms.get(j).representation + ",";
                }
                res.representation += res.terms.get(res.terms.size() - 1).representation + ")";
            } else {
                res.freeVariables.add(res.representation);
            }
        }
        int i = 0;
        while(i < res.rest.length() && res.rest.charAt(i) == '\'') {
            ++i;
            Expression tmp = new Expression();
            tmp.first = res;
            tmp.inBraces = true;
            tmp.rest = res.rest;
            tmp.representation = res.representation + "'";
            tmp.freeVariables = res.freeVariables;
            tmp.oper = "'";
            res = tmp;
        }
        res.rest = res.rest.substring(i);
        return res;
    }

    public Expression variable(String s) {
        Expression res = new Expression();
        int i = 1;
        while(i < s.length() && s.charAt(i) > 47 && s.charAt(i) < 58)
        {
            i++;
        }
        res.representation = s.substring(0,i);
        res.rest = s.substring(i);
        res.inBraces = true;
        return res;
    }
}
