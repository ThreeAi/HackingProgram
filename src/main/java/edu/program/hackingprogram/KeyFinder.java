package edu.program.hackingprogram;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.program.hackingprogram.VigenereCipher.ALPHABET_SIZE;
import static edu.program.hackingprogram.VigenereCipher.decrypt;

public class KeyFinder {

    public static final char[] MOST_POPULAR_LETTERS = {'T', 'E'};

    @FXML
    private static Label label;
    public static Set<String> loadDictionary(String filePath) throws IOException {
        Set<String> dictionary = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String word;
        while ((word = reader.readLine()) != null) {
            dictionary.add(word.toLowerCase());
        }
        reader.close();
        return dictionary;
    }

    // Метод для подбора ключа с использованием словаря
    public static String findKeyUsingFrequencyAnalysis(String encryptedText, int keyLength, Set<String> dictionary, Label lab) throws ExecutionException, InterruptedException {
        label = lab;
        // Создаём ключ заданной длины, который будет перебираться
        StringBuilder[] groups = new StringBuilder[keyLength];
        for (int i = 0; i < keyLength; i++) {
            groups[i] = new StringBuilder();
        }

        // Разделяем текст на группы, соответствующие длине ключа
        for (int i = 0; i < encryptedText.length(); i++) {
            if (Character.isLetter(encryptedText.charAt(i))) {
                groups[i % keyLength].append(encryptedText.charAt(i));
            }
        }

        char[] letterHighFrequency = new char[keyLength];
        for (int i = 0; i < keyLength; i++) {
            int finalI = i;
            letterHighFrequency[i] = (char) IntStream.range(0, toCount(groups[i].toString()).length)
                    .reduce((a, b) -> toCount(groups[finalI].toString())[a] > toCount(groups[finalI].toString())[b] ? a : b)
                    .orElse(-1);
        }

        char[][] setsChars = new char[keyLength][];
        for (int i = 0; i < letterHighFrequency.length; i++) {
            setsChars[i] = new char[MOST_POPULAR_LETTERS.length];
            for (int j = 0; j < MOST_POPULAR_LETTERS.length; j++) {
                int shift = 'A' + letterHighFrequency[i] - (char)MOST_POPULAR_LETTERS[j];
                if(shift >= 0) {
                    setsChars[i][j] = (char)((char)'A' + shift);
                }
                else {
                    setsChars[i][j] = (char)((char)'Z' + shift + 1);
                }
            }
        }

        List<String> setPossibleKeys = generateAllPossibleKeys(setsChars);


        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<KeyMatchResult>> futureList = setPossibleKeys.stream()
                .map(key -> CompletableFuture.supplyAsync(() -> {
                    String decryptedText = decrypt(encryptedText, key);
                    int matchCount = countWordMatches(decryptedText, dictionary);
                    return new KeyMatchResult(key, matchCount);
                }, executor))
                .collect(Collectors.toList());

        String bestKey = "";
        int maxMatches = 0;

        for (CompletableFuture<KeyMatchResult> future : futureList) {
            KeyMatchResult result = future.get();
            if (result.matchCount > maxMatches) {
                maxMatches = result.matchCount;
                bestKey = result.key;
                label.setText("Current best key: " + result.key + " (Matches: " + result.matchCount + ")");
            }
        }

        executor.shutdown();

        return removeRepeatedPatterns(bestKey);


    }

    private static class KeyMatchResult {
        String key;
        int matchCount;

        KeyMatchResult(String key, int matchCount) {
            this.key = key;
            this.matchCount = matchCount;
        }
    }

    public static List<String> generateAllPossibleKeys(char[][] setsChars) {
        List<String> possibleKeys = new ArrayList<>();
        StringBuilder currentKey = new StringBuilder();
        generateKeys(setsChars, 0, currentKey, possibleKeys);
        return possibleKeys;
    }

    // Рекурсивный метод для генерации всех возможных комбинаций ключей
    private static void generateKeys(char[][] setsChars, int depth, StringBuilder currentKey, List<String> possibleKeys) {
        // Если достигли длины ключа, добавляем ключ в список
        if (depth == setsChars.length) {
            possibleKeys.add(currentKey.toString());
            return;
        }

        // Перебираем все символы для текущего уровня глубины
        for (char c : setsChars[depth]) {
            currentKey.append(c); // добавляем текущий символ
            generateKeys(setsChars, depth + 1, currentKey, possibleKeys); // рекурсивно идем на следующий уровень
            currentKey.deleteCharAt(currentKey.length() - 1); // удаляем последний символ для возврата
        }
    }

    // Метод для проверки, содержатся ли слова из словаря в тексте
    private static int countWordMatches(String text, Set<String> dictionary) {
        int matchCount = 0;

        String textUpperCase = text.toUpperCase();

        for (String word : dictionary) {
            int index = 0;

            while ((index = textUpperCase.indexOf(word.toUpperCase(), index)) != -1) {
                matchCount++;
                index += word.length();
            }
        }

        return matchCount;
    }

    // Метод для подсчета каждой буквы
    private static int[] toCount(String text) {
        int[] fileStatistics = new int[ALPHABET_SIZE];
        for (char c : text.toCharArray()) {
            fileStatistics[c - 'A']++;
        }
        return fileStatistics;
    }

    private static String removeRepeatedPatterns(String text) {
        int len = text.length();
        for (int i = 1; i <= len / 2; i++) {
            String pattern = text.substring(0, i);
            // Manually repeat the substring
            StringBuilder repeatedPattern = new StringBuilder();
            int repeats = len / i;
            for (int j = 0; j < repeats; j++) {
                repeatedPattern.append(pattern);
            }
            // Check if the repeated pattern matches the original string
            if (repeatedPattern.toString().equals(text)) {
                return pattern; // Return only the repeated pattern
            }
        }
        return text; // Return the original text if no repeating pattern found
    }

}
