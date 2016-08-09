package ch.adoray.scotty.integrationtest.common.entityhelper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FilenameUtils;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.common.Tables;
public class FileHelper {
    public static long createDummyFile(long fileMetadataId, String pdfResourceName) {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO file (filemetadata_id, data, filename, filesize, filetype) VALUES (?, ?, ?, ?, ?);")) {
            File file = new File(getPdfResourcePathByName(pdfResourceName));
            String filename = file.getName();
            statement.setLong(1, fileMetadataId);
            statement.setBlob(2, new FileInputStream(file));
            statement.setString(3, filename);
            statement.setString(4, String.valueOf(file.length()));
            statement.setString(5, FilenameUtils.getExtension(filename));
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException("Error while creating dummy file.", e);
        }
    }

    public static String getPdfResourcePathByName(String pdfResourceName) {
        URL url = ResourceLoader.class.getClassLoader().getResource(pdfResourceName);
        try {
            File f = new File(url.toURI());
            return f.getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readFileByIdAndSaveTo(long id, Path path) {
        String sqlStatement = "SELECT data FROM " + Tables.File.TABLE + " WHERE id = " + id;
        try (PreparedStatement statement = DatabaseAccess.prepareStatement(sqlStatement); ResultSet resultSet = statement.executeQuery();) {
            if (!resultSet.next()) {
                throw new RuntimeException("DB record with id " + id + " not found. Statement: " + sqlStatement);
            }
            try (InputStream binaryStream = resultSet.getBinaryStream(1)) {
                Files.copy(binaryStream, path);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error while reading file from database.", e);
        }
    }
}
