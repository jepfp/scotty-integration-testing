package ch.adoray.scotty.acceptancetest.base.model;

import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.appfoundation.automation.util.XPathUtils;

import ch.adoray.scotty.acceptancetest.base.util.AwaitFinder;
public class SongModel extends BaseModel {
    public static final String TITEL_XPATH = XPathUtils.findInputByName("Titel");
    public static final String TONALITY_XPATH = ExtJs5XPathUtils.findComboBoxButtonByFormLabel("Tonart:");
    public static final String RUBRIK_XPATH = ExtJs5XPathUtils.findComboBoxButtonByFormLabel("Rubrik:");
    public static final String SONG_LOADED_XPATH = XPathUtils.findDivByText("Lied vollständig geladen.");
    public static final String SONG_SAVED_XPATH = XPathUtils.findDivByText("Änderungen am Lied gespeichert.");
    public static final String SONGSHEET_DELETED_XPATH = XPathUtils.findDivByText("Noten gelöscht.");
    public static final String SONGSHEET_DOWNLOAD_LINK_XPATH = XPathUtils.findLinkByText("Noten anzeigen.");
    public static final String SONGSHEET_NO_SONGSHEET_AVAILABLE_XPATH = XPathUtils.findDivByText("keine Noten vorhanden");
    public static final String SONGSHEET_UPLOAD_FILE_FIELD = ExtJs5XPathUtils.findFileFieldByButtonText("PDF-Noten hochladen...");
    public static final String SONGSHEET_DELETE_BUTTON_XPATH = ExtJs5XPathUtils.findButtonByText("Noten löschen");
    public static final String YES_BUTTON_IN_MESSAGE_BOX_XPATH = ExtJs5XPathUtils.findMessageBoxButtonByText("Yes");
    public static final String NO_BUTTON_IN_MESSAGE_BOX_XPATH = ExtJs5XPathUtils.findMessageBoxButtonByText("No");

    public SongModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findTitelField() {
        return this.find(TITEL_XPATH);
    }
    
    public WebElement findTonalityComboBox() {
        return this.find(TONALITY_XPATH);
    }

    public WebElement findSongLoadedDiv() {
        return this.find(SONG_LOADED_XPATH);
    }

    public WebElement findSongsheetDownloadLink() {
        return this.find(SONGSHEET_DOWNLOAD_LINK_XPATH);
    }

    public WebElement findNoSongsheetAvailable() {
        return this.find(SONGSHEET_NO_SONGSHEET_AVAILABLE_XPATH);
    }

    public WebElement findSongsheetUploadFileField() {
        return this.find(SONGSHEET_UPLOAD_FILE_FIELD);
    }

    public WebElement findSongsheetDeleteButton() {
        return this.find(SONGSHEET_DELETE_BUTTON_XPATH);
    }

    public WebElement findYesButtonInMessageBox() {
        return this.find(YES_BUTTON_IN_MESSAGE_BOX_XPATH);
    }

    public WebElement findNoButtonInMessageBox() {
        return this.find(NO_BUTTON_IN_MESSAGE_BOX_XPATH);
    }

    public WebElement findSongbookNumberCellByRowNr(int rowNr) {
        String headerTextOfSongbookNumberGrid = ""; //empty because it has no title
        String xpath = ExtJs5XPathUtils.findCellByRowAndColumnInGridByHeaderText(rowNr, 2, headerTextOfSongbookNumberGrid);
        return findElement(xpath);
    }

    public WebElement findSongbookNumberEditFieldByRowNr(long rowNr) {
        String headerTextOfSongbookNumberGrid = ""; //empty because it has no title
        String xpath = ExtJs5XPathUtils.findCellByRowAndColumnInGridByHeaderText((int) rowNr, 2, headerTextOfSongbookNumberGrid);
        WebElement cell = findElement(xpath);
        cell.click();
        String xpathInputField = XPathUtils.findInputByName("Liednr");
        WebElement inputField = findElement(xpathInputField);
        return inputField;
    }

    public WebElement findSpeichernButton() {
        String xpath = ExtJs5XPathUtils.findButtonByText("Speichern");
        return findElement(xpath);
    }

    private WebElement findElement(String xpath) {
        return AwaitFinder.awaitFindElement(this.getTest().getDriver(), xpath);
    }

    public WebElement waitAndFindMessageBoxWithOkButton() {
        WebElement button = findElement(ExtJs5XPathUtils.findMessageBoxButtonByText("OK"));
        return button;
    }

    public String readMessageBoxBodyTextOfOpenMessageBox() {
        WebElement body = findElement(ExtJs5XPathUtils.findMessageBoxBody());
        return body.getText();
    }

    public WebElement findRubrikComboBox() {
        return this.find(RUBRIK_XPATH);
    }
}
