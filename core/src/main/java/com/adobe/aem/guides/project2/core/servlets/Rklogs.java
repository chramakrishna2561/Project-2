package com.adobe.aem.guides.project2.core.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rklogs {

    private static final Logger LOG = LoggerFactory.getLogger(Rklogs.class);

    public static void main(String[] args) {
        // Log a simple informational message
        LOG.info("This is an informational log message to test logging.");

        // Log a warning message
        LOG.warn("This is a warning log message to test logging.");

        // Log an error message
        LOG.error("This is an error log message to test logging.");

        // Log an error with an exception stack trace
        try {
            throw new Exception("This is a test exception");
        } catch (Exception e) {
            LOG.error("An exception occurred while testing logging", e);
        }
    }
}
