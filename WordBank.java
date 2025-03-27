import java.io.*;
import java.util.*;

class WordBank {
    private Map<Integer, List<String>> wordsByLength;

    public WordBank(String filePath) {
        wordsByLength = new HashMap<>();
        loadWords(filePath);
        System.out.println("Palavras carregadas por tamanho:");
        for (Map.Entry<Integer, List<String>> entry : wordsByLength.entrySet()) {
            System.out.println("Tamanho " + entry.getKey() + ": " + entry.getValue().size() + " palavras");
        }
    }

    private void loadWords(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();
                int length = word.length();
                wordsByLength.computeIfAbsent(length, k -> new ArrayList<>()).add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar o arquivo de palavras: " + filePath);
        }
    }

    public String getRandomWord(int length, Set<String> usedWords) {
        List<String> words = wordsByLength.get(length);
        if (words == null || words.isEmpty()) return null;
        
        List<String> availableWords = new ArrayList<>(words);
        availableWords.removeAll(usedWords);
        
        if (availableWords.isEmpty()) return null;
        
        return availableWords.get(new Random().nextInt(availableWords.size()));
    }

    public Set<Integer> getAvailableLengths() {
        return wordsByLength.keySet();
    }
}