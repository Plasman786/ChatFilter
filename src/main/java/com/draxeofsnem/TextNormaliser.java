package com.draxeofsnem;

import java.util.*;

public class TextNormaliser {

    public static class Result {
        public final String normalised;
        public final int[] mapOriginal; // maps normalised index → original index
        public Result(String normalised, int[] mapOriginal) {
            this.normalised = normalised;
            this.mapOriginal = mapOriginal;
        }
    }

    public static Result normalise(String input) {
        StringBuilder sb = new StringBuilder();
        List<Integer> map = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
                map.add(i);
            }
        }

        int[] mapping = map.stream().mapToInt(Integer::intValue).toArray();
        return new Result(sb.toString(), mapping);
    }
}
