package com.vi34;

import com.vi34.Ordinal.CNFOrdinal;
import com.vi34.Parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by vi34 on 29/10/2016.
 */
public class Main {
    public static final String NOT_EQUAL = "Не равны";
    public static final String EQUAL = "Равны";
    private static Parser parser = new Parser();

    public static void main(String[] args) throws IOException {
        Files.walk(Paths.get("tests"))
                .filter(f -> !Files.isDirectory(f))
                .forEach(f -> {
                    try {
                        String res = compute(Files.readAllLines(f).get(0));
                        String correctAnsw;
                        if (f.getFileName().toString().contains("different")) {
                            correctAnsw = NOT_EQUAL;
                        } else {
                            correctAnsw = EQUAL;
                        }
                        System.out.println(f.getFileName() + " " + res.equals(correctAnsw));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static String compute(String s) {
        String a = s.substring(0, s.indexOf("="));
        String b = s.substring(s.indexOf("=") + 1);
        CNFOrdinal x = parser.parseOrdinal(a).toCNF();
        CNFOrdinal y = parser.parseOrdinal(b).toCNF();
        System.out.println(x);
        System.out.println(y);
        if (x.compareTo(y) == 0) {
            return EQUAL;
        }
        return NOT_EQUAL;
    }
}
