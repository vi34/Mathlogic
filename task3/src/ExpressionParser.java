
public class ExpressionParser {
    Expression parse (String s) {
        s = s.replace(" ", "");
        Expression res = disjunction(s);
        while (res.rest.length() != 0) {
            String oper = res.rest.substring(0,1);
            if(!oper.equals("-")) {
                return res;
            }
            String next = res.rest.substring(2);
            Expression right = parse(next);
            if(!right.inBraces)
            {
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

        }
        if (!res.inBraces) {
            res.representation = "(" + res.representation + ")";
            res.inBraces = true;
        }
        return res;
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
            res.representation = "(" + res.first.representation + "|" + res.second.representation + ")";
            res.inBraces = true;

        }


        return res;
    }

    public Expression conjunction(String s) {
        Expression res = parseBraces(s);

        while (res.rest.length() != 0) {
            String oper = res.rest.substring(0,1);
            if(!oper.equals("&")) {
                return res;
            }
            String next = res.rest.substring(1);
            Expression right = parseBraces(next);

            Expression left = res;
            res = new Expression();
            res.first = left;
            res.second = right;
            res.rest = right.rest;
            res.oper = "&";
            res.representation = "(" + res.first.representation + "&" + res.second.representation + ")";
            res.inBraces = true;

        }

        return res;
    }

    public Expression parseBraces(String s) {
        if(s.charAt(0) == '(') {
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

            Expression res = parse(s.substring(1, i - 1));
            res.rest = s.substring(i);
            if(!res.inBraces) {
                res.representation = "(" + res.representation + ")";
                res.inBraces = true;
            }
            return res;
        }
        return negate(s);
    }

    public Expression negate(String s) {
        if(s.charAt(0) == '!') {
            Expression res = new Expression();
            res.first = parseBraces(s.substring(1));
            res.representation = "!" + res.first.representation;
            res.oper = "!";
            res.rest = res.first.rest;
            res.inBraces = res.first.inBraces;
            return res;
        }
        return term(s);
    }

    public Expression term(String s) {
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