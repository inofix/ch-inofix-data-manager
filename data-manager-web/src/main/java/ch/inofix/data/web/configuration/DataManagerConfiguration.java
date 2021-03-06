package ch.inofix.data.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-10 16:51
 * @modified 2017-11-21 20:36
 * @version 1.0.6
 *
 */
@Meta.OCD(
    id = "ch.inofix.data.web.configuration.DataManagerConfiguration", 
    localization = "content/Language", 
    name = "data.manager.configuration.name"
)
public interface DataManagerConfiguration {
    
    @Meta.AD(deflt = "id|name|timestamp|modified-date|user-name", required = false)
    public String[] columns();
    
    @Meta.AD(deflt = "", required = false)
    public String dataURL();
    
    @Meta.AD(deflt = "id", required = false)
    public String idField();
    
    @Meta.AD(deflt = "{}", required = false)
    public String jsonSchema();
    
    @Meta.AD(deflt = "lexicon", required = false)
    public String markupView();
    
    @Meta.AD(deflt = "name", required = false)
    public String nameField();

    @Meta.AD(deflt = "false", required = false)
    public boolean showSearchSpeeed();
    
    @Meta.AD(deflt = "timestamp", required = false)
    public String timestampField();

    @Meta.AD(deflt = "", required = false)
    public String userId();

    @Meta.AD(deflt = "", required = false)
    public String userName();
    
}
