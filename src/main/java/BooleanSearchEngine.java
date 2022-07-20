import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class BooleanSearchEngine implements SearchEngine {

    private final String PDF_EXTENSION = ".pdf";
    final private Map<String, ArrayList<PageEntry>> parsedData = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if ((!pdfsDir.isDirectory() && !pdfsDir.getName().toLowerCase().endsWith(PDF_EXTENSION)))
            throw new RuntimeException("Current path is file, but doesn't a PDF");

        List<File> pdfFiles = new ArrayList<>();
        if (pdfsDir.isDirectory())
            pdfFiles = Files.list(pdfsDir.toPath())
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().toLowerCase().endsWith(PDF_EXTENSION))
                    .collect(Collectors.toList());
        else pdfFiles.add(pdfsDir);
        pdfFiles.forEach(this::parseFile);
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> result = parsedData.get(word);
        return nonNull(result)
                ? result.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())
                : Collections.emptyList();
    }

    private void parseFile(File path) {
        try (PdfDocument doc = new PdfDocument(new PdfReader(path))) {
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                parsePage(doc, path.getName(), i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsePage(PdfDocument doc, String fileName, int pageNumber) {
        String page = PdfTextExtractor.getTextFromPage(doc.getPage(pageNumber));
        Map<String, Integer> wordsFrequencyOnPage = new HashMap<>();
        for (var word : page.split("\\P{IsAlphabetic}+")) {
            if (word.isEmpty()) {
                continue;
            }
            wordsFrequencyOnPage.put(word.toLowerCase(), wordsFrequencyOnPage.getOrDefault(word.toLowerCase(), 0) + 1);
        }

        for (Map.Entry<String, Integer> wordFrequency : wordsFrequencyOnPage.entrySet()) {
            if (parsedData.containsKey(wordFrequency.getKey()))
                parsedData.get(wordFrequency.getKey()).add(new PageEntry(fileName, pageNumber, wordsFrequencyOnPage.get(wordFrequency.getKey())));
            else
                parsedData.put(wordFrequency.getKey(), new ArrayList<>(List.of(new PageEntry(fileName, pageNumber, wordsFrequencyOnPage.get(wordFrequency.getKey())))));
        }
    }
}
