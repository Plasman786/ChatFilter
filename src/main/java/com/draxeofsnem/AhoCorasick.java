package com.draxeofsnem;

import java.util.*;

public class AhoCorasick {
    private final int[] trie;        // packed trie: child indices
    private final int[] fail;        // failure links
    private final List<List<Integer>> output;  // matched word indices
    private final int ALPHABET = 128;  // ASCII subset

    private int nodeCount = 1;

    public AhoCorasick(List<String> words) {
        // Build trie in memory-packed form
        trie = new int[1000000];   // large enough, adjust if needed
        fail = new int[1000000];
        output = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) output.add(new ArrayList<>());

        Arrays.fill(trie, -1);

        // Insert words
        for (int i = 0; i < words.size(); i++) {
            String w = words.get(i).toLowerCase();
            int node = 0;
            for (char c : w.toCharArray()) {
                int idx = c;
                int child = trie[node * ALPHABET + idx];
                if (child == -1) {
                    trie[node * ALPHABET + idx] = nodeCount;
                    node = nodeCount++;
                } else {
                    node = child;
                }
            }
            output.get(node).add(i);
        }

        // Build failure links (BFS)
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < ALPHABET; i++) {
            int child = trie[i];
            if (child != -1) {
                fail[child] = 0;
                q.add(child);
            }
        }

        while (!q.isEmpty()) {
            int r = q.poll();
            for (int c = 0; c < ALPHABET; c++) {
                int u = trie[r * ALPHABET + c];
                if (u == -1) continue;
                q.add(u);

                int v = fail[r];
                while (v != 0 && trie[v * ALPHABET + c] == -1) {
                    v = fail[v];
                }
                if (trie[v * ALPHABET + c] != -1) {
                    fail[u] = trie[v * ALPHABET + c];
                } else {
                    fail[u] = 0;
                }

                output.get(u).addAll(output.get(fail[u]));
            }
        }
    }

    public List<Match> search(String text) {
        List<Match> matches = new ArrayList<>();
        int node = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != 0 && trie[node * ALPHABET + c] == -1) {
                node = fail[node];
            }
            if (trie[node * ALPHABET + c] != -1) {
                node = trie[node * ALPHABET + c];
            }

            for (int idx : output.get(node)) {
                matches.add(new Match(idx, i));
            }
        }
        return matches;
    }

    public static class Match {
        public final int wordIndex;
        public final int endIndex;
        public Match(int wordIndex, int endIndex) {
            this.wordIndex = wordIndex;
            this.endIndex = endIndex;
        }
    }
}
