/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private int wordLength = DEFAULT_WORD_LENGTH;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            // Add to list of words.
            wordList.add(word);
            // Add to set of words.
            wordSet.add(word);
            // Add to map from sorted word (key) to word list (value)
            String sorted = sortLetters(word);
            if (lettersToWord.containsKey(sorted)) {
                lettersToWord.get(sorted).add(word);
            } else {
                ArrayList<String> words = new ArrayList<>();
                words.add(word);
                lettersToWord.put(sorted, words);
            }
            // Add to map from word length to word list.
            int length = word.length();
            if (sizeToWords.containsKey(length)) {
                sizeToWords.get(length).add(word);
            } else {
                ArrayList<String> words = new ArrayList<>();
                words.add(word);
                sizeToWords.put(length, words);
            }
        }
        Log.d("AnagramDictionary", "Constructor done");
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String sorted = sortLetters(targetWord);
        if (lettersToWord.containsKey(sorted)) {
            result.addAll(lettersToWord.get(sortLetters(targetWord)));
        }
        /*for (String word : wordList) {
            if (word.length() == targetWord.length()
                    && sortLetters(word).equals(sortLetters(targetWord))) {
                result.add(word);
            }
        }*/
        return result;
    }

    private static String sortLetters(String unsorted) {
        char [] chars = unsorted.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (char c = 'a'; c <= 'z'; c++) {
            result.addAll(getAnagrams(word + c));
        }
        return result;
    }

    public String pickGoodStarterWord() {
        //int randStart = random.nextInt(wordList.size());
        ArrayList<String> wordsOfCertainLength = sizeToWords.get(wordLength);
        if (wordLength != MAX_WORD_LENGTH) {
            wordLength++;
        }
        int randStart = random.nextInt(wordsOfCertainLength.size());
        int wordsChecked = 0;
        for (int i = randStart; wordsChecked < wordsOfCertainLength.size();
             i = (i + 1) % wordsOfCertainLength.size(), wordsChecked++) {
            String word = wordsOfCertainLength.get(i);
            if (getAnagramsWithOneMoreLetter(word).size() > MIN_NUM_ANAGRAMS) {
                return word;
            }
        }
        return "pots";

    }
}
