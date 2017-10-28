package ccLinkClassServlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import cocluster.LinkClassClustersInterface;
import ccLinkClassUtil.ThreeTuple;
import ccLinkClassUtil.TwoTuple;
import ccLinkClassUtil.UserData;

@WebServlet("/CoClusServlet")
public class CoClusServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public CoClusServlet() {
		super();
	}
	
	private ArrayList<String> getOption(HttpServletRequest request) {
		String option = null;
		String json = null;
		if (request.getParameter("getAllLinkClass") != null) {
			json = request.getParameter("getAllLinkClass");
			option = "getAllLinkClass";
		}
		else if (request.getParameter("getMinLinkClass") != null) {
			json = request.getParameter("getMinLinkClass");
			option = "getMinLinkClass";
		}
		ArrayList<String> result = new ArrayList<String>();
		result.add(option);
		result.add(json);
		return result;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		ArrayList<String> optionList = getOption(request);
		String option = optionList.get(0);
		String json = optionList.get(1);
		
		HttpSession session = request.getSession();
		UserData userData = (UserData) session.getAttribute("userData");
		if (userData == null) {
			userData = new UserData();
			session.setAttribute("userData", userData);
		}
		if (option == null) {
		}
		else if (option.equals("getAllLinkClass")) {
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String sp = (String)reqtJSON.get("uri");
			JSONArray linkJA = new JSONArray();
			JSONArray classJA = new JSONArray();
			JSONArray l2cJA = new JSONArray();
			
			LinkClassClustersInterface lc = userData.getPreviousLinkClassClusters(sp);
			int existed = lc.getAllLinkedEntities();
			if (existed == 0) {
				JSONObject jsob = new JSONObject();
				jsob.put("result", 0);
				response.getWriter().print(jsob);
			}
			else {
				ThreeTuple<ArrayList<ArrayList<String>>,ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
				ArrayList<ThreeTuple<Integer, Integer, Double>>> allLinkClass = lc.getAllLinkClassCluster();
				ArrayList<ArrayList<String>> allLink = allLinkClass.getFirst();
				ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>> allClass = allLinkClass.getSecond();
				ArrayList<ThreeTuple<Integer, Integer, Double>> allL2C = allLinkClass.getThird();
				int i = 0;
				for (ArrayList<String> ls: allLink) {
					JSONObject linkJO = new JSONObject();
					JSONArray la = new JSONArray();
					for (String l: ls) {
						JSONObject jo = new JSONObject();
						jo.put("link", l);
						boolean d = false;
						if (l.startsWith("1")) {
							d = true;
						}
						jo.put("direction", d);
						la.add(jo);
					}
					linkJO.put("links", la);
					linkJO.put("lid", i++);
					linkJA.add(linkJO);
				}
				i = 0;
				for (ArrayList<TwoTuple<String, HashSet<String>>> map: allClass) {
					JSONObject classJO = new JSONObject();
					JSONArray ca = new JSONArray();
					for (TwoTuple<String, HashSet<String>> c: map) {
						JSONObject jo = new JSONObject();
						jo.put("clas", c.getFirst());
						JSONArray ea = new JSONArray();
						for (String e: c.getSecond()) {
							JSONObject eo = new JSONObject();
							eo.put("entity", e);
							ea.add(eo);
						}
						jo.put("entities", ea);
						jo.put("enum", c.getSecond().size());
						ca.add(jo);
					}
					classJO.put("classes", ca);
					classJO.put("cid", i++);
					classJA.add(classJO);
				}
				for (ThreeTuple<Integer, Integer, Double> tri: allL2C) {
					int lid = tri.getFirst();
					int cid = tri.getSecond();
					double w = tri.getThird();
					JSONObject line = new JSONObject();
					line.put("lid", lid);
					line.put("cid", cid);
					line.put("weight", w);
					l2cJA.add(line);
				}
				JSONObject jsob = new JSONObject();
				jsob.put("result", 1);
				jsob.put("links", linkJA);
				jsob.put("classes", classJA);
				jsob.put("l2c", l2cJA);
				response.getWriter().print(jsob);
			}
				
		}
		else if (option.equals("getMinLinkClass")) {
			String str = URLDecoder.decode(json, "utf-8");
			JSONObject reqtJSON = (JSONObject)JSONValue.parse(str);
			String sp = (String)reqtJSON.get("uri");
			JSONArray filters = (JSONArray)reqtJSON.get("filters");
			ArrayList<String> fts = new ArrayList<String>();
			for (int i=0; i<filters.size(); i++) {
				fts.add((String)filters.get(i));
			}
			JSONArray linkJA = new JSONArray();
			JSONArray classJA = new JSONArray();
			JSONArray l2cJA = new JSONArray();
			
			LinkClassClustersInterface lc = userData.getPreviousLinkClassClusters(sp);
			ThreeTuple<ArrayList<ArrayList<String>>,ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allLinkClass = lc.getMinLinkClassCluster(fts);
			ArrayList<ArrayList<String>> allLink = allLinkClass.getFirst();
			ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>> allClass = allLinkClass.getSecond();
			ArrayList<ThreeTuple<Integer, Integer, Double>> allL2C = allLinkClass.getThird();
			int i = 0;
			for (ArrayList<String> ls: allLink) {
				JSONObject linkJO = new JSONObject();
				JSONArray la = new JSONArray();
				for (String l: ls) {
					JSONObject jo = new JSONObject();
					jo.put("link", l);
					boolean d = false;
					if (l.startsWith("1")) {
						d = true;
					}
					jo.put("direction", d);
					la.add(jo);
				}
				linkJO.put("links", la);
				linkJO.put("lid", i++);
				linkJA.add(linkJO);
			}
			i = 0;
			for (ArrayList<TwoTuple<String, HashSet<String>>> map: allClass) {
				JSONObject classJO = new JSONObject();
				JSONArray ca = new JSONArray();
				for (TwoTuple<String, HashSet<String>> c: map) {
					JSONObject jo = new JSONObject();
					jo.put("clas", c.getFirst());
					JSONArray ea = new JSONArray();
					for (String e: c.getSecond()) {
						JSONObject eo = new JSONObject();
						eo.put("entity", e);
						ea.add(eo);
					}
					jo.put("entities", ea);
					jo.put("enum", c.getSecond().size());
					ca.add(jo);
				}
				classJO.put("classes", ca);
				classJO.put("cid", i++);
				classJA.add(classJO);
			}
			for (ThreeTuple<Integer, Integer, Double> tri: allL2C) {
				int lid = tri.getFirst();
				int cid = tri.getSecond();
				double w = tri.getThird();
				JSONObject line = new JSONObject();
				line.put("lid", lid);
				line.put("cid", cid);
				line.put("weight", w);
				l2cJA.add(line);
			}
			JSONObject jsob = new JSONObject();
			jsob.put("links", linkJA);
			jsob.put("classes", classJA);
			jsob.put("l2c", l2cJA);
			response.getWriter().print(jsob);	
		}
	}
	
	

}
