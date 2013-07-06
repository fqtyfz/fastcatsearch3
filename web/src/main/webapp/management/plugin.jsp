<%--
# Copyright (C) 2011 - 2013 Websquared, Inc.
# All rights reserved.
--%>

<%@ page contentType="text/html; charset=UTF-8"%> 

<%@page import="org.fastcatsearch.settings.IRSettings"%>
<%@page import="org.fastcatsearch.ir.config.IRConfig"%>
<%@page import="org.fastcatsearch.control.JobService"%>
<%@page import="org.fastcatsearch.ir.IRService"%>
<%@page import="org.fastcatsearch.service.KeywordService"%>
<%@page import="org.fastcatsearch.service.WebService"%>
<%@page import="org.fastcatsearch.db.DBService"%>
<%@page import="org.fastcatsearch.job.Job"%>
<%@page import="org.fastcatsearch.ir.util.Formatter"%>
<%@page import="org.fastcatsearch.management.ManagementInfoService"%>
<%@page import="org.fastcatsearch.server.CatServer"%>
<%@page import="org.fastcatsearch.statistics.StatisticsInfoService"%>
<%@page import="com.fastcatsearch.util.WebUtils"%>
<%@page import="org.fastcatsearch.cluster.*"%>
<%@page import="org.fastcatsearch.service.*"%>
<%@page import="org.fastcatsearch.plugin.*"%>

<%@page import="java.util.concurrent.ThreadPoolExecutor"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Date"%>
<%@page import="java.util.*"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	IRConfig irConfig = IRSettings.getConfig(true);
	
	PluginService pluginService = ServiceManager.getInstance().getService(PluginService.class);
	Collection<Plugin> plugins = pluginService.getPlugins();
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>FASTCAT 검색엔진 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<!--[if lte IE 6]>
<link href="<%=FASTCAT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/common.js"></script>
<script src="<%=FASTCAT_MANAGE_ROOT%>js/jquery-1.4.4.min.js" type="text/javascript"></script>
<script type="text/javascript">
	function alertMessage(){
		var message = "<%=message%>";
		if(message != "")
			alert(message);
	}

	function restartCatServer(){
		//managementService.jsp?cmd=3&component=0&cmd2=2
		var request = $.ajax({
		  url: "managementService.jsp",
		  type: "GET",
		  data: {cmd : "3", component : "0", cmd2 : "2"},
		  dataType: "html"
		});
		alert("서버를 재시작하였습니다.");
	}

	function restartServiceHandler(){
		//managementService.jsp?cmd=3&component=1&cmd2=2
		var request = $.ajax({
		  url: "managementService.jsp",
		  type: "GET",
		  data: {cmd : "3", component : "1", cmd2 : "2"},
		  dataType: "html"
		});
		alert("ServiceHandler를 재시작하였습니다.");
	}
	</script>
</head>

<body onload="alertMessage()">

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>관리</h3>
			<ul class="latest">
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/main.jsp">시스템상태</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/serverCluster.jsp">서버클러스터</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/plugin.jsp" class="selected">플러그인</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/searchEvent.jsp">이벤트내역</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/jobHistory.jsp">작업히스토리</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/account.jsp">계정관리</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/config.jsp">사용자설정</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/advConfig.jsp">고급설정보기</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/restore.jsp">복원</a></li>
			<li><a href="<%=FASTCAT_MANAGE_ROOT%>management/license.jsp">라이선스</a></li>
			</ul>
	</div>
	<div class="sidebox">
		<h3>도움말</h3>
			<ul class="latest">
			<li>컴포넌트상태 : 각 컴포넌트를 시작/정지/재시작가능하다.</li>
			<li>시스템정보 : 검색엔진이 설치된 운영체제의 시스템정보를 보여주며, JVM의 정보를 확인할 수 있다.</li>
			<li>서버상태 : 구동시간과 사용하고 있는 JVM메모리를 보여준다.</li>
			<li>작업실행기 상태 : 실행중인 작업과 수행한 작업을 보여준다. POOL사이즈는 작업쓰레드의 갯수이다.</li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

	<div id="mainContent">
	
	
	
	<h2>플러그인 리스트</h2>
	<div class="fbox">
	<table summary="색인결과" class="tbl02">
	<colgroup><col width="5%" /><col width="25%" /><col width="15%" /><col width="*" /><col width="10%" /></colgroup>
	<thead>
	<tr>
		<th class="first">번호</th>
		<th>플러그인명</th>
		<th>버전</th>
		<th>설명</th>
		<th>동작</th>
	</tr>
	</thead>
	<tbody>
	<%
	int number = 1;
	Iterator<Plugin> iterator = plugins.iterator();
	while(iterator.hasNext()){
		Plugin plugin = iterator.next();
		PluginSetting pluginSetting = plugin.getPluginSetting();
	%>
	<tr>
		<td class="first"><%=number++ %></td>
		<td><%=pluginSetting.getName() %></td>
		<td><%=pluginSetting.getVersion() %></td>
		<td><%=pluginSetting.getDescription() %></td>
		<td>
			<a href="#" class="btn_s">관리</a>
		</td>
	</tr>
	<%
	}
	%>
	</tbody>
	</table>
	</div>
	
	<p class="clear"></p>
	<!-- E : #mainContent --></div>

<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
