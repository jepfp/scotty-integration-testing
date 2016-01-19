package ch.adoray.scotty.integrationtest.common;

public class Tables {
    public static final String LIEDTEXT = "liedtext";
    public static final String REFRAIN = "refrain";
    public static final String LIED = "lied";
    public static final String LANGUAGE = "language";
    public static final String USER = "user";
    public static final String LOGGING = "logging";
    public static final String FK_LIEDERBUCH_LIED = "fkliederbuchlied";
    public static final String RUBRIK = "rubrik";
    public static final String FILE = "file";
    public static final String FILE_METADATA = "filemetadata";

    public class FkLiederbuchLied {
        public static final String TABLE = "fkliederbuchlied";
        public static final String LIEDNR = "Liednr";
        public static final String LIEDERBUCH_ID = "liederbuch_id";
        public static final String LIED_ID = "lied_id";
    }

    public class Liederbuch {
        public static final String TABLE = "liederbuch";
        public static final String MNEMONIC = "mnemonic";
    }
}
