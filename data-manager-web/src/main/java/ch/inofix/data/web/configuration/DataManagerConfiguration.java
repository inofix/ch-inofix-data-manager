package ch.inofix.data.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
*
* @author Christian Berndt
* @created 2017-09-10 16:51
* @modified 2017-09-10 16:51
* @version 1.0.0
*
*/
@Meta.OCD(id = "ch.inofix.data.web.configuration.DataManagerConfiguration", localization = "content/Language", name = "data.manager.configuration.name")
public interface DataManagerConfiguration {
    
    @Meta.AD(deflt = "lexicon", required = false)
    public String markupView();
    
    @Meta.AD(deflt = "false", required = false)
    public boolean showSearchSpeeed();
}
