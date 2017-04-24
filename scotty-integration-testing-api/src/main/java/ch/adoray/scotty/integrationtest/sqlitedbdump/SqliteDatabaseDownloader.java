package ch.adoray.scotty.integrationtest.sqlitedbdump;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.rules.TemporaryFolder;
public class SqliteDatabaseDownloader {
    public static File downloadDatabase(TemporaryFolder targetFolder) {
        try {
            configureLibrariesToAllowUnsecureSslConnections();
            URL website = new URL(config().getSqliteDbDumpUrl());
            System.out.println("Download DB from " + config().getSqliteDbDumpUrl());
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File database = targetFolder.newFile("adonai.sqlite");
            try (FileOutputStream fos = new FileOutputStream(database)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return database;
        } catch (Exception e) {
            throw new RuntimeException("Error while downloading sqlite database.", e);
        }
    }

    private static void configureLibrariesToAllowUnsecureSslConnections() {
        //https://gist.github.com/mafulafunk/1625912
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            // Create an ssl socket factory with our all-trusting manager
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            });
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while configuring SSL libs.", e);
        }
    }
}