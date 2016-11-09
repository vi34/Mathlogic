import java.io.*;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class Main {
    public static final String TEST = "false7.in";
    FastScanner in;
    PrintWriter out;
    PrintWriter sout = new PrintWriter(System.out);
    ExpressionParser parser;
    Model mainModel;
    Vector<Model> allModels;
    long allModelsCount = 0;


    public void solve() throws IOException {
        String s;
        Expression expr;
        parser = new ExpressionParser();
        allModels = new Vector<>();
        s = in.nextLine();
        s = s.replace(" ", "");
        expr = parser.parse(s);

        Vector<Expression> variables = getVariables(expr);
        Set<World> world_combinations = generateWorlds(variables);
        World world = new World();
        mainModel = new Model(world);
        generateModel(mainModel, world_combinations, variables.size());
        mainModel.print(sout, 0);
        sout.flush();
        System.out.println("Worlds count in full tree: " + allModels.size());
        System.out.println("Models count estimate: " + (modelsCountEstimate(mainModel) -1));
        if (checkAllModels(expr, 0)) {
            out.println("Формула общезначима");
        } else {
            mainModel.print(out, 0);
        }
        System.out.println("Models count: " + allModelsCount);

    }

    long modelsCountEstimate(Model model) {
        long estimate;
        if (model.children.size() != 0) {
            estimate = 1;
            for (Model child : model.children) {
                estimate *= modelsCountEstimate(child);
            }
            estimate++;
        } else {
            estimate = 2;
        }
        return estimate;
    }

    boolean checkAllModels(Expression expr, int index) {
        Model model = allModels.get(index);
        if (model == allModels.lastElement()) {
            model.setActive(true);
                //out.println("----"); //debug
                //mainModel.print(out, 0);    // debug
            allModelsCount += 2;
            if (allModelsCount % 100_000 == 0) {
                System.out.println("Models checked: " + allModelsCount);
            }
            if (!mainModel.checkExpression(expr)) {
                return false;
            }
            model.setActive(false);
                //out.println("----"); //debug
                //mainModel.print(out, 0);    // debug
            return mainModel.checkExpression(expr);
        }

        model.setActive(false);
        int next = index + 1;
        if (model.subtree != 0) {
            next = index + 1 + model.subtree;
        }
        if (!checkAllModels(expr,next))
            return false;

        model.setActive(true);
        return checkAllModels(expr, index + 1);

    }

    Set<World> generateWorlds(Vector<Expression> variables) {
        Set<World> worlds = new HashSet<>();
        for (long i = 0; i < (1 << variables.size()); ++i){
            World world = new World();
            for (int j = 0; j < variables.size(); ++j) {
                if((i & (1 << j)) != 0) {
                    world.forceVariable(variables.get(j));
                }
            }
            worlds.add(world);
        }

      /*  Set<World> resWorlds = new HashSet<>();
        for (World world: worlds) {
            for (long[] i = {0}; i[0] < (1 << world.variables.size()); ++i[0]) {
                World world1 = new World();
                int[] j = {0};
                world.variables.forEach(v -> {
                    if ((i[0] & (1 << j[0])) != 0) {
                        world1.forceVariable(v);
                    }
                    j[0]++;
                });
                resWorlds.add(world1);
            }
        }*/

        return worlds;
    }

    void generateModel(Model model, Set<World> worlds, int maxSize) {
        for (World world: worlds) {
            boolean emptyWorld = world.variables.size() == 0 && model == mainModel;
            if (model.world.isSubset(world) && world.variables.size() <= maxSize ||
                    model.world.variables.equals(world.variables) && world.variables.size() < maxSize - 1) {
                model.addChild(world);
                allModels.add(model.children.lastElement());
                if (!model.world.variables.equals(world.variables)) {
                    generateModel(allModels.lastElement(), worlds, maxSize);
                } else {
                    generateModel(allModels.lastElement(), worlds, maxSize - 1);
                }

                model.subtree += model.children.lastElement().subtree + 1;
            }
        }
    }

    public Vector<Expression> getVariables(Expression expression) {
        Set<String> s = new HashSet<String>();
        Vector<Expression> res = new Vector<>();
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
