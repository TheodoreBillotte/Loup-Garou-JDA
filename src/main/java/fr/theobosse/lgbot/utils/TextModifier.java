package fr.theobosse.lgbot.utils;

import java.util.ArrayList;
import java.util.List;

public class TextModifier {
    private final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String[] alphabetList = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public String getCustomTypoASCII(String words, Typos typo) {
        StringBuilder sb = new StringBuilder();
        List<String> chars = new ArrayList<>();
        for (char chr : typo.getString().toCharArray()) chars.add(String.valueOf(chr));
        for (char c : words.toCharArray()) {
            int letter = alphabet.indexOf(c);
            if (alphabet.contains(String.valueOf(c)))
                sb.append(chars.get(letter));
            else sb.append(c);
        }
        return sb.toString();
    }

    public String getTitleASCII(String words) {
        words = words.replaceAll("a", "𝕒");
        words = words.replaceAll("A", "𝔸");
        words = words.replaceAll("b", "𝕓");
        words = words.replaceAll("B", "𝔹");
        words = words.replaceAll("c", "𝕔");
        words = words.replaceAll("C", "ℂ");
        words = words.replaceAll("d", "𝕕");
        words = words.replaceAll("D", "𝔻");
        words = words.replaceAll("e", "𝕖");
        words = words.replaceAll("E", "𝔼");
        words = words.replaceAll("f", "𝕗");
        words = words.replaceAll("F", "𝔽");
        words = words.replaceAll("g", "𝕘");
        words = words.replaceAll("G", "𝔾");
        words = words.replaceAll("h", "𝕙");
        words = words.replaceAll("H", "ℍ");
        words = words.replaceAll("i", "𝕚");
        words = words.replaceAll("I", "𝕀");
        words = words.replaceAll("j", "𝕛");
        words = words.replaceAll("J", "𝕁");
        words = words.replaceAll("k", "𝕜");
        words = words.replaceAll("K", "𝕂");
        words = words.replaceAll("l", "𝕝");
        words = words.replaceAll("L", "𝕃");
        words = words.replaceAll("m", "𝕞");
        words = words.replaceAll("M", "𝕄");
        words = words.replaceAll("n", "𝕟");
        words = words.replaceAll("N", "ℕ");
        words = words.replaceAll("o", "𝕠");
        words = words.replaceAll("O", "𝕆");
        words = words.replaceAll("p", "𝕡");
        words = words.replaceAll("P", "ℙ");
        words = words.replaceAll("q", "𝕢");
        words = words.replaceAll("Q", "ℚ");
        words = words.replaceAll("r", "𝕣");
        words = words.replaceAll("R", "ℝ");
        words = words.replaceAll("s", "𝕤");
        words = words.replaceAll("S", "𝕊");
        words = words.replaceAll("t", "𝕥");
        words = words.replaceAll("T", "𝕋");
        words = words.replaceAll("u", "𝕦");
        words = words.replaceAll("U", "𝕌");
        words = words.replaceAll("v", "𝕧");
        words = words.replaceAll("V", "𝕍");
        words = words.replaceAll("w", "𝕨");
        words = words.replaceAll("W", "𝕎");
        words = words.replaceAll("x", "𝕩");
        words = words.replaceAll("X", "𝕏");
        words = words.replaceAll("y", "𝕪");
        words = words.replaceAll("Y", "𝕐");
        words = words.replaceAll("z", "𝕫");
        words = words.replaceAll("Z", "ℤ");
        return words;
    }

}
