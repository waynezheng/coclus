$(document).ready(function() {
	coclus_servlet = "/coclus/CoClusServlet";
	var url = window.location.href;
	$(".form-search").find("#query").val(getSpFromUrl(url));
	var startpoint = getSpFromUrl(url);
	$(".input_uri").html(startpoint);
	getAllLinkClass();
	$("#searchq").live("click", function() {
		var input = $(".form-search").find("#query");
		uri = $(input).val();
		window.open(getHrefForLinkPattern(uri), "_self");
		$(".input_uri").html(uri);
		getAllLinkClass();
	});
	
	$("#searchq_index").live("click", function() {
		var input = $(".form-search").find("#query");
		uri = $(input).val();
		window.open(getHrefForLinkPattern(uri), "_self");
		$(".input_uri").html(uri);
		getAllLinkClass();
	});
	
	//click link pattern to filter linked entities
	$(".cluster").live("click", function() {
		$("#entity").html("");
		filterMembersByClusters($(this));
	});
	
	//remove a type filter
	$(".itag.filter").live("click",function(){
		var box = $(".filter_box");
		if(box.find("a.itag.filter").length>0){
		}else{
			box.html("");
		}
		$(this).remove();
		$("#entity").html("");	
		filterMembersByClusters();
	});
	
	//remove all type filters and return the original members
	$(".itag.main").live("click",function(){
		var box = $(".filter_box");
		box.html("");
		getAllLinkClass();
	});
	
	$(".entities").live("click", function(e){
		e.stopPropagation();		
		var ul = $(this).parent().children(".cell-list");
		var li = $(this).parent().find(".member");
		var uri = "";
		for (var i=0; i<li.length; i++) {
			uri += $(li.get(i)).attr("uri");
			uri += ";";
		}
		uri = uri.substring(0, uri.length-1);
		$("#entity").html("");	
		var button = $("<button class='browse_all ibutton_blue fb' style='height:22px;'>Browse all</button>").appendTo($("#entity"));
		var ul_new = $("<ul></ul>").appendTo($("#entity"));		
		button.attr("val", uri);
		ul_new.html(ul.html());
	});
	
	$(".browse_all").live("click", function() {
		window.open(getHrefForLinkPattern($(this).attr("val")), "_self");
	});
	
});

function getCurrentFilters() {
	var props = new Array();
	var filterbox = $(".filter_box");
	var filters = filterbox.children(".filter");
	for (var i=0; i<filters.length; i++) {
		var plist = $(filters[i]).find(".prop");
		for (var j=0; j<plist.length; j++) {
			props.push($(plist[j]).html());
		}
	}
	return props;
}

