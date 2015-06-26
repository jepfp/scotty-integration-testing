package ch.adoray.scotty.acceptancetest.base.model;

import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.appfoundation.automation.util.XPathUtils;
public class SongModel extends BaseModel {
    public static final String TITEL_XPATH = XPathUtils.findInputByName("Titel");
    public static final String SONG_LOADED_XPATH = XPathUtils.findDivByText("Lied vollständig geladen.");
    public static final String SONGSHEET_DELETED_XPATH = XPathUtils.findDivByText("Noten gelöscht.");
    public static final String SONGSHEET_DOWNLOAD_LINK_XPATH = XPathUtils.findLinkByText("Noten anzeigen.");
    public static final String SONGSHEET_NO_SONGSHEET_AVAILABLE_XPATH = XPathUtils.findDivByText("keine Noten vorhanden");
    public static final String SONGSHEET_DELETE_BUTTON_XPATH = ExtJs5XPathUtils.findButtonByText("Noten löschen");
    public static final String YES_BUTTON_IN_MESSAGE_BOX_XPATH = ExtJs5XPathUtils.findMessageBoxButtonByText("Yes");
    public static final String NO_BUTTON_IN_MESSAGE_BOX_XPATH = ExtJs5XPathUtils.findMessageBoxButtonByText("No");
    

    public SongModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findTitelField() {
        return this.find(TITEL_XPATH);
    }
    
    public WebElement findSongLoadedDiv() {
        return this.find(SONG_LOADED_XPATH);
    }
    
    public WebElement findSongsheetDownloadLink() {
        return this.find(SONGSHEET_DOWNLOAD_LINK_XPATH);
    }
    
    public WebElement findSongsheetNoSongsheetAvailable() {
        return this.find(SONGSHEET_NO_SONGSHEET_AVAILABLE_XPATH);
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

}
