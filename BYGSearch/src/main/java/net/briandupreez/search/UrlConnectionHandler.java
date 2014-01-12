package net.briandupreez.search;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handle a URL Connection.
 * Created by Brian on 2014/01/08.
 */
public class UrlConnectionHandler {

    private transient final Log logger = LogFactory.getLog(this.getClass());

    /**
     * Base Auth, used with Bing search
     *
     * @param url    the url
     * @param apiKey the API Key
     * @return http url connection
     */
    public HttpURLConnection createBasicConnection(final String url, final String apiKey) {
        final HttpURLConnection connection = createConnection(url);

        final byte[] accountKeyBytes = Base64.encodeBase64((apiKey + ":" + apiKey).getBytes());
        final String accountKeyEnc = new String(accountKeyBytes);
        final String s1 = "Basic " + accountKeyEnc;
        connection.setRequestProperty("Authorization", s1);
        return connection;

    }

    /**
     * A Signed OAuth Connection, used with yahoo
     *
     * @param url      the url
     * @param consumer the oauth consumer
     * @return http url connection
     */
    public HttpURLConnection createOAuthConnection(final String url, final OAuthConsumer consumer) {
        final HttpURLConnection connection = createConnection(url);

        if (consumer != null) {
            try {
                logger.info("Signing the oAuth consumer");
                consumer.sign(connection);
                connection.connect();
                return connection;
            } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
                logger.error("OAuth Error signing the consumer", e);
                throw new RuntimeException("OAuth Error", e);
            } catch (final IOException e) {
                logger.error("Connection Error", e);
                throw new RuntimeException("Connection Error", e);
            }
        }
        return null;

    }

    private HttpURLConnection createConnection(final String url) {
        try {
            final URL u = new URL(url);
            final HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            return uc;
        } catch (final Exception e) {
            logger.error("Create Connection Exception.", e);
            throw new RuntimeException("Connection Error", e);
        }

    }


    /**
     * Process connection
     * @param connection the connection
     * @return the result
     */
    public RequestResult processConnection(final HttpURLConnection connection) {
        RequestResult result = null;
        try {
            final int responseCode = connection.getResponseCode();
            if (200 == responseCode || 401 == responseCode || 404 == responseCode) {
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader(new InputStreamReader(responseCode == 200 ? connection.getInputStream() : connection.getErrorStream()));
                    final StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    result = new RequestResult(responseCode, sb.toString());
                } catch (final IOException e) {
                    logger.error("Stream Error", e);
                    throw new RuntimeException("Stream Error", e);
                } finally {
                    if (rd != null) {
                        rd.close();
                    }
                }
            }
        } catch (final IOException e) {
            logger.error("Connection Exception", e);
            throw new RuntimeException("Connection Exception", e);

        }
        return result;
    }


    public static class RequestResult {
        private final int responseCode;
        private final String response;

        public RequestResult(final int responseCode, final String response) {
            this.responseCode = responseCode;
            this.response = response;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponse() {
            return response;
        }
    }

}
