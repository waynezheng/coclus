<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CoClus</title>
<link rel="stylesheet" type="text/css" href="css/linkpattern.css" />
<link rel="stylesheet" type="text/css" href="css/bootstrap1.css" />
<script type="text/javascript" src="script/jquery.js"></script>
<script type="text/javascript" src="script/coclus.js"></script>


</head>

<body>
	<div class="form-search search">
			<a class="lp_title" href="index.jsp"><img src="icon/coclus2.png"></a>
            <input id="query" type="text" style="width:40%;" size="50" value="" placeholder="Enter a URI."  onblur="if(this.value == '') {this.style.color='#AAAAAA'}" onfocus="this.style.color='#333333'" class ="itext default" name="query">
            <button type="button"  class="ibutton_blue fb"  style="height:30px;" id="searchq">Go</button>
<!--     	<div class="example_div">
    		<strong>Examples:</strong>
    		<a class="example_a" href="coclus.jsp?sp=http://dbpedia.org/resource/Steven_Spielberg">Steven Spielberg</a>	
    		<a class="example_a" href="coclus.jsp?sp=http://dbpedia.org/resource/James_Cameron">James Cameron</a>
    		<a class="example_a" href="coclus.jsp?sp=http://dbpedia.org/resource/Tom_Cruise">Tom Cruise</a>
    		
    	</div> -->

    </div>
	<div class="linkpattern">	
		<div class="filterBox itagBox" style="display:block;">
		<div class="filter_box"></div>
		</div>
		
		<div class="main-cntr" style="float:left">
		<div class="al"></div>
		<div class="view-content" id="link" style="padding:10px;">	
		</div>
		<div class="line" id="line" style="padding:10px;">
		</div>
		<div class="view-content" id="class" style="padding:10px;">
		</div>	
		<div class="view-content" id="entity" style="padding:10px;">
		</div>	
	</div>
	<span class="input_uri" style="display:none"></span>
</body>

</html>