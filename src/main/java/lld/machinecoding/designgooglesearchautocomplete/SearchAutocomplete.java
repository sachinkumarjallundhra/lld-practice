package lld.machinecoding.designgooglesearchautocomplete;

import java.util.*;

public class SearchAutocomplete {

    // ================= Trie Node =================
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> sentenceCount = new HashMap<>();
    }

    private final TrieNode root = new TrieNode();
    private TrieNode currentNode = root;
    private final StringBuilder currentInput = new StringBuilder();

    // ================= Constructor =================
    public SearchAutocomplete(String[] phrases, int[] counts) {
        for (int i = 0; i < phrases.length; i++) {
            insert(phrases[i], counts[i]);
        }
    }

    // ================= Public API =================
    public List<String> getSuggestions(char ch) {
        if (ch == '#') {
            String sentence = currentInput.toString();
            insert(sentence, 1);

            // reset state
            currentInput.setLength(0);
            currentNode = root;
            return Collections.emptyList();
        }

        currentInput.append(ch);

        if (currentNode != null) {
            currentNode = currentNode.children.get(ch);
        }

        if (currentNode == null) {
            return Collections.emptyList();
        }

        return getTop3(currentNode.sentenceCount);
    }

    // ================= Helpers =================

    private void insert(String sentence, int count) {
        TrieNode node = root;
        for (char c : sentence.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            node.sentenceCount.put(sentence,
                    node.sentenceCount.getOrDefault(sentence, 0) + count);
        }
    }

    private List<String> getTop3(Map<String, Integer> map) {
        PriorityQueue<String> pq = new PriorityQueue<>(
                (a, b) -> {
                    int freqCompare = map.get(b) - map.get(a);
                    if (freqCompare != 0) return freqCompare;
                    return a.compareTo(b);
                }
        );

        pq.addAll(map.keySet());

        List<String> result = new ArrayList<>();
        for (int i = 0; i < 3 && !pq.isEmpty(); i++) {
            result.add(pq.poll());
        }
        return result;
    }
}

