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
import java.util.Optional;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jgit.lib.ObjectIdOwnerMap.Entry;
import org.jabref.logic.cleanup.Formatter;
import org.jabref.logic.formatter.Formatters;
import org.jabref.logic.importer.ImporterPreferences;
import org.jabref.logic.layout.format.EntryTypeFormatter;
import org.jabref.logic.net.URLDownload;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.json.JSONArray;
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
        List<BibEntry> parsingResults = new ArrayList<>();
        if (!jObject.isEmpty() && !jObject.isNull("Result")) {
            JSONArray jsArray = jObject.getJSONArray("Result");
            jsArray.forEach(
                jObjectToParse -> {
                    if(jObjectToParse instanceof JSONObject) {
                        BibEntry newBibEntry = new BibEntry();
                            newBibEntry.setType(
                                    StandardEntryType.valueOf(((JSONObject) jObjectToParse).getString("Genre")));
                            newBibEntry.setField(StandardField.TYPE,
                                    ((JSONObject) jObjectToParse).getString("BHLType"));
                            newBibEntry.setField(StandardField.TITLE, ((JSONObject) jObjectToParse).getString("Title"));
                            newBibEntry.setField(StandardField.URL, ((JSONObject) jObjectToParse).getString("PartUrl"));

                        String authorString = 
                                generateAuthorString(((JSONObject) jObjectToParse)
                                        .getJSONArray("Authors")).orElse("");
                        if(!authorString.isEmpty()) {
                            newBibEntry.setField(StandardField.AUTHOR, authorString);
                        }

                        parsingResults.add(newBibEntry);
                    }
                }
            );
            return parsingResults;
        }
        
        return Collections.emptyList();
    }

    public Optional<String> generateAuthorString(JSONArray authorsObject) {
        String authorStringResult = "";
        
        if(!authorsObject.isEmpty()) {
            for(int i = 0; i < authorsObject.length()-1; i++) {
                authorStringResult.concat(JSONObject.class.cast(authorsObject.get(i)).getString("Name"));
                if(i < authorsObject.length()-2)
                    authorStringResult.concat(", ");
            }
            if(authorsObject.length()>1)
                authorStringResult
                        .concat("and ")
                        .concat(JSONObject.class.cast(
                            authorsObject.get(authorsObject.length()-1)).getString("Name"));
            }

        return Optional.empty();
    }
}
