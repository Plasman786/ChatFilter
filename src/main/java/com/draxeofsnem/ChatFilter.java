package com.draxeofsnem;

import java.util.*;

public class ChatFilter {

    private final AhoCorasick ac;
    private final List<String> bannedWords;

    public ChatFilter(List<String> bannedWords) {
        this.bannedWords = bannedWords;
        this.ac = new AhoCorasick(bannedWords);
    }

    public static class Result {
        public final boolean blocked;
        public final String filtered;
        public final String original;
        public Result(boolean blocked, String filtered, String original) {
            this.blocked = blocked;
            this.filtered = filtered;
            this.original = original;
        }
    }

    public Result filter(String msg) {
        TextNormaliser.Result norm = TextNormaliser.normalise(msg);
        String normalised = norm.normalised;
        int[] map = norm.mapOriginal;

        char[] filteredChars = msg.toCharArray();
        boolean blocked = false;

        List<AhoCorasick.Match> matches = ac.search(normalised);

        for (AhoCorasick.Match m : matches) {
            blocked = true;
            // end index in normalised
            int end = m.endIndex;
            // length of matched word
            String word = bannedWords.get(m.wordIndex);
            int start = end - word.length() + 1;
            if (start < 0) start = 0;

            for (int i = start; i <= end; i++) {
                if (i >= map.length) continue;
                filteredChars[map[i]] = '*'; // mark in original
            }
        }

        String filtered = new String(filteredChars);
        if (blocked) filtered = filtered.replaceAll("\\*+", "Blocked");

        return new Result(blocked, filtered, msg);
    }
}