function filterMembersByClusters(ele) {
	if (ele) {
		var label = $(ele).find(".member_a_label").first().html();
		var prop_list = $(ele).find(".prop_list").html();
		var box = $(".filter_box");
		if (box.find("a.itag").length > 0) {
			
		}
		else {
			var home = $("<a class='main itag' title='clear filter'></a>").appendTo(box);
			home.html("<span style='font-size:15px;'>Filter path> </span>");
		}
		var tag = $("<a class='itag filter'></a>");
		tag.html(label);
		tag.find(".member_a_label").removeClass("member_a_label");		
		var close = $("<span class='close-btn close-icon'></span>").appendTo(tag);
		var props = $("<ul class='prop_list' style='display:none'></ul>").appendTo(tag);
		props.html(prop_list);
		tag.appendTo(box);
	}
	var filters = getCurrentFilters();
	var uri = $(".input_uri").html();
	var data = {};
	data["uri"] = uri;
	data["filters"] = filters;
	data = JSON.stringify(data);
	data = ("getMinLinkClass=" + encodeURIComponent(data));
	$.ajax({
		url: coclus_servlet,
		async: true,
		data: data,
		dataType: "json",
		type: "post",
		beforeSend: function() {},
		success: function(jsob) {
			// get min links
			var links = jsob.links;
			var link_content = $("#link").html("");
			
			for (var i=0; i<links.length; i++) {
				var link = links[i].links;
				var cluster = $("<div class='cluster' id="+links[i].lid+"></div>");
				var members = $("<ul class='member'></ul>").appendTo(cluster);
				var link_uri = $("<ul class='prop_list' style='display:none;'></ul>").appendTo(cluster);
				// set first as cluster label
				var l = link[0].link;
				var li = $("<li class='member'></li>");
				var a = $("<a class='member_a_label'></a>").appendTo(li);
				if (link[0].direction) {
					var s = $("<span>is </span><span class='member_a_label'>"+getLocalNameFromUri(l)+"</span><span> of</span>");
					a.html(s);
				}
				else {
					a.html(getLocalNameFromUri(l));
				}
				var li_uri = $("<li class='prop'>"+l+"</li>").appendTo(link_uri);
				li.appendTo(members);
				// others				
				for (var j=1; j<link.length; j++) {
					var l = link[j].link;
					var li = $("<li class='member'></li>");
					var a = $("<a class='member_a'></a>").appendTo(li);
					if (link[j].direction) {
						var s = $("<span>is </span><span class='member_a'>"+getLocalNameFromUri(l)+"</span><span> of</span>");
						a.html(s);
					}
					else {
						a.html(getLocalNameFromUri(l));
					}
					
					var li_uri = $("<li class='prop'>"+l+"</li>").appendTo(link_uri);
					li.appendTo(members);	
				}
				cluster.appendTo(link_content);
			}
			
			// get min classes
			var classes = jsob.classes;
			var clas_content = $("#class").html("");
			for (var i=0; i<classes.length; i++) {
				var clas = classes[i].classes;
				var cluster = $("<div class='cluster' id="+classes[i].cid+"></div>");
				var members = $("<ul class='member'></ul>").appendTo(cluster);
				var clas_uri = $("<ul class='prop_list' style='display:none;'></ul>").appendTo(cluster);
				// set first as cluster label
				var c = clas[0].clas;
				var li = $("<li class='member'></li>");
				var a = $("<a class='member_a'></a>").appendTo(li);
				var s = $("<span class='member_a_label'>"+getLocalNameFromUri(c)+"</span><span class='entities'> ("+clas[0].enum+")</span>");
				a.html(s);
				var e_ul = $("<ul class='cell-list' style='display:none;'></ul>").appendTo(a);
				var e = clas[0].entities;
				for (var k=0; k<e.length; k++) {
					var e_li = $("<li class='member' uri="+e[k].entity+"><a class='member_a' href="+getHrefForLinkPattern(e[k].entity)+">"+getLocalNameFromUri(e[k].entity)+"</a</li>").appendTo(e_ul);
				}
				var li_uri = $("<li class='prop'>"+c+"</li>").appendTo(clas_uri);
				li.appendTo(members);
				// others
				for (var j=1; j<clas.length; j++) {
					var c = clas[j].clas;
					var li = $("<li class='member'></li>");
					var a = $("<a class='member_a'></a>").appendTo(li);
					var s = $("<span class='member_a'>"+getLocalNameFromUri(c)+"</span><span class='entities'> ("+clas[j].enum+")</span>");
					a.html(s);
					var e_ul = $("<ul class='cell-list' style='display:none;'></ul>").appendTo(a);
					var e = clas[j].entities;
					for (var k=0; k<e.length; k++) {
						var e_li = $("<li class='member' uri="+e[k].entity+"><a class='member_a' href="+getHrefForLinkPattern(e[k].entity)+">"+getLocalNameFromUri(e[k].entity)+"</a</li>").appendTo(e_ul);
					}
					var li_uri = $("<li class='prop'>"+c+"</li>").appendTo(clas_uri);
					li.appendTo(members);
				}
				cluster.appendTo(clas_content);
			}
			var l2c = jsob.l2c;
			var line = "";
			for (var i=0; i<l2c.length; i++) {
				var lid = "#" + l2c[i].lid;
				var cid = "#" + l2c[i].cid;
				var w = l2c[i].weight;
				var link_div = $("#link").find(lid);
				var link_pos = link_div.offset();
				var clas_div = $("#class").find(cid);
				var clas_pos = clas_div.offset();
				var s_w = parseInt(link_div.css("width"));
				var s_h = parseInt(link_div.css("height"));
				var e_h = parseInt(clas_div.css("height"));
				line += drawLine(link_pos.left+s_w, link_pos.top+s_h/2, clas_pos.left, clas_pos.top+e_h/2, "#11B9CC");	
			}
			$("#line").html(line);
		}
	});
}

