import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by vi34 on 05.02.15.
 */
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
