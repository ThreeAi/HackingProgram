package edu.program.hackingprogram;

import static edu.program.hackingprogram.VigenereCipher.ALPHABET_SIZE;
import static edu.program.hackingprogram.VigenereCipher.BASE_CHAR;

public class KeyLengthFinder {

    private static double calculateIC(String text) {
        int[] freq = new int[ALPHABET_SIZE];
        int textLength = text.length();
        double ic = 0.0;

        // Подсчёт частоты появления символов
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                freq[Character.toUpperCase(c) - BASE_CHAR]++;
            }
        }

        // Вычисление индекса совпадения
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            ic += freq[i] * (freq[i] - 1);
        }
        ic /= (textLength * (textLength - 1));
        return ic;
    }

    public static int findKeyLength(String text) {
        double expectedIC = 0.064;  // Ожидаемый индекс совпадения для нормального текста на английском
        int maxKeyLength = 20; // Максимальная длина ключа для теста
        double minICDifference = Double.MAX_VALUE;
        int keyLength = 0;

        // Тестирование различных длин ключа
        for (int length = 1; length <= maxKeyLength; length++) {
            StringBuilder[] groups = new StringBuilder[length];
            for (int i = 0; i < length; i++) {
                groups[i] = new StringBuilder();
            }

            // Разделяем текст на группы, соответствующие длине ключа
            for (int i = 0; i < text.length(); i++) {
                if (Character.isLetter(text.charAt(i))) {
                    groups[i % length].append(text.charAt(i));
                }
            }

            // Вычисляем средний индекс совпадения для всех групп
            double avgIC = 0.0;
            for (StringBuilder group : groups) {
                avgIC += calculateIC(group.toString());
            }
            avgIC /= length;

            // Сравниваем средний индекс совпадения с ожидаемым
            double icDifference = Math.abs(avgIC - expectedIC);
            if (icDifference < minICDifference) {
                minICDifference = icDifference;
                keyLength = length;
            }
        }

        return keyLength;
    }
}
