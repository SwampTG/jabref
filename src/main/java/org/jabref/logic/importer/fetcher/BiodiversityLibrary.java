package org.jabref.logic.importer.fetcher;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.utils.URIBuilder;

public class BiodiversityLibrary {

    private final String BASE_LIBRARY_URL = "https://www.biodiversitylibrary.org/api3";
    private final String API_KEY = "33b1c2f0-5320-4714-85c7-a7c202d1a813";
    private final String RESPONSE_FORMAT = "json";
    private final String AUTHOR_SEARCH = "AuthorSearch";

    public BiodiversityLibrary() {}

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
}
