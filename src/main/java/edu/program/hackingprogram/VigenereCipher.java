package edu.program.hackingprogram;

public class VigenereCipher {
    public static final int ALPHABET_SIZE = 26; // Размер английского алфавита
    public static final char BASE_CHAR = 'A';   // Начальный символ для сдвигов

    // Метод для шифрования текста
    public static String encrypt(String text, String key) {
        // Приводим текст к верхнему регистру и удаляем все неалфавитные символы
        String sanitizedText = text.replaceAll("[^A-Za-z]", "").toUpperCase();
        key = key.toUpperCase();
        StringBuilder encryptedText = new StringBuilder();

        // Шифрование
        for (int i = 0; i < sanitizedText.length(); i++) {
            char textChar = sanitizedText.charAt(i);
            char keyChar = key.charAt(i % key.length()); // Используем ключ циклично

            if (Character.isLetter(textChar)) {
                int encryptedChar = (textChar - BASE_CHAR + (keyChar - BASE_CHAR)) % ALPHABET_SIZE;
                encryptedText.append((char) (encryptedChar + BASE_CHAR));
            } else {
                encryptedText.append(textChar);
            }
        }
        return encryptedText.toString();
    }

    // Метод для дешифрования текста
    public static String decrypt(String encryptedText, String key) {
        StringBuilder decryptedText = new StringBuilder();
        key = key.toUpperCase();

        // Дешифрование
        for (int i = 0; i < encryptedText.length(); i++) {
            char encryptedChar = encryptedText.charAt(i);
            char keyChar = key.charAt(i % key.length()); // Используем ключ циклично

            if (Character.isLetter(encryptedChar)) {
                int decryptedChar = (encryptedChar - keyChar + ALPHABET_SIZE) % ALPHABET_SIZE;
                decryptedText.append((char) (decryptedChar + BASE_CHAR));
            } else {
                decryptedText.append(encryptedChar);
            }
        }
        return decryptedText.toString();
    }
}
