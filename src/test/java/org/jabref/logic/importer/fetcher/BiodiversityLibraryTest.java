package org.jabref.logic.importer.fetcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BiodiversityLibraryTest {
    private final String BASE_LIBRARY_URL = "https://www.biodiversitylibrary.org/api3?";
    private final String API_KEY = "apikey=33b1c2f0-5320-4714-85c7-a7c202d1a813";
    private final String RESPONSE_FORMAT = "&format=json";

    private BiodiversityLibrary biodiversityFetcher;

    @BeforeEach
    public void setup() throws Exception {
        biodiversityFetcher = new BiodiversityLibrary();
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
}
