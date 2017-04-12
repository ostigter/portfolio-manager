// This file is part of the 'portfolio-manager' (Portfolio Manager)
// project, an open source stock portfolio manager application
// written in Java.
//
// Copyright 2015 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

/**
 * Retrieves HTTP pages from an URL, with support for a HTTP proxy with optional authentication.
 * 
 * @author Oscar Stigter
 */
public class HttpPageReader {

    /** User-Agent spoofing as Android phone for minimum page size (maximum performance). */
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; Mobile)";

    /** HTTP connect timeout in milliseconds. */
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds

    /** HTTP read timeout in milliseconds. */
    private static final int READ_TIMEOUT = 60000; // 1 minute

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    private boolean useProxy = false;
    private String proxyHost = "";
    private int proxyPort = 8080;
    private String proxyUsername = "";
    private String proxyPassword = "";

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * Reads an HTTP page from an URL.
     * 
     * @param uri
     *            The URL.
     * 
     * @return The response content body.
     */
    public String read(String uri) throws IOException {
        updateProxySettings();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        return IOUtils.toString(connection.getInputStream());
    }

    /**
     * Retrieves the last modified date of a remote file (if set) over HTTP.
     * 
     * @param uri
     *            The URI of the remote file.
     * 
     * @return The last modified date if found, otherwise -1L.
     * 
     * @throws IOException
     *             If the remote file could not be found, or the last modified date could not be determined.
     */
    public long getFileLastModified(String uri) throws IOException {
        long timestamp = -1L;

        updateProxySettings();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        int statusCode = connection.getResponseCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            String dateString = connection.getHeaderField("Last-Modified");
            if (dateString != null && dateString.length() > 16) {
                dateString = dateString.substring(5, 16);
                try {
                    timestamp = DATE_FORMAT.parse(dateString).getTime();
                } catch (ParseException e) {
                    System.err.format("ERROR: Invalid Last-Modified date '%s' from HEAD request to '%s'\n", dateString, url);
                }
            } else {
                System.err.format("ERROR: No Last-Modified header from HEAD request to '%s'\n", url);
            }
        } else {
            System.err.format("ERROR: Failed HEAD request to '%s' (HTTP status code: %d)\n", url, statusCode);
        }

        return timestamp;
    }

    /**
     * Retrieves (downloads) a remote file over HTTP.
     * 
     * @param uri
     *            The URI of the remote file.
     * 
     * @return The file contents.
     * 
     * @throws IOException
     *             If the file could not be found or retrieved.
     */
    public InputStream downloadFile(String uri) throws IOException {
        updateProxySettings();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        return connection.getInputStream();
    }

    /**
     * Updates the HTTP proxy settings (if used).
     */
    private void updateProxySettings() {
        if (useProxy) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            Authenticator.setDefault(new SimpleAuthenticator(proxyUsername, proxyPassword));
        }
    }

    /**
     * Simple HTTP authenticator with a username and password.
     * 
     * @author Oscar Stigter
     */
    private static class SimpleAuthenticator extends Authenticator {

        private final String username;

        private final String password;

        public SimpleAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}
