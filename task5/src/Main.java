import java.util.*;
import java.io.*;


public class Main {
    FastScanner in;
    PrintWriter out;
    ExpressionParser parser;
    Model mainModel;
    Vector<Model> allModels;
    public static final String TEST = "false1.in";

    public void solve() throws IOException {
        String s;
        Expression expr;
        parser = new ExpressionParser();
        allModels = new Vector<Model>();
        s = in.nextLine();
        s = s.replace(" ", "");
        expr = parser.parse(s);

        Vector<World> world_combinations = generateWorlds(getVariables(expr));
        mainModel = new Model(world_combinations.get(0));
        generateModel(mainModel, world_combinations);
        System.out.println(allModels.size());
        if (checkAllModels(expr, 0)) {
            out.println("Формула общезначима");
        } else {
            mainModel.print(out, 0);
        }

    }

    boolean checkAllModels(Expression expr, int index) {
        Model model = allModels.get(index);
        if (model == allModels.lastElement()) {
            model.active = true;
            if (!mainModel.checkExpression(expr)) {
                return false;
            }
            model.active = false;
            return mainModel.checkExpression(expr);
        }

        model.active = false;
        int next = index + 1;
        if (model.subtree != 0) {
            next = index + model.subtree;
        }
        if (!checkAllModels(expr,next))
            return false;

        model.active = true;
        return checkAllModels(expr, index + 1);

    }

    Vector<World> generateWorlds(Vector<Expression> variables) {
        Vector<World> worlds = new Vector<World>();
        for (long i = 0; i < (1 << variables.size()); ++i){
            World world = new World();
            for (int j = 0; j < variables.size(); ++j) {
                if((i & (1 << j)) != 0) {
                    world.forceVariable(variables.get(j));
                }
            }
            worlds.add(world);
        }
        return worlds;
    }

    void generateModel(Model model, Vector<World> worlds) {
        for (World world: worlds) {
            if (model.world.isSubset(world)) {
                model.addChild(world);
                allModels.add(model.children.lastElement());
                generateModel(allModels.lastElement(), worlds);
                model.subtree += model.children.lastElement().subtree + 1;
            }
        }
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

    public void run() {
        try {
            in = new FastScanner(new File(TEST));
            out = new PrintWriter(new File("test.out"),"UTF-8");
            solve();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class FastScanner {
        BufferedReader br;

        FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
}
