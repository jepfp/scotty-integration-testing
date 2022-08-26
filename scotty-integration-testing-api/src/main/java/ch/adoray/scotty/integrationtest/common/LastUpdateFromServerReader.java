package ch.adoray.scotty.integrationtest.common;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

public class LastUpdateFromServerReader {
    public static String read() {
        try {
            URL url = new URL(config().getLastUpdateUrl());
            System.out.println("Getting " + config().getLastUpdateUrl());
            try (InputStream input = url.openStream()) {
                return IOUtils.toString(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while getting config().getLastUpdateUrl().", e);
        }
    }
}
