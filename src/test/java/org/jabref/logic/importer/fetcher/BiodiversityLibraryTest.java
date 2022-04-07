package org.jabref.logic.importer.fetcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jabref.logic.importer.ImporterPreferences;
import org.jabref.logic.util.StandardFileType;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kong.unirest.json.JSONObject;

public class BiodiversityLibraryTest {
    private final String BASE_LIBRARY_URL = "https://www.biodiversitylibrary.org/api3?";
    private final String API_KEY = "apikey=33b1c2f0-5320-4714-85c7-a7c202d1a813";
    private final String RESPONSE_FORMAT = "&format=json";

    private BiodiversityLibrary biodiversityFetcher;
    private BibEntry giffordFieldNotes;
    private ImporterPreferences importerPreferences;

    @BeforeEach
    public void setup() throws Exception {
        this.importerPreferences = mock(ImporterPreferences.class);
        biodiversityFetcher = new BiodiversityLibrary(importerPreferences);
        giffordFieldNotes = new BibEntry();

        giffordFieldNotes.setType(StandardEntryType.Article);
        giffordFieldNotes.setField(StandardField.AUTHOR, "Gifford, Edward Winslow");
        giffordFieldNotes.setField(StandardField.TITLE,
                "Field notes on the land birds of the Galapagos Islands, and of Cocos Island,Costa Rica");
    }

    @Test
    public void baseURLConstruction() {
        try {
            String baseURL = biodiversityFetcher.getBaseURL().toString();
            assertEquals(BASE_LIBRARY_URL
                .concat(API_KEY)
                .concat(RESPONSE_FORMAT), baseURL);
        } catch(Exception e) {
            System.out.println("Base URL error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void basicAPIMethodUrlConstruction() {
        try {
            String queryParam = "&op=AuthorSearch";
            assertEquals(BASE_LIBRARY_URL
                    .concat(API_KEY)
                    .concat(RESPONSE_FORMAT)
                    .concat(queryParam), biodiversityFetcher.getAuthorSearchBaseURL().toString());
        } catch(Exception e) {
            System.out.println("Base URL error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void urlDownloadAPIReturnAfter() {
        
        String  result = biodiversityFetcher.searchAuthors("dimmock");
        JSONObject searchResult = new JSONObject(result);

        assertNotNull(result);
        assertNotNull(searchResult.get("Result"));
    }

    @Test
    public void testAdvancedSearchByAuthorWithTitleAndAuthor() {
        BibEntry searchParam = new BibEntry();
        searchParam.setField(StandardField.AUTHOR, "Gifford");
        searchParam.setField(StandardField.TITLE, "");
        List<BibEntry> searchResults = biodiversityFetcher.performAdvancedSearch(searchParam);
        assertTrue(searchResults.contains(giffordFieldNotes));
    }
}
