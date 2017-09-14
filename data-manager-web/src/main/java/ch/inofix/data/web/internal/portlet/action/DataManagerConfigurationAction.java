package ch.inofix.data.web.internal.portlet.action;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

import ch.inofix.data.constants.PortletKeys;

/**
 * @author Christian Berndt
 * @created 2017-09-14 17:28
 * @modified 2017-09-14 17:28
 * @version 1.0.0
 */
@Component(
    immediate = true,
    property = {"javax.portlet.name=" + PortletKeys.DATA_MANAGER},
    service = ConfigurationAction.class
)
public class DataManagerConfigurationAction extends DefaultConfigurationAction {

}
