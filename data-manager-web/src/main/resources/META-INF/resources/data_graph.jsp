<%--
    data_graph.jsp:  d3 powered channel graph.

	Created:	2017-12-19 00:06 by Christian Berndt
    Modified: 	2017-12-19 16:32 by Christian Berndt
    Version:  	1.0.1
--%>

<%@ include file="/init.jsp"%>

<div class="container-fluid-1280">   

data_graph.jsp v.6   

    <div id="<portlet:namespace />-root" style="cursor:pointer;"></div>

    <aui:script require="data-manager-web@1.0.0">
        dataManagerWeb100.default('<portlet:namespace />-root');
    </aui:script>

</div>
