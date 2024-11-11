package edu.program.hackingprogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VigenereCipherBreaker {
    private static final int ALPHABET_SIZE = 26;
    private static final char BASE_CHAR = 'a';
    private static final Map<Character, Double> ENGLISH_LETTER_FREQUENCY = Map.of(
            'a', 8.17, 'b', 1.49, 'c', 2.78, 'd', 4.25, 'e', 12.70,
            'f', 2.23, 'g', 2.02, 'h', 6.09, 'i', 6.97, 'j', 0.15,
            'k', 0.77, 'l', 4.03, 'm', 2.41, 'n', 6.75, 'o', 7.51,
            'p', 1.93, 'q', 0.10, 'r', 5.99, 's', 6.33, 't', 9.06,
            'u', 2.76, 'v', 0.98, 'w', 2.36, 'x', 0.15, 'y', 1.97, 'z', 0.07
    );

    public static void main(String[] args) {
        String encryptedText = "zabcxzabcxzabc";  // Пример шифртекста
        int keyLength = findKeyLengthUsingKasiskiTest(encryptedText);
        String key = breakCipher(encryptedText, keyLength);
        System.out.println("Recovered key: " + key);
        System.out.println("Decrypted text: " + decrypt(encryptedText, key));
    }

    // Шаг 1: Тест Казиски для нахождения длины ключа
    private static int findKeyLengthUsingKasiskiTest(String text) {
        Map<String, List<Integer>> repeatedSequences = new HashMap<>();
        for (int i = 0; i < text.length() - 3; i++) {
            String sequence = text.substring(i, i + 3);
            for (int j = i + 3; j < text.length() - 3; j++) {
                if (text.startsWith(sequence, j)) {
                    repeatedSequences
                            .computeIfAbsent(sequence, k -> new ArrayList<>())
                            .add(j - i);
                }
            }
        }
        List<Integer> distances = repeatedSequences.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return findGCD(distances);
    }

    // Вспомогательная функция для нахождения НОД
    private static int findGCD(List<Integer> numbers) {
        int result = numbers.get(0);
        for (int number : numbers) {
            result = gcd(result, number);
            if (result == 1) return 1;
        }
        return result;
    }

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // Шаг 2: Частотный анализ для взлома шифра
    private static String breakCipher(String text, int keyLength) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keyLength; i++) {
            String periodGroup = getPeriodGroup(text, i, keyLength);
            char keyChar = findBestShift(periodGroup);
            key.append(keyChar);
        }
        return key.toString();
    }

    // Получение группы периода для частотного анализа
    private static String getPeriodGroup(String text, int start, int keyLength) {
        StringBuilder group = new StringBuilder();
        for (int i = start; i < text.length(); i += keyLength) {
            group.append(text.charAt(i));
        }
        return group.toString();
    }

    // Нахождение лучшего сдвига для группы периода, используя частотный анализ
    private static char findBestShift(String group) {
        double minChiSquare = Double.MAX_VALUE;
        char bestShift = 'a';

        for (char shift = 'a'; shift <= 'z'; shift++) {
            double chiSquare = calculateChiSquare(shiftGroup(group, shift));
            if (chiSquare < minChiSquare) {
                minChiSquare = chiSquare;
                bestShift = shift;
            }
        }
        return bestShift;
    }

    // Расчет хи-квадрат для соответствия частотному распределению английского текста
    private static double calculateChiSquare(String shiftedGroup) {
        Map<Character, Long> frequencies = shiftedGroup.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        double chiSquare = 0.0;
        for (char letter = 'a'; letter <= 'z'; letter++) {
            double observed = frequencies.getOrDefault(letter, 0L);
            double expected = ENGLISH_LETTER_FREQUENCY.getOrDefault(letter, 0.0) / 100.0 * shiftedGroup.length();
            chiSquare += Math.pow(observed - expected, 2) / expected;
        }
        return chiSquare;
    }

    // Сдвиг группы на определенное количество символов
    private static String shiftGroup(String group, char shift) {
        StringBuilder shifted = new StringBuilder();
        int shiftAmount = shift - BASE_CHAR;
        for (char c : group.toCharArray()) {
            shifted.append((char) ((c - BASE_CHAR - shiftAmount + ALPHABET_SIZE) % ALPHABET_SIZE + BASE_CHAR));
        }
        return shifted.toString();
    }

    // Шаг 3: Дешифрование текста с найденным ключом
    private static String decrypt(String text, String key) {
        StringBuilder decryptedText = new StringBuilder();
        String extendedKey = extendKey(text, key);

        for (int i = 0; i < text.length(); i++) {
            int decryptedChar = (text.charAt(i) - BASE_CHAR - (extendedKey.charAt(i) - BASE_CHAR) + ALPHABET_SIZE) % ALPHABET_SIZE;
            decryptedText.append((char) (decryptedChar + BASE_CHAR));
        }
        return decryptedText.toString();
    }

    // Расширение ключа до длины шифртекста
    private static String extendKey(String text, String key) {
        StringBuilder extendedKey = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            extendedKey.append(key.charAt(i % key.length()));
        }
        return extendedKey.toString();
    }
}
