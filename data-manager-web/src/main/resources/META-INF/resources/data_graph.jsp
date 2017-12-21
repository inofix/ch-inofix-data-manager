<%--
    data_graph.jsp:  d3 powered channel graph.

	Created:	2017-12-19 00:06 by Christian Berndt
    Modified: 	2017-12-21 20:52 by Christian Berndt
    Version:  	1.0.2
--%>

<%@ include file="/init.jsp"%>

<div class="container-fluid-1280">   

data_graph.jsp v.7

    <h1 id="click-me">Click on me!</h1>

    <p class="hover-me">Hover over me!</p>

    <p class="hover-me">OK now hover over here!</p>

    <p class="hover-me">Hover here too!</p>

    <aui:script require="data-manager-web@1.0.0">
        dataManagerWeb100.default();
    </aui:script>

</div>
