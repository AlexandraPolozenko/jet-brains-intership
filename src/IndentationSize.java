import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;


/**
 * @author Alexandra Polozenko
 * <p>
 * Class is used for counting indentation in file with java source code. I expect that code was somehow formated,
 * because of that I'm trying to find indentations from lines with close brackets, or with the beginning of a cycle,
 * or with the beginning of an "if" (they are usually first in line).
 * If not only one symbol was used for indentation or somewhere code is not well-formated I'm trying to find the most commonly used one.
 * Also I expect that size of indentations with the similar symbols is always the same.
 */
public class IndentationSize {
    /**
     * Key in this map is possible element used for indentation, first element in value is how many times this char was used for indentation,
     * second element in value is minimal size of indentation (searched size).
     */
    private HashMap<Character, Pair<Integer, Integer>> amountOfChars;

    /**
     * Key in this map is similar to the first alement in value in amountOfChars map, value is similar to key in amountOfChars map
     */
    private HashMap<Integer, Character> charFromAmount;

    /**
     * Used for finding the most common symbol for indentation
     */
    private int maxAmount;

    /**
     * Check if arguments are valid and starts the main process.
     *
     * @param args arguments from command line
     */
    public static void main(String[] args) {
        IndentationSize indentationSize = new IndentationSize();

        /*if (args == null || args.length != 1 || args[0] == null) {
            System.err.println("Invalid arguments");
        } else {
            indentationSize.find(args[0]);
        }*/
        indentationSize.find("src/input1.txt");
    }


    /**
     * The main process for finding indentation. Opens input file, reads lines by lines while they're exist,
     * catches some exceptions, and finds the most useful symbol for indentation. Writes answer in file and in command line.
     *
     * @param path path for a file with java source code where indentation symbol must be found.
     */
    private void find(String path) {
        amountOfChars = new HashMap<>();
        charFromAmount = new HashMap<>();
        maxAmount = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("result.txt"), StandardCharsets.UTF_8)) {
                String s;

                try {
                    while ((s = reader.readLine()) != null && s.length() > 0) {
                        findCloseBracket(s);
                    }

                    char c = charFromAmount.get(maxAmount);
                    s = nameOfSymbol(c) + " " + amountOfChars.get(c).getValue();

                    writer.write(s);
                    System.out.println(s);

                } catch (IOException e) {
                    System.err.println("Unable to read the line");
                }

            } catch (IOException e) {
                System.err.println("Output file error");
            }

        } catch (NoSuchFileException e) {
            System.err.println("Input file doesn't exist");
        } catch (InvalidPathException e) {
            System.err.println("Invalid input file path");
        } catch (IOException | SecurityException e) {
            System.err.println("Input file error");
        }
    }

    /**
     * Function which processing strings. Check if we can find close bracket, cycle or if, fills maps with the size of found
     * indentation and how often it was used;
     *
     * @param s line from a file with java source code.
     */
    private void findCloseBracket(String s) {
        String[] s1;

        if (s.contains("}")) {
            s1 = s.split("}");
        } else if (s.contains("if")) {
            s1 = s.split("if");
        } else if (s.contains("for")) {
            s1 = s.split("for");
        } else {
            return;
        }

        int len = s1[0].length(), amount;
        char c = s1[0].charAt(0);

        if (!amountOfChars.containsKey(c)) {
            amount = 1;
            amountOfChars.put(c, new Pair<>(amount, len));
            charFromAmount.put(amount, c);
        } else {
            amount = amountOfChars.get(c).getKey() + 1;
            if (len > amountOfChars.get(c).getValue()) {
                len = amountOfChars.get(c).getValue();
            }

            amountOfChars.remove(c);
            amountOfChars.put(c, new Pair<>(amount, len));

            charFromAmount.remove(amount - 1);
            charFromAmount.put(amount, c);
        }

        if (amount > maxAmount) {
            maxAmount = amount;
        }
    }


    /**
     * Function returns a name of the symbol used for indentation (This symbols came to my mind first)
     *
     * @param c the most common symbol for indentations.
     * @return name of a symbol c.
     */
    private String nameOfSymbol(char c) {
        if (c == ' ') {
            return "Space";
        }
        if (c == '\t') {
            return "Tabulation";
        }
        if (c == '.') {
            return "Dot";
        }
        if (c == '_') {
            return "Low line";
        } else {
            return "Unknown symbol";
        }
    }
}
