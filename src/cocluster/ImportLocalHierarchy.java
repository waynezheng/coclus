package cocluster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.TreeMap;


public class ImportLocalHierarchy {
	public HashSet<String> getSupClassTypes(String uri, Connection conn){
		HashSet<String> typeset = new HashSet<String>();
		try {
				PreparedStatement ps = conn.prepareStatement("SELECT o from yago_taxonomy "+
					     "where  s=? ");
				ps.setString(1,uri);
				java.sql.ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String class_uri = rs.getString("o");
					typeset.add(class_uri);
				}
				rs.close();     
				ps.close();	
		    }
			catch (SQLException e) {
					e.printStackTrace();
			}	
		
		return typeset;
	}

	public HashSet<String> getAncestorsOfClass(String uri, Connection conn){
		HashSet<String> ancestors=new HashSet<String>();
			HashSet<String> needfetchtypes = new HashSet<String>();
			HashSet<String> newneedfetchtypes = new HashSet<String>();
			HashSet<String> firsttypes = new HashSet<String>();
			firsttypes=getSupClassTypes(uri, conn);
			if(firsttypes.size()!=0){
				for(String t:firsttypes){
					needfetchtypes.add(t);
					ancestors.add(t);
				}
				while(!needfetchtypes.isEmpty()){
					for(String type:needfetchtypes){
						HashSet<String> temptypes = new HashSet<String>();
						temptypes=getSupClassTypes(type,conn);
						if(temptypes!=null&&!temptypes.isEmpty()){
							newneedfetchtypes.addAll(temptypes);
							ancestors.addAll(temptypes);
						}else{
							newneedfetchtypes.clear();break;
						}
					}
					needfetchtypes.clear();
					if(newneedfetchtypes.isEmpty()) {break;}
					for(String t:newneedfetchtypes){
						needfetchtypes.add(t);
					}
				    newneedfetchtypes.clear();
				}
			}	
		return ancestors;
	}

	public static HashSet<String> getSubClassTypes(String uri,Connection conn){
		HashSet<String> typeset = new HashSet<String>();
			try {
				PreparedStatement ps = conn.prepareStatement("SELECT s from yago_taxonomy "+
					     "where  o=? ");
				ps.setString(1,uri);
				java.sql.ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String subclass_uri = rs.getString("s");
					typeset.add(subclass_uri);
				}
				rs.close();     
				ps.close();
		    }
			catch (SQLException e) {
				e.printStackTrace();
			}	
		return typeset;
	}
	
	public  double getNodeshortestDepthOfClass2DB(String c,Connection conn){
		double depth=2.0;
		HashSet<String> needfetchtypes = new HashSet<String>();
				HashSet<String> newneedfetchtypes = new HashSet<String>();
				HashSet<String> firsttypes = new HashSet<String>();
				firsttypes=getSupClassTypes(c, conn);
				if(firsttypes.size()==0){
						depth=2.0;
				}else{
					for(String t:firsttypes){
						needfetchtypes.add(t);
					}
					while(!needfetchtypes.isEmpty()){
						for(String type:needfetchtypes){
							HashSet<String> temptypes = new HashSet<String>();
							temptypes=getSupClassTypes(type,conn);
							if(temptypes!=null&&!temptypes.isEmpty()){
								newneedfetchtypes.addAll(temptypes);
							}else{
								newneedfetchtypes.clear();break;
							}
						}
						depth++;
						needfetchtypes.clear();
						if(newneedfetchtypes.isEmpty()) {break;}
						for(String t:newneedfetchtypes){
							needfetchtypes.add(t);
						}
					    newneedfetchtypes.clear();
					}
				}
		return depth;
	}
	

	
	public HashSet<String> getAncestorOfClassFromDB(String c, Connection conn){
		HashSet<String> ancestors=new HashSet<String>();
		String ancestorString="";
			try {
					PreparedStatement ps = conn.prepareStatement("SELECT ancestor from dbpedia_ontology_info "+
							"where uri=?");
					ps.setString(1,c);
					java.sql.ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						ancestorString = rs.getString("ancestor");
					}
					rs.close();     
					ps.close();	
		    }
			catch (SQLException e) {
					e.printStackTrace();
			}	
		if(ancestorString!=null&&!ancestorString.equals("")){
			 String[] aa = ancestorString.split(";");
			 for (int i = 0 ; i <aa.length ; i++ ) {
				 if(!aa[i].equals("")){
					 ancestors.add(aa[i]);
				 } 
			 }
		}
		return ancestors;
	}
	
	
	public HashSet<String> getMinimalClassesFromDB(HashSet<String> typeset, Connection conn){
		HashSet<String> temptypeset=new HashSet<String>();
		
		TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>(); 
		for(String c:typeset){
			int depth=(int) getDepthOfClassFromDB(c,conn);
			treeMap.put(depth, c); 
		}
		NavigableSet<Integer> nset=treeMap.descendingKeySet();
		int n=6;
    	for(Integer depth:nset){
    		if(n>0){
    			String minc=treeMap.get(depth);
    			temptypeset.add(minc);
    			n--;
    		}
    		
    	}
		return temptypeset;
	}
	
	public static HashSet<String> getMinimalClasses(HashSet<String> typeset){
		HashSet<String> temptypeset=new HashSet<String>();
		for(String t:typeset){
			temptypeset.add(t);
		}		
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/dbpedia38";
		String user = "root";
		String password = "1234";
		try {
			Class.forName(driver);
			Connection conn = null;
			try {
				conn = DriverManager.getConnection(url, user, password);
				for(String t:typeset){
					HashSet<String> subClassTypes=new HashSet<String>();
					subClassTypes=getSubClassTypes(t,conn);
					if(!subClassTypes.isEmpty()){
						for(String s1:subClassTypes){
							if(temptypeset.contains(s1)){
								temptypeset.remove(t);
							}
						}
					}
				}
				conn.close();
		    }
			catch (SQLException e) {
					e.printStackTrace();
			}	
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return temptypeset;
	}
	
	public int getDepthOfClassFromDB(String c, Connection conn){
		int depth=0;
			try {
					PreparedStatement ps = conn.prepareStatement("SELECT depth from dbpedia_ontology_info "+
							"where uri=?");
					ps.setString(1,c);
					java.sql.ResultSet rs = ps.executeQuery();//灏嗘煡璇㈢粨鏋滄斁鍒癛esultSet绫诲０鏄庣殑瀵硅薄RS閲�
					while (rs.next()) {
						depth = rs.getInt("depth");
					}
					rs.close();     
					ps.close();	
		    }
			catch (SQLException e) {
					e.printStackTrace();
			}
		return depth;
	}
	
	public int getDepthOfLCAFromDB(String a, String b, Connection conn){
		int depth=0;
		HashSet<String> aancestor=new HashSet<String>();
		aancestor=getAncestorOfClassFromDB(a, conn);
		HashSet<String> bancestor=new HashSet<String>();
		bancestor=getAncestorOfClassFromDB(b, conn);
		HashSet<String> LCA=new HashSet<String>();
		
		if(aancestor.contains(b)){
			depth=getDepthOfClassFromDB(b, conn);
		}else{
			if(bancestor.contains(a)){
				depth=getDepthOfClassFromDB(a, conn);
			}else{
				LCA.addAll(aancestor);
				LCA.retainAll(bancestor);
				if(LCA.size()>0){
					int maxdepth=0;
					for(String c:LCA){
						int cDepth=getDepthOfClassFromDB(c, conn);
						if(cDepth>maxdepth){
							maxdepth=cDepth;
						}
					}
					depth=maxdepth;
				}
			}
		}
		return depth;
	}
	
	public void insertClassDepth2DB() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://114.212.86.204:3306/dbpedia2014";
		String username = "jdjiang";
		String password = "jdjiang";
		HashMap<String,Integer> entity2DepthrMap = new HashMap<String,Integer>();
		try {
			Class.forName(driver);
			Connection conn = null;
			int begin = 1;
			int end = 10000;
			while (end < 460000) {
				try {
					conn = DriverManager.getConnection(url, username, password);
					PreparedStatement ps = conn.prepareStatement("SELECT uri from yago_type_info where id >= ? and id <= ?");
						ps.setInt(1, begin);
						ps.setInt(2, end);
						java.sql.ResultSet rs = ps.executeQuery();
						while (rs.next()) {
							String class_uri = rs.getString("uri");
							entity2DepthrMap.put(class_uri, 2);
						}
						
					
						for(String c:entity2DepthrMap.keySet()){
							double depth=2.0;
							depth=getNodeshortestDepthOfClass2DB(c, conn);
							ps = conn.prepareStatement("UPDATE  yago_type_info set depth =? " +
			    	        		"where uri=?");
							ps.setInt(1,(int)depth);
							ps.setString(2,c);
						    ps.executeUpdate();
					        ps.close();
						}
					end += 10000;
					begin += 10000;
					rs.close();
					rs = null;
					ps.close();
					ps = null;
					conn.close();
					conn = null;
					conn = DriverManager.getConnection(url, username, password);
			    }
				catch (SQLException e) {
						e.printStackTrace();
				}	
			} 
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	public void insertClassAncestor2DB() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://114.212.86.204:3306/dbpedia2014";
		String username = "jdjiang";
		String password = "jdjiang";
		HashMap<String, HashSet<String>> entity2AncestorrMap = new HashMap<String, HashSet<String>>();
		try {
			Class.forName(driver);
			Connection conn = null;
			int begin = 1;
			int end = 10000;
			while (end < 460000) {
				try {
				    conn = DriverManager.getConnection(url, username, password);
				    PreparedStatement ps = conn.prepareStatement("SELECT uri from yago_type_info where id >= ? and id <= ?");
					ps.setInt(1, begin);
					ps.setInt(2, end);
					java.sql.ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						String class_uri = rs.getString("uri");
						HashSet<String> ancestor=new HashSet<String>();
						entity2AncestorrMap.put(class_uri, ancestor);
					}
				
					for(String c:entity2AncestorrMap.keySet()){
						HashSet<String> ancestor=new HashSet<String>();
						ancestor=getAncestorsOfClass(c, conn);
						if(ancestor.size()>0){
							String ancestorString =""; 
							for(String cc:ancestor){
								ancestorString=ancestorString+";"+cc;
							}
							ps = conn.prepareStatement("UPDATE  yago_type_info set ancestor =? " +
		    	        		"where uri=?");
							ps.setString(1,ancestorString);
							ps.setString(2,c);
							ps.executeUpdate();
							ps.close();
						}
					}
					end += 10000;
					begin += 10000;
					rs.close();
					rs = null;
					ps.close();
					ps = null;
					conn.close();
					conn = null;
					conn = DriverManager.getConnection(url, username, password);
		    }catch (SQLException e) {
					e.printStackTrace();
			}	
	  	} 
	  }
		catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}	
	}
	
	public void insertClassSim2DB() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://114.212.86.204:3306/dbpedia2014";
		String username = "jdjiang";
		String password = "jdjiang";
		HashMap<String, HashSet<String>> type2AncestorrMap = new HashMap<String, HashSet<String>>();
		HashSet<String> alltype = new HashSet<String>();
		try {
			Class.forName(driver);
			Connection conn = null;
			
		    try {
				conn = DriverManager.getConnection(url, username, password);
				PreparedStatement ps = conn.prepareStatement("SELECT uri, ancestor from dbpedia_ontology_info");
				java.sql.ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String class_uri = rs.getString("uri");
					String ancestorString = rs.getString("ancestor");
					if(ancestorString!=null&&!ancestorString.equals("")){
						 alltype.add(class_uri);
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			int begin = 1;
			int end = 100;
			while (end < 750) {
				try {		    
					PreparedStatement ps = conn.prepareStatement("SELECT uri, ancestor from dbpedia_ontology_info where id >= ? and id <= ?");
					ps.setInt(1, begin);
					ps.setInt(2, end);
					java.sql.ResultSet rs = ps.executeQuery();//灏嗘煡璇㈢粨鏋滄斁鍒癛esultSet绫诲０鏄庣殑瀵硅薄RS閲�
					while (rs.next()) {
						String class_uri = rs.getString("uri");
						HashSet<String> ancestors=new HashSet<String>();
						String ancestorString = rs.getString("ancestor");
						if(ancestorString!=null&&!ancestorString.equals("")){
							 String[] aa = ancestorString.split(";");
							 for (int i = 0 ; i <aa.length ; i++ ) {
								 if(!aa[i].equals("")){
									 ancestors.add(aa[i]);
								 } 
							 }
							 type2AncestorrMap.put(class_uri, ancestors);
						}
					}
				
					for(String c1:type2AncestorrMap.keySet()){
						for(String c2:alltype){
							double sim=0.0;
							sim=LowestCommSuperDistance.
									getSimilarity(c1, c2, conn);
							ps = conn.prepareStatement("insert into dbpedia_type_similarity(type1, type2, sim) values(?,?,?)");
								ps.setString(1,c1);
								ps.setString(2,c2);
								ps.setDouble(3,sim);
								ps.executeUpdate();
								ps.close();
						}
					}
					end += 100;
					begin += 100;
					rs.close();
					rs = null;
					ps.close();
					ps = null;
					conn.close();
					conn = null;
					conn = DriverManager.getConnection(url, username, password);
		    }catch (SQLException e) {
					e.printStackTrace();
			}	
	  	} 
	  }
		catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}	
	}
	
	
	
	public void insertSomeClassSim2DB() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://114.212.86.204:3306/dbpedia2014";
		String username = "jdjiang";
		String password = "jdjiang";
		HashSet<String> alltype = new HashSet<String>();
		HashSet<String> alltype0 = new HashSet<String>();
		HashSet<String> havedtype = new HashSet<String>();
		try {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Connection conn = null;
		    try {
				conn = DriverManager.getConnection(url, username, password);
				PreparedStatement ps = conn.prepareStatement("SELECT uri, ancestor from dbpedia_ontology_info");
				java.sql.ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String class_uri = rs.getString("uri");
					String ancestorString = rs.getString("ancestor");
					if(ancestorString!=null&&!ancestorString.equals("")){
						
						 alltype0.add(class_uri);
					}
				}
			    HashSet<String> alltype3 = new HashSet<String>();
			    alltype3.add("http://dbpedia.org/ontology/WinterSportPlayer");
			    alltype3.add("http://dbpedia.org/ontology/AdultActor");
			    alltype3.add("http://dbpedia.org/ontology/SnookerWorldRanking");
			    alltype3.add("http://dbpedia.org/ontology/Guitar");
			    alltype3.add("http://dbpedia.org/ontology/NetballPlayer");
			    alltype3.add("http://www.w3.org/2002/07/owl#Thing");
			    alltype3.add("http://dbpedia.org/ontology/HumanGene");
			    alltype3.add("http://dbpedia.org/ontology/Book");
			    alltype3.add("http://dbpedia.org/ontology/RadioControlledRacingLeague");
			    alltype3.add("http://dbpedia.org/ontology/PersonalEvent");
			    alltype3.add("http://dbpedia.org/ontology/SportCompetitionResult");
			    alltype3.add("http://dbpedia.org/ontology/Tournament");
			    alltype3.add("http://dbpedia.org/ontology/EducationalInstitution");
			    alltype3.add("http://dbpedia.org/ontology/Lighthouse");
			    alltype3.add("http://dbpedia.org/ontology/RaceHorse");
				
			    for(String c1:alltype3){
					for(String c2:alltype0){
						double sim=0.0;
						sim=LowestCommSuperDistance.
								getSimilarity(c1, c2, conn);
						ps = conn.prepareStatement("update dbpedia_type_similarity set sim=? where type1=? and type2=?");
						ps.setDouble(1,sim);
						ps.setString(2,c1);
						ps.setString(3,c2);
						ps.executeUpdate();
						ps.close();
						
						PreparedStatement ps1 = conn.prepareStatement("update dbpedia_type_similarity set sim=? where type1=? and type2=?");ps1.setString(1,c2);
						ps1.setDouble(1,sim);
						ps1.setString(2,c2);
						ps1.setString(3,c1);
						ps1.executeUpdate();
						ps1.close();
					}
				}
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			
		}
	}
	
	
	public static HashSet<String> add2needFilterUriSet(){
    	HashSet<String> needfilterUriSet=new HashSet<String>();
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/mbox");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/workplaceHomepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/workInfoHomepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/homepage");
		needfilterUriSet.add("http://usefulinc.com/ns/doap#homepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/schoolHomepage");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/img");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/page");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/weblog");
		needfilterUriSet.add("http://www.w3.org/2002/07/owl#sameAs");
		needfilterUriSet.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		needfilterUriSet.add("http://www.w3.org/1999/xhtml/vocab#stylesheet");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/isPrimaryTopicOf");
		needfilterUriSet.add("http://semantic-mediawiki.org/swivt/1.0#page");
		needfilterUriSet.add("http://www.w3.org/ns/prov#wasDerivedFrom");	
		needfilterUriSet.add("http://dbpedia.org/property/website");
		needfilterUriSet.add("http://dbpedia.org/property/hasPhotoCollection");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageUsesTemplate");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageExternalLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageInterLanguageLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageRedirects");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageDisambiguates");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageExternalLink");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageInterLanguageLink");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageRedirects");
		needfilterUriSet.add("http://dbpedia.org/property/wikiPageDisambiguates");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageRevisionLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageEditLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageHistoryLink");
		needfilterUriSet.add("http://dbpedia.org/ontology/wikiPageWikiLink");
		needfilterUriSet.add("http://it.dbpedia.org/property/wikiPageUsesTemplate");
		needfilterUriSet.add("http://fr.dbpedia.org/property/wikiPageUsesTemplate");	
		needfilterUriSet.add("http://purl.org/dc/terms/subject");
		needfilterUriSet.add("http://dbpedia.org/property/subject");
		needfilterUriSet.add("http://www.w3.org/2004/02/skos/core#subject");
		needfilterUriSet.add("http://purl.org/dc/elements/1.1/subject");
		needfilterUriSet.add("http://dbpedia.org/property/type");
		needfilterUriSet.add("http://dbpedia.org/ontology/type");
		needfilterUriSet.add("http://dbpedia.org/ontology/assembly");
		needfilterUriSet.add("http://dbpedia.org/ontology/genre");		
		needfilterUriSet.add("http://dbpedia.org/property/wordnet_type");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/depiction");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/primaryTopic");
		needfilterUriSet.add("http://dbpedia.org/ontology/thumbnail");
		needfilterUriSet.add("http://dbpedia.org/ontology/termPeriod");
		needfilterUriSet.add("http://data.nytimes.com/elements/topicPage");
		needfilterUriSet.add("http://schema.org/about");
		needfilterUriSet.add("http://www.w3.org/2006/03/wn/wn20/schema/hyponymOf");
		needfilterUriSet.add("http://creativecommons.org/ns#attributionURL");
		needfilterUriSet.add("http://de.dbpedia.org/property/wikiPageUsesTemplate");
		needfilterUriSet.add("http://fr.dbpedia.org/property/wikiPageUsesTemplate");			
		needfilterUriSet.add("http://d-nb.info/standards/elementset/gnd#gndSubjectCategory");
		needfilterUriSet.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#seeAlso");
		needfilterUriSet.add("http://www.w3.org/2000/01/rdf-schema#seeAlso");
		needfilterUriSet.add("http://www.semanlink.net/2001/00/semanlink-schema#tag");
		needfilterUriSet.add("http://data.linkedct.org/vocab/has_provenance");

		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/d0.owl#CognitiveEntity");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/d0.owl#Location");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#BiologicalObject");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Collection");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Collective");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Concept");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Configuration");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Description");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#DesignedArtifact");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Entity");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Event");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#FunctionalSubstance");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#InformationEntity");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#InformationObject");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#NaturalPerson");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organism");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#PhysicalBody");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#PlanExecution");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Quality");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Situation");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#SocialPerson");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#SpaceRegion");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#TimeInterval");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#UnitOfMeasure");
		needfilterUriSet.add("http://schema.org/Festival");
		needfilterUriSet.add("http://schema.org/MusicGroup");
		needfilterUriSet.add("http://schema.org/Organization");
		needfilterUriSet.add("http://schema.org/Product");
		needfilterUriSet.add("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Agent");
		needfilterUriSet.add("http://wikidata.dbpedia.org/resource/Q5");
		needfilterUriSet.add("http://www.w3.org/2002/07/owl#Thing");
		needfilterUriSet.add("http://xmlns.com/foaf/0.1/Person");
		needfilterUriSet.add("http://dbpedia.org/ontology/Agent");
		needfilterUriSet.add("0http://dbpedia.org/ontology/occupation");
		needfilterUriSet.add("1http://dbpedia.org/ontology/occupation");
		needfilterUriSet.add("http://dbpedia.org/ontology/religion");
		return needfilterUriSet;
	}

}
