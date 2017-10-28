<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CoClus</title>
<link rel="stylesheet" type="text/css" href="css/linkpattern.css" />
<script type="text/javascript" src="script/jquery.js"></script>
<script type="text/javascript" src="script/coclus.js"></script>

</head>
<body>
	<div style="width: 100%; padding-top: 112px;font-family: Helvetica; font-size: 14px;line-height: 20px;color: #333333;">
		<div style="text-align: center">
			<a href="index.jsp">
				<img src="icon/coclus.png" id="logo"/>
			</a>
			<div class="form-search index-search" style="margin-top:10px;">
              <input id="query" type="text" style="width: 30%; height:25px; font-size:16px; color: rgb(170, 170, 170);" size="80" value="" placeholder="Start exploring here. Enter a URI." onblur="if(this.value == '') {this.style.color='#AAAAAA'}" onfocus="this.style.color='#333333'" class="itext default ui-autocomplete-input" name="query" autocomplete="off"><span role="status" aria-live="polite" class="ui-helper-hidden-accessible"></span>
              <button type="button" class="ibutton_blue fb" style="height:30px" id="searchq_index">Go</button>
              <div class="example_div_index" style="text-align:center;">
    			<strong>Examples:</strong>
    			<a class="example_a" href="coclus.jsp?sp=http://dbpedia.org/resource/Steven_Spielberg">Steven Spielberg</a>	
    			<a class="example_a" href="coclus.jsp?sp=http://dbpedia.org/resource/James_Cameron">James Cameron</a>
    			<a class="example_a" href="coclus.jsp?sp=http://dbpedia.org/resource/Tom_Cruise">Tom Cruise</a>
    		</div>
        </div>
		</div>
		
		<div style="text-align: center; margin-bottom: 10px; margin-top:30px;clear:both">
			©2015 <a href="http://ws.nju.edu.cn/" style="color: #0088cc;text-decoration: none;"onclick="window.open(this.href); return false;"> The Websoft
				Research Group</a>, Nanjing University, P.R. China
		</div>
		
	</div>
</body>
</html>