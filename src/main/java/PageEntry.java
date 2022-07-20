import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;

@AllArgsConstructor
@Getter
public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    @Override
    public int compareTo(PageEntry o) {
        return Comparator.comparing(PageEntry::getCount)
                .compare(this, o);
    }
}