/******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 Thomas Mckeesick
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 * 
 *******************************************************************************
 *
 * Class Name:        WordTarget
 *
 * @author:           Thomas McKeesick
 * Creation Date:     Monday, February 16 2015, 02:25
 * Last Modified:     Thursday, March 05 2015, 16:14
 *
 * Class Description: A Java class that solves the 9 letter "Word-Target"
 *                    puzzle.
 *
 * @version 0.2.5     Now includes "possible.txt" file, ignores impossible
 *                    starting strings when permuting. Results in checking
 *                    ~150 000 strings instead of ~980 000
 */

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;

public class WordTarget {

    private static int MIN_LENGTH = 4;
    private static int count = 0;
    private static List<String> dict;
    private static List<String> possArr;

    public static void main(String[] args) {
        if( args.length < 3 ) {
        System.err.println(
            "Usage: java Wordsquare <puzzle> <dictionary> <strFile>");
                    System.exit(1);
        } else if( args.length == 4 ) {
            MIN_LENGTH = Integer.parseInt(args[3]);
        }

        long startTime = System.currentTimeMillis();

        char[] grid = loadPuzzle(args[0]);
        loadDict(args[1]);
        loadStrFile(args[2]);

        List<String> results = findStrings(grid);

        System.out.println("WORD GRID:");
        System.out.println(grid[0] + " " + grid[1] + " " + grid[2] + "\n" +
                           grid[3] + " " + grid[4] + " " + grid[5] + "\n" +
                           grid[6] + " " + grid[7] + " " + grid[8] + "\n");

        //Removes duplicates from array, then adds back into original array
        HashSet<String> set = new HashSet<String>();
        set.addAll(results);
        results.clear();
        results.addAll(set);

        Collections.sort(results);

        System.out.println("Found " + results.size() + " results with " +
                           MIN_LENGTH + " letters or more");

        for(int i = MIN_LENGTH; i <= grid.length; i++) {
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

        Runtime r = Runtime.getRuntime();
        r.gc();

        long endTime = System.currentTimeMillis();
        System.out.println("\n\n\nTime taken: " + (endTime - startTime) +
                           " milliseconds");
        System.out.println("Memory used: " +
                    ((r.totalMemory() - r.freeMemory())/1024) + " kB");
        System.out.println("Strings produced: " + count);
        System.exit(0);
    }

    /**
     * Private method to load a puzzle text file into a char array
     * @param filename The name of the file to load
     * @return The char array containing the text file, with the "centre" letter
     *         in upper case to avoid finding duplicate letters when searching
     *         in the permute method.
     */
    private static char[] loadPuzzle(String filename) {
        char[] grid = new char[9];
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));

            String line;
            int n = 0;
            while( (line = in.readLine()) != null ) {
                String[] row = line.split("\\s");
                for(int i = 0; i < 3; i++) {
                    if( n != MIN_LENGTH ) {
                        grid[n] = Character.toLowerCase(
                                                row[i].charAt(0));
                    } else {
                        grid[n] = Character.toUpperCase(
                                                row[i].charAt(0));
                    }
                    n++;
                }
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + filename +
                               "Error message: " + e.getMessage() +
                               e.getStackTrace());
            System.exit(1);
        }
        return grid;
    }

    /**
     * A method that loads a dictionary text file into a tree structure
     * @param filename The dictionary file to load
     * @return The ArrayList containing the dictionary
     */
    private static void loadDict(String filename) {
        dict = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(filename));
            String word;
            while( (word = in.readLine()) != null ) {
                dict.add(word);
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + filename +
                               "Error message: " + e.getMessage() +
                               e.getStackTrace());
            System.exit(1);
        }
    }

    /**
     * A method that loads a dictionary text file into a tree structure
     * @param filename The dictionary file to load
     * @return The ArrayList containing the dictionary
     */
    private static void loadStrFile(String filename) {
        possArr = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(filename));
            String word;
            while( (word = in.readLine()) != null ) {
                possArr.add(word);
            }
        } catch( IOException e ) {
            System.err.println("A file error occurred: " + filename +
                               "Error message: " + e.getMessage() +
                               e.getStackTrace());
            System.exit(1);
        }
    }

    /**
     * Private method to call the permute function, provides a List to
     * populate and the "centre" character.
     * @param grid The puzzle grid to solve
     * @return The List containing the words found in the puzzle
     */
    private static List<String> findStrings(char[] grid){
        List<String> tmp = new ArrayList<String>();
        String str = new String(grid);
        char centre = grid[4];
        tmp = permute(tmp, str, String.valueOf(centre));
        return tmp;
    }

    /**
     * Outer function to call the recursive permute function
     * @param words The ArrayList to populate
     * @param str   The string containing the letters to permute, the "centre"
     *              letter is capitalised.
     * @param centre The "centre" letter that each word must contain
     * @return The ArrayList of all dictionary words found in str
     */
    private static List<String> permute(List<String> words, String str,
                                        CharSequence centre) {
        permute("", str, words, centre);
        return words;
    }

    /**
     * The recursive permute function. Generates every permutation of every
     * length from 0-the length of the string. Checks whether each string is
     * larger than the minimum length supplied, contains the "centre" letter,
     * and is contained in the dictionary. If so, adds it to the ArrayList
     * of strings to be returned.
     */
    private static void permute(String prefix, String str, List<String> words,
                                CharSequence centre) {
        count++;
        int length = str.length();
        String lowPre = prefix.toLowerCase();

        if(prefix.length() == 4 || prefix.length() == 3) {
            if(Collections.binarySearch(possArr, lowPre) <= 0) {
                return;
            }
        }
        if(prefix.length() >= MIN_LENGTH && prefix.contains(centre) &&
                Collections.binarySearch(dict, lowPre) >= 0) {
            words.add(lowPre);
        }
        if( length != 0 ) {
            for(int i = 0; i < length; i++) {
                permute(prefix + str.charAt(i),
                    str.substring(0, i) + str.substring(i+1, length),
                    words, centre);
            }
        }
    }
}
