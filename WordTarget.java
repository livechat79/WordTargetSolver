/**
 * Class Name:        Target
 *
 * @author:           Thomas McKeesick
 * Creation Date:     Monday, February 16 2015, 02:25 
 * Last Modified:     Monday, February 16 2015, 02:29
 * 
 * Class Description: A Java class that solves the 9 letter "Word-Target"
 *                    puzzle.
 *
 * @version 0.1.4
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;

public class WordTarget {

    private static char CENTRE;
    private static int MIN_LENGTH = 4;
    private static char[] grid = new char[9];
    private static ArrayList<String> dict;
    private static ArrayList<String> results;

    public static void main(String[] args) {

        if( args.length < 2 ) {
            System.err.println("Usage: java Wordsquare <dictionary> <puzzle>");
            System.exit(1);
        }else if( args.length == 3 ) {
            MIN_LENGTH = Integer.parseInt(args[2]);
        }

        solvePuzzle(args[0], args[1]);

        System.exit(0);
    }

    /**
     * Public method to solve a puzzle from a textfile, calls private methods
     * @param dictFilename The dictionary file to load
     * @param puzzleFilename The puzzle file to load
     * @return The ArrayList of solutions
     */
    public static ArrayList<String> solvePuzzle(String dictFilename, 
                                                String puzzleFilename) {
        long startTime = System.currentTimeMillis();

        dict = loadDict(dictFilename);
        grid = loadPuzzle(puzzleFilename);
        results = findStrings(grid);

        System.out.println("WORD GRID:");
        System.out.println(grid[0] + " " + grid[1] + " " + grid[2] + "\n" +
                           grid[3] + " " + grid[4] + " " + grid[5] + "\n" +
                           grid[6] + " " + grid[7] + " " + grid[8] + "\n");
        
        HashSet<String> set = new HashSet<String>();
        set.addAll(results);
        results.clear();
        results.addAll(set);

        Collections.sort(results);

        System.out.println("Found " + results.size() + " results with " +
                           4 + " letters or more");

        for(int i = 4; i <= grid.length; i++) {
            ArrayList<String> tmp = new ArrayList<String>();
            for(String s: results) {
                if(s.length() == i ) {
                    tmp.add(s);
                }
            }
            System.out.println("Found " + tmp.size() + " results with " + i +
                               " letters: ");
            for(String s: tmp) {
                System.out.println(" - " + s);
            }
        }

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + 
                           " milliseconds");
        System.out.println("Memory used: " + 
                    ((runtime.totalMemory() - runtime.freeMemory()) / 1024) + 
                    " kB");
        return results;
    }

    /**
     * Private method to load a puzzle text file into a char array
     * @param filename The name of the file to load
     * @return The char array containing the text file 
     */
    private static char[] loadPuzzle(String filename) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));

            String line;
            int letterNum = 0;
            while( (line = in.readLine()) != null ) {
                String[] row = line.split("\\s");
                for(int i = 0; i < 3; i++) {
                    if( letterNum != 4 ) {
                        grid[letterNum] = Character.toLowerCase(
                                                row[i].charAt(0));
                    } else {
                        grid[letterNum] = Character.toUpperCase(
                                                row[i].charAt(0));
                    }
                    letterNum++;
                }
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + e.getMessage());
            System.exit(1);
        }
        CENTRE = grid[4];
        return grid;
    }

    /**
     * A method that loads a dictionary text file into a tree structure
     * @param filename The dictionary file to load
     * @return The ArrayList containing the dictionary
     */
    private static ArrayList<String> loadDict(String filename) {
        dict = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(filename));
            String word;
            while( (word = in.readLine()) != null ) {
                dict.add(word);
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + filename );
            System.exit(1);
        }
        return dict;
    }

    private static ArrayList<String> findStrings(char[] grid){
        ArrayList<String> tmp = new ArrayList<String>();
        String str = new String(grid);

        tmp = permute(tmp, str, MIN_LENGTH, 
                String.valueOf(CENTRE));
        return tmp;        
    }

    /**
     * Outer function to call the recursive permute function
     * @param words The ArrayList to populate
     * @param str   The string containing the letters to permute, the "centre"
     *              letter is capitalised.
     * @param minLength The minimum amount of letters that a word must contain
     * @param centre The "centre" letter that each word must contain
     * @return The ArrayList of all dictionary words found in str
     */
    private static ArrayList<String> permute(ArrayList<String> words, 
                        String str, int minLength, CharSequence centre) {
        permute("", str, words, minLength, centre);
        return words;
    }

    /**
     * The recursive permute function. Generates every permutation of every
     * length from 0-the length of the string. Checks whether each string is
     * larger than the minimum length supplied, contains the "centre" letter,
     * and is contained in the dictionary. If so, adds it to the ArrayList
     * of strings to be returned.
     */
    private static void permute(String prefix, String str, 
                        ArrayList<String> words, int minLength,
                        CharSequence centre) {
        int length = str.length();
        if(prefix.length() >= minLength && prefix.contains(centre) && 
                Collections.binarySearch(dict, prefix.toLowerCase()) >= 0) {
            words.add(prefix.toLowerCase());
        }
        if( length != 0 ) {
            for(int i = 0; i < length; i++) {
                permute(prefix + str.charAt(i), 
                    str.substring(0, i) + str.substring(i+1, length),
                    words, minLength, centre);
            }
        }
    }
}

