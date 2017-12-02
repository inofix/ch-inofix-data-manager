package ch.inofix.data.web.internal.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 *
 * @author Christian Berndt
 * @created 2017-09-27 11:47
 * @modified 2017-11-27 23:56
 * @version 1.0.1
 *
 */
public class MeasurementDisplayTerms extends DisplayTerms {

    public static final String CREATE_DATE = "createDate";
    public static final String DATA = "data";
    public static final String FROM = "from";
    public static final String GROUP_ID = "groupId";
    public static final String ID = "id";
    public static final String MEASUREMENT_ID = "measurementId";
    public static final String MODIFIED_DATE = "modifiedDate";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String UNTIL = "until";
    public static final String USER_NAME = "userName";

    public MeasurementDisplayTerms(PortletRequest portletRequest) {

        super(portletRequest);

        createDate = ParamUtil.getString(portletRequest, CREATE_DATE);
        data = ParamUtil.getString(portletRequest, DATA);
        from = ParamUtil.getLong(portletRequest, FROM);
        groupId = ParamUtil.getLong(portletRequest, GROUP_ID);
        id = ParamUtil.getString(portletRequest, ID);
        measurementId = ParamUtil.getLong(portletRequest, MEASUREMENT_ID);
        modifiedDate = ParamUtil.getString(portletRequest, MODIFIED_DATE);
        name = ParamUtil.getString(portletRequest, NAME);
        String statusString = ParamUtil.getString(portletRequest, STATUS);

        if (Validator.isNotNull(statusString)) {
            status = GetterUtil.getInteger(statusString);
        }
        until = ParamUtil.getLong(portletRequest, UNTIL);
        userName = ParamUtil.getString(portletRequest, USER_NAME);
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(long measurementId) {
        this.measurementId = measurementId;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUntil() {
        return until;
    }

    public void setUntilDate(long until) {
        this.until = until;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    protected String createDate;
    protected String data;
    protected long from;
    protected long groupId;
    protected String id;
    protected long measurementId;
    protected String modifiedDate;
    protected String name;
    protected int status;
    protected long until;
    protected String userName;

}
