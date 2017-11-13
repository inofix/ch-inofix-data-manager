package ch.inofix.data.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import ch.inofix.data.constants.PortletKeys;
import javax.portlet.Portlet;
import org.osgi.service.component.annotations.Component;

/**
 * DataManagerPortlet: MVC-Controller of the data-manager.
 *
 * @author Christian Berndt
 * @created 2017-09-10 16:32
 * @modified 2017-11-13 17:00
 * @version 1.0.9
 */
@Component(
    configurationPid = "ch.inofix.data.web.configuration.DataManagerConfiguration",
    immediate = true, 
    property = { 
        "com.liferay.portlet.css-class-wrapper=portlet-data-manager",
        "com.liferay.portlet.display-category=category.inofix", 
        "com.liferay.portlet.header-portlet-css=/css/main.css",
        "com.liferay.portlet.header-portlet-javascript=/js/main.js",
        "com.liferay.portlet.instanceable=false", 
        "com.liferay.portlet.preferences-owned-by-group=true",
        "com.liferay.portlet.preferences-unique-per-layout=false",
        "com.liferay.portlet.scopeable=true",
        "javax.portlet.display-name=Data Manager",
        "javax.portlet.init-param.template-path=/", 
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=" + PortletKeys.DATA_MANAGER, 
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" 
    }, 
    service = Portlet.class
)
public class DataManagerPortlet extends MVCPortlet {
}
    