function getAllLinkClass() {
	var $target = $('.main-cntr');
	if (!$target.hasClass("loading-data")) {
		$target.addClass("loading-data");
	}
	
	var uri = $(".input_uri").html();
	var data = {};
	data["uri"] = uri;
	data = JSON.stringify(data);
	data = ("getAllLinkClass=" + encodeURIComponent(data));
	$.ajax({
		url: coclus_servlet,
		async: true,
		data: data,
		dataType: "json",
		type: "post",
		beforeSend: function() {},
		success: function(jsob) {
			$target.removeClass("loading-data");
			$target.children(".link-tab-lp").show();
			$target.children(".link-tab-lp").addClass("load-over");
			var input = $(".form-search").find("#query");
			
			if (jsob.result == 0) {
				console.log(0);
				$("<div class='alert-cocl'> Cant't find the uri<strong> "+$(input).val()+"</strong>.</div>").appendTo($(".al"));
			}
			else {
				var links = jsob.links;
				var link_content = $("#link").html("");
				// get all links
				for (var i=0; i<links.length; i++) {
					var link = links[i].links;
					var cluster = $("<div class='cluster' id="+links[i].lid+"></div>");
					var members = $("<ul class='member'></ul>").appendTo(cluster);
					var link_uri = $("<ul class='prop_list' style='display:none;'></ul>").appendTo(cluster);
					// set first as cluster label
					var l = link[0].link;
					var li = $("<li class='member'></li>");
					var a = $("<a class='member_a_label'></a>").appendTo(li);
					if (link[0].direction) {
						var s = $("<span>is </span><span class='member_a_label'>"+getLocalNameFromUri(l)+"</span><span> of</span>");
						a.html(s);
					}
					else {
						a.html(getLocalNameFromUri(l));
					}
					
					var li_uri = $("<li class='prop'>"+l+"</li>").appendTo(link_uri);
					li.appendTo(members);
					// others
					for (var j=1; j<link.length; j++) {
						var l = link[j].link;
						var li = $("<li class='member'></li>");
						var a = $("<a class='member_a'></a>").appendTo(li);
						if (link[j].direction) {
							var s = $("<span>is </span><span class='member_a'>"+getLocalNameFromUri(l)+"</span><span> of</span>");
							a.html(s);
						}
						else {
							a.html(getLocalNameFromUri(l));
						}
						
						var li_uri = $("<li class='prop'>"+l+"</li>").appendTo(link_uri);
						li.appendTo(members);	
					}
					cluster.appendTo(link_content);
				}
				
				// get all classes
				var classes = jsob.classes;
				var clas_content = $("#class").html("");
				for (var i=0; i<classes.length; i++) {
					var clas = classes[i].classes;
					var cluster = $("<div class='cluster' id="+classes[i].cid+"></div>");
					var members = $("<ul class='member'></ul>").appendTo(cluster);
					var clas_uri = $("<ul class='prop_list' style='display:none;'></ul>").appendTo(cluster);
					// set first as cluster label
					var c = clas[0].clas;
					var li = $("<li class='member'></li>");
					var a = $("<a class='member_a'></a>").appendTo(li);
					var s = $("<span class='member_a_label'>"+getLocalNameFromUri(c)+"</span><span class='entities'> ("+clas[0].enum+")</span>");
					a.html(s);
					var e_ul = $("<ul class='cell-list' style='display:none;'></ul>").appendTo(a);
					var e = clas[0].entities;
					for (var k=0; k<e.length; k++) {
						var e_li = $("<li class='member' uri="+e[k].entity+"><a class='member_a' href="+getHrefForLinkPattern(e[k].entity)+">"+getLocalNameFromUri(e[k].entity)+"</a</li>").appendTo(e_ul);
					}
					var li_uri = $("<li class='prop'>"+c+"</li>").appendTo(clas_uri);
					li.appendTo(members);
					// others
					for (var j=1; j<clas.length; j++) {
						var c = clas[j].clas;
						var li = $("<li class='member'></li>");
						var a = $("<a class='member_a'></a>").appendTo(li);
						var s = $("<span class='member_a'>"+getLocalNameFromUri(c)+"</span><span class='entities'> ("+clas[j].enum+")</span>");
						a.html(s);
						var e_ul = $("<ul class='cell-list' style='display:none;'></ul>").appendTo(a);
						var e = clas[j].entities;
						for (var k=0; k<e.length; k++) {
							var e_li = $("<li class='member' uri="+e[k].entity+"><a class='member_a' href="+getHrefForLinkPattern(e[k].entity)+">"+getLocalNameFromUri(e[k].entity)+"</a</li>").appendTo(e_ul);
						}
						var li_uri = $("<li class='prop'>"+c+"</li>").appendTo(clas_uri);
						li.appendTo(members);
					}
					cluster.appendTo(clas_content);
				}
				var l2c = jsob.l2c;
				var line = "";
				for (var i=0; i<l2c.length; i++) {
					var lid = "#" + l2c[i].lid;
					var cid = "#" + l2c[i].cid;
					var w = l2c[i].weight;
					var link_div = $("#link").find(lid);
					var link_pos = link_div.offset();
					var clas_div = $("#class").find(cid);
					var clas_pos = clas_div.offset();
					var s_w = parseInt(link_div.css("width"));
					var s_h = parseInt(link_div.css("height"));
					var e_h = parseInt(clas_div.css("height"));
					line += drawLine(link_pos.left+s_w, link_pos.top+s_h/2, clas_pos.left, clas_pos.top+e_h/2, "#11B9CC");	
				}
				$("#line").html(line);
			}
			
		}
	});
}


