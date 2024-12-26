import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SequentialSearch {

    private static final String BOOKS_DIRECTORY = "/books";
    private static final String SEARCH_QUERY_1 = "The best detective that catches many criminals using his deductive methods";
    private static final String SEARCH_QUERY_2 = "The girl that falls through a rabbit hole into a fantasy wonderland";
    private static final String SEARCH_QUERY_3 = "A war between Russia and France in the cold winter";

    public static void main(String[] args) throws Exception {
        Path documentsDirectory = Paths.get(SequentialSearch.class.getResource(BOOKS_DIRECTORY).toURI());

        List<String> documents = Files.list(documentsDirectory)
                .map(Path::toString)
                .collect(Collectors.toList());


        List<String> terms = TFIDF.getWordsFromLine(SEARCH_QUERY_1);

        findMostRelevantDocuments(documents, terms);
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws Exception {
        Map<String, DocumentData> documentResults = new HashMap<>();

        for (String document : documents) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(document))) {
                List<String> lines = bufferedReader.lines().toList();
                List<String> words = TFIDF.getWordsFromLines(lines);
                DocumentData documentData = TFIDF.createDocumentData(words, terms);
                documentResults.put(document, documentData);
            }
        }

        Map<Double, List<String>> documentsByScore = TFIDF.getDocumentsSortedByScore(terms, documentResults);
        printResults(documentsByScore);
    }

    private static void printResults(Map<Double, List<String>> documentsByScore) {
        for (Map.Entry<Double, List<String>> docScorePair : documentsByScore.entrySet()) {
            double score = docScorePair.getKey();
            for (String document : docScorePair.getValue()) {
                String[] paths = document.split("/");
                System.out.printf("Book : %s - score : %f%n", paths[paths.length - 1], score);
            }
        }
    }
}
