package ch.adoray.scotty.integrationtest.sqlitedbdump;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.rules.TemporaryFolder;
public class SqliteDatabaseDownloader {
    public static File downloadDatabase(TemporaryFolder targetFolder) {
        try {
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
}