package org.jabref.logic.importer.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.jabref.logic.importer.ImporterPreferences;
import org.jabref.logic.layout.format.EntryTypeFormatter;
import org.jabref.logic.net.URLDownload;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kong.unirest.json.JSONObject;

public class BiodiversityLibrary {

    private final String BASE_LIBRARY_URL = "https://www.biodiversitylibrary.org/api3";
    private final String API_KEY = "33b1c2f0-5320-4714-85c7-a7c202d1a813";
    private final String RESPONSE_FORMAT = "json";
    private final String AUTHOR_SEARCH = "AuthorSearch";
    private final String ADVANCED_SEARCH = "PublicationSearchAdvanced";
    private final String SEARCH_BY_AUTHOR = "title";
    private final String SEARCH_BY_TITLE = "authorname";

    private final ImporterPreferences importerPreferences;

    private final Logger logger = LoggerFactory.getLogger(BiodiversityLibrary.class);

    public BiodiversityLibrary(ImporterPreferences importerPreferences) {
        this.importerPreferences = importerPreferences;
    }

    public URL getBaseURL() throws URISyntaxException, MalformedURLException {
        URIBuilder baseURI = new URIBuilder(BASE_LIBRARY_URL);
        baseURI.addParameter("apikey", API_KEY);
        baseURI.addParameter("format", RESPONSE_FORMAT);

        return baseURI.build().toURL();
    }

    public URL getAuthorSearchBaseURL() throws URISyntaxException, MalformedURLException {
        URIBuilder authorSearchURI = new URIBuilder(getBaseURL().toURI());
        authorSearchURI.addParameter("op", AUTHOR_SEARCH);

        return authorSearchURI.build().toURL();
    }

    public URL getAdvancedSearchByAuthorBaseURL() throws URISyntaxException, MalformedURLException {
        URIBuilder authorSearchURI = new URIBuilder(getBaseURL().toURI());
        authorSearchURI.addParameter("op", ADVANCED_SEARCH);

        return authorSearchURI.build().toURL();
    }

    public String searchAuthors(String authorName) {
        String result = "";

        try {
            URIBuilder searchBuilder = new URIBuilder(getAuthorSearchBaseURL().toURI());
            searchBuilder.addParameter("authorname", authorName);
            URLDownload download = new URLDownload(searchBuilder.build().toURL());
            result = download.asString();
        } catch(URISyntaxException | IOException exception) {
            logger.error("Search URL didn't functioned: ".concat(exception.getMessage()), exception);
        }

        return result;
    }

    public List<BibEntry> performAdvancedSearch(BibEntry searchParam) {
        String results = "";
        try {
            URIBuilder searchBuilder = new URIBuilder(getAdvancedSearchByAuthorBaseURL().toURI());

            if (searchParam.hasField(StandardField.TITLE)) {
                searchBuilder.addParameter(SEARCH_BY_TITLE, searchParam.getTitle().orElse(""));
            }
            searchBuilder.addParameter(SEARCH_BY_AUTHOR,
                    searchParam.getField(StandardField.AUTHOR).orElse(""));

            URLDownload download = new URLDownload(searchBuilder.build().toURL());
            results = download.asString();

        } catch (URISyntaxException | IOException exception) {
            logger.error("Search URL didn't functioned: ".concat(exception.getMessage()),
                    exception);
        }
        return !results.isEmpty() ? parseEntries(new JSONObject(results)) : Collections.emptyList();
    }
    
    public List<BibEntry> parseEntries(JSONObject jObject) {
        if (!jObject.isEmpty() && !jObject.isNull("Result")) {
            JSONObject jObjectToParse = jObject.getJSONObject("Result");
            BibEntry newBibEntry = new BibEntry();
            newBibEntry.setType(jObject.get("Genre"));
        }
        
        return Collections.emptyList();
    }
}
