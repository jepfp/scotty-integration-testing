package ch.adoray.scotty.acceptancetest.base.model;

import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.XPathUtils;
public class SongModel extends BaseModel {
    public static final String TITEL_XPATH = XPathUtils.findInputByName("Titel");
    public static final String SONG_LOADED_XPATH = XPathUtils.findDivByText("Lied vollständig geladen.");

    public SongModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findTitelField() {
        return this.find(TITEL_XPATH);
    }
    
    public WebElement findSongLoadedDiv() {
        return this.find(SONG_LOADED_XPATH);
    }

}
