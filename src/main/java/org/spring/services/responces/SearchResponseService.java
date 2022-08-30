package org.spring.services.responces;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.spring.services.SearchData;

@Data
public class SearchResponseService implements ResponseService {
    private boolean result;
    private int count;
    private SearchData[] data;

    public SearchResponseService(boolean result, int count, SearchData[] data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }

    public SearchResponseService(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