function getLocalNameFromUri(uri) {
	var ret = null;
	var pos = uri.lastIndexOf('#');
	if (pos >= 0) {
		ret = uri.substring(pos + 1);
		ret = decodeURIComponent(ret);
		ret = ret.replace(/_/g,' ');
		var local_name = yagoTrim(uri, ret);
		return local_name;
	}
	pos = uri.lastIndexOf('/');
	if (pos >= 0 && pos < uri.length - 1) {
		ret = uri.substring(pos + 1);
		ret = decodeURIComponent(ret);
		ret = ret.replace(/_/g,' ');
		var local_name = yagoTrim(uri, ret);
		return local_name;
	} else if (pos >= 0) {
		var cut = uri.substring(0, uri.length - 1);
		pos = cut.lastIndexOf('/');
		if (pos >= 0) {
			ret = cut.substring(pos + 1);
			ret = decodeURIComponent(ret);
			ret = ret.replace(/_/g,' ');
			var local_name = yagoTrim(uri, ret);
			return local_name;
		}
	}
}

function yagoTrim(uri, ret){
	if(ret != null && uri.indexOf("yago") > 0){
		ret = ret.replace(/[0-9]+\b/i, "");
	}
	return ret;
}

function getHrefForLinkPattern(uri) {
	return "coclus.jsp?sp=" + uri;
}

function getSpFromUrl(url) {
	if (url.indexOf("?") == -1)
		return "";
	var str = url.split("?")[1];
	var re = str.split("=")[1];
	return re;
}

function drawLine(x0, y0, x1, y1, color) {
	var rs = "";
	/*if (y0 == y1) {
		if (x0>x1) {
			var t=x0;
			x0=x1;
			x1=t;
		}  
		rs = "<span style='top:"+y0+"px;left:"+x0+"px;position:absolute;font-size:2px;background-color:"+color+";height:1px; width:"+Math.abs(x1-x0)+"px'></span>";
	}
	else if (x0 == x1){
		if (y0>y1){
			var t=y0;
			y0=y1;
			y1=t;
		} 
		rs = "<span style='top:"+y0+"px;left:"+x0+"px;position:absolute;font-size:1px;background-color:"+color+";width:1px;height:"+Math.abs(y1-y0)+"px'></span>";
	}
	else {*/
		var lx = x1-x0;
		var ly = y1-y0;
		var l = Math.sqrt(lx*lx+ly*ly);
		rs = new Array();
		for (var i=0;i<l;i+=8) {
			var p = i/l;
			var px = parseInt(x0 + lx*p);
			var py = parseInt(y0 + ly*p);
			rs[rs.length] = "<span style='top:"+py+"px;left:"+px+"px;height:2px;width:2px;position:absolute;font-size:1px;background-color:"+color+"'></span>";
		}
		rs = rs.join("");
//	}
	return rs;
}
