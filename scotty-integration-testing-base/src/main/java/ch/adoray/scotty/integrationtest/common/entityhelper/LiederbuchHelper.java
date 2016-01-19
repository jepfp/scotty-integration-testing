package ch.adoray.scotty.integrationtest.common.entityhelper;

import java.util.Map;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;

public class LiederbuchHelper {
    public final static long BOOKID_ADORAY_LIEDERBUCH = 1;
    public final static long BOOKID_DIR_SINGEN_WIR2 = 2;
    public final static long BOOKID_ADONAI_ZUG = 3;
    public final static long BOOKID_ADORAY_LUZERN = 4;
    
    public static String getValueForIdAndColumn(Long id, String column) {
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.Liederbuch.TABLE, id);
        return record.get(column);
    }
    
    public static String getBookMnemonic(long liederbuchId){
        return getValueForIdAndColumn(liederbuchId, Tables.Liederbuch.MNEMONIC);
    }
}
