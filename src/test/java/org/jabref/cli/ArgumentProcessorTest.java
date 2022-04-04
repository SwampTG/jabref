package org.jabref.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import org.mockito.Answers;

import org.jabref.preferences.JabRefPreferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgumentProcessorTest {
    private JabRefPreferences preferences;

    @BeforeEach
    public void setUp() throws Exception {
        preferences = mock(JabRefPreferences.class, Answers.RETURNS_DEEP_STUBS);
    }

    @Test
    void processingArgumentsForImportFileToDownload() throws Exception {
        String[] arguments = new String[]{"--nogui", "--import=https://raw.githubusercontent.com/barbagroup/bibtex/master/barba.bib"};
        JabRefCLI cli = new JabRefCLI(arguments);
        ArgumentProcessor argumentProcessor = new ArgumentProcessor(arguments, ArgumentProcessor.Mode.REMOTE_START, preferences);
        assertTrue(argumentProcessor.hasParserResults());
    }

    @Test
    void processingArgumentsForImportFileToDownloadThatDoesntExist() throws Exception {
        String[] arguments = new String[]{"-i=http://www.fum.com.br/"};
        ArgumentProcessor argumentProcessor = new ArgumentProcessor(arguments, ArgumentProcessor.Mode.REMOTE_START, preferences);
        assertFalse(argumentProcessor.hasParserResults());

    }
}