package cocluster;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.TreeMap;

import ccLinkClassService.DBTypeLuceneIndexer;
import ccLinkClassService.LogLuceneIndexer;
import ccLinkClassUtil.ThreeTuple;
import ccLinkClassUtil.TwoTuple;

public class LinkClassClusters implements LinkClassClustersInterface{

	private String startpointString = "";
	public String getCurrentUri() {
		return this.startpointString;
	}
	
	public LinkClassClusters(String spString) {
		if(spString==null||spString.equals("")){
			numEntity=0;
		}else{
			HashSet<String> currentUris = new HashSet<String>();
			if(spString.contains(";")){
				startpointString=spString;
				String[] uris = spString.split(";");
				for(String uri:uris){
					currentUris.add(uri);
				}
			}else{
				startpointString=spString;
				currentUris.add(spString);
			}
			
			int num=0;
			for(String uri:currentUris){
				if(num<10){
					buildnavigationGraph(uri);
					num++;
				}
				
			}
			buildALLmatrix();

			buildsimLLmatrix();
			buildsimCCmatrix();
		}
	}
	
	
	public LinkClassClusters() {
	}

	public HashMap<String, HashMap<String, HashSet<String>>> link2entity2startuirMap= new HashMap<String, HashMap<String,  HashSet<String>>>();
	public HashMap<String, HashMap<String, Integer>> class2entity2numMap= new HashMap<String, HashMap<String, Integer>>();
	public HashMap<String, Integer> link2IndexMap= new HashMap<String, Integer>();
	public HashMap<Integer, String> index2LinkMap= new HashMap<Integer, String>();
	
	public HashMap<String, Integer> entity2IndexMap= new HashMap<String, Integer>();
	public HashMap<Integer, String> index2EntityMap= new HashMap<Integer, String>();
	
	public HashMap<String, Integer> class2IndexMap= new HashMap<String, Integer>();
	public HashMap<Integer, String> index2ClassMap= new HashMap<Integer, String>();
	
	public int numLink=0, numEntity=0, numClass=0; 
	
	public int Link2Entity[][], Class2Entity[][], Link2Class[][], Class2Link[][];
	public double simLE[][], simCE[][], simLC[][], simCL[][], newsimLC[][], simLL[][], simCC[][];
	
	public void buildnavigationGraph(String uri) {
		HashSet<String> needfilterUriSet=new HashSet<String>();
		needfilterUriSet=ImportLocalHierarchy.add2needFilterUriSet();
		
		HashMap<String, HashSet<String>> link2EntitiesValue=new HashMap<String, HashSet<String>>();
		link2EntitiesValue=LogLuceneIndexer.findProperty2ValueMap(uri);
		if(link2EntitiesValue.size()>0){
		for(String link:link2EntitiesValue.keySet()){
			if(needfilterUriSet.contains(link)) continue;
			int enu=0;
			for(String entity:link2EntitiesValue.get(link)){
				if(enu<50){
					HashSet<String> entityClasses=new HashSet<String>();
					entityClasses=DBTypeLuceneIndexer.getTypesOfentityFromIndex(entity);
					entityClasses.removeAll(needfilterUriSet);
					HashSet<String> newentityClasses=new HashSet<String>();
					for(String type:entityClasses){
						if(!type.contains("http://wikidata.dbpedia.org/")&&!type.contains("http://purl.org/")
								  &&!type.contains("http://schema.org/")&&!type.contains("http://dbpedia.org/ontology/Wikidata")){
							newentityClasses.add(type);
						}
					}
					if(newentityClasses.size()>0){
						if(link2entity2startuirMap.containsKey(link)){
							if(link2entity2startuirMap.get(link).containsKey(entity)){
								link2entity2startuirMap.get(link).get(entity).add(uri);
							}else{
								HashSet<String> euris=new HashSet<String>();
								euris.add(uri);
								link2entity2startuirMap.get(link).put(entity, euris);
							}
						}else{
							HashMap<String, HashSet<String>> innermap=new HashMap<String, HashSet<String>>();
							HashSet<String> euris=new HashSet<String>();
							euris.add(uri);
							innermap.put(entity, euris);
							link2entity2startuirMap.put(link, innermap);
						}
						
						for(String type:newentityClasses){
								if(class2entity2numMap.containsKey(type)){
									if(class2entity2numMap.get(type).containsKey(entity)){
										class2entity2numMap.get(type).put(entity, 1);
									}else{
										class2entity2numMap.get(type).put(entity, 1);
									}
								}else{
									HashMap<String, Integer> innermap=new HashMap<String, Integer>();
									innermap.put(entity, 1);
									class2entity2numMap.put(type, innermap);
								}
						}
						}
					enu++;
				}
			}	
		}
		}
	}//4 maps
	
	public void buildALLmatrix() {
		numLink=link2entity2startuirMap.size(); 
		numClass=class2entity2numMap.size();
		HashSet<String> allentities=new HashSet<String>();
		for(String link:link2entity2startuirMap.keySet()){
			for(String e:link2entity2startuirMap.get(link).keySet()){
				allentities.add(e);
			}	
		}
		numEntity=allentities.size(); 
		
		Link2Entity=new int[numLink][numEntity];
		Class2Entity=new int[numClass][numEntity];
		Link2Class=new int[numLink][numClass];
		Class2Link=new int[numClass][numLink];
		
		//Link2Entity
		for(String link : link2entity2startuirMap.keySet()){
			if(link2IndexMap.containsKey(link)){
				int indexOflink=link2IndexMap.get(link);
				for(String e:link2entity2startuirMap.get(link).keySet()){
					if(entity2IndexMap.containsKey(e)){
						int indexOfentity=entity2IndexMap.get(e);
						Link2Entity[indexOflink][indexOfentity]=link2entity2startuirMap.get(link).get(e).size();	
					}else{
						int indexOfentity= entity2IndexMap.size();
						entity2IndexMap.put(e, indexOfentity);
						index2EntityMap.put(indexOfentity, e);
						Link2Entity[indexOflink][indexOfentity]=link2entity2startuirMap.get(link).get(e).size();
					}
				}
			}else{
				int indexOflink=link2IndexMap.size();
				link2IndexMap.put(link, indexOflink);
				index2LinkMap.put(indexOflink, link);
				for(String e:link2entity2startuirMap.get(link).keySet()){
					if(entity2IndexMap.containsKey(e)){
						int indexOfentity=entity2IndexMap.get(e);
						Link2Entity[indexOflink][indexOfentity]=link2entity2startuirMap.get(link).get(e).size();
					}else{
						int indexOfentity= entity2IndexMap.size();
						entity2IndexMap.put(e, indexOfentity);
						index2EntityMap.put(indexOfentity, e);
						Link2Entity[indexOflink][indexOfentity]=link2entity2startuirMap.get(link).get(e).size();
					}
				}
			}
		}
		//Class2Entity
		for(String type : class2entity2numMap.keySet()){
			if(class2IndexMap.containsKey(type)){
				int indexOfclass=class2IndexMap.get(type);
				for(String e:class2entity2numMap.get(type).keySet()){
					if(entity2IndexMap.containsKey(e)){
						int indexOfentity=entity2IndexMap.get(e);
						Class2Entity[indexOfclass][indexOfentity]=class2entity2numMap.get(type).get(e);	
					}else{
						int indexOfentity= entity2IndexMap.size();
						entity2IndexMap.put(e, indexOfentity);
						index2EntityMap.put(indexOfentity, e);
						Class2Entity[indexOfclass][indexOfentity]=class2entity2numMap.get(type).get(e);	
					}
				}
			}else{
				int indexOfclass=class2IndexMap.size();
				class2IndexMap.put(type, indexOfclass);
				index2ClassMap.put(indexOfclass, type);
				for(String e:class2entity2numMap.get(type).keySet()){
					if(entity2IndexMap.containsKey(e)){
						int indexOfentity=entity2IndexMap.get(e);
						Class2Entity[indexOfclass][indexOfentity]=class2entity2numMap.get(type).get(e);	
					}else{
						int indexOfentity= entity2IndexMap.size();
						entity2IndexMap.put(e, indexOfentity);
						index2EntityMap.put(indexOfentity, e);
						Class2Entity[indexOfclass][indexOfentity]=class2entity2numMap.get(type).get(e);
//								class2entity2numMap.get(type).get(e);
					}
				}
			}
		}
		//Link2Class 
		for(int l=0;l<numLink;l++){
			for(int c=0;c<numClass;c++){
				int maxnum=0;
				for(int e=0; e<numEntity;e++){
				  if(Link2Entity[l][e]!=0&&Class2Entity[c][e]!=0){
						if(Link2Entity[l][e]>maxnum){
							maxnum=Link2Entity[l][e];	
						}
				  }
				}
				Link2Class[l][c]=maxnum;
			}	
		}
		
		//Class2Link
		for(int c=0;c<numClass;c++){
			for(int l=0;l<numLink;l++){
				Class2Link[c][l]=Link2Class[l][c];
			}
		}
	}
	
	public void buildsimLLmatrix() {
		simLL=new double[Link2Entity.length][Link2Entity.length];
			Connection conn = null;
			for(int i=0;i<numLink;i++){
				for(int j=0;j<numLink;j++){
					Double d=new Double(getSimLink2Link(i,j,conn));
					boolean naN = Double.isNaN(d);
					if(!naN){
						simLL[i][j]=d;
					}
				}
			}
	}
	
	public void buildsimCCmatrix() {
		simCC=new double[Class2Entity.length][Class2Entity.length];
			Connection conn = null;
			for(int i=0;i<numClass;i++){
				for(int j=0;j<numClass;j++){
					Double d=new Double(getSimClass2Class(i,j,conn));
					boolean naN = Double.isNaN(d);
					if(!naN){
						simCC[i][j]=d;
					}
				}
			}
	}
	
	public double getSimClass2Class(int c1, int c2, Connection conn){
		double sim=0.0;
		double cossim=0.0, editsim=0.0, semsim=0.0;
		if(c1==c2){
			sim=1.0;
		}else{
			int c1entity[]= new int[Class2Entity[c1].length];
			int c2entity[]= new int[Class2Entity[c2].length];
			for(int i=0;i<Class2Entity[c1].length;i++){
				c1entity[i]=Class2Entity[c1][i];
			}
			for(int i=0;i<Class2Entity[c2].length;i++){
				c2entity[i]=Class2Entity[c2][i];
			}
			cossim=CosSimilarity.getSimilarity(c1entity, c2entity);
			sim= cossim + editsim + semsim;
		}
		return sim;
	}
	
	public double getSimLink2Link(int l1, int l2, Connection conn){
		double sim=0.0;
		double cossim=0.0, editsim=0.0, semsim=0.0;
		if(l1==l2){
			sim=1.0;
		}else{
			int l1entity[]= new int[Link2Class[l1].length];
			int l2entity[]= new int[Link2Class[l2].length];
			for(int i=0;i<Link2Class[l1].length;i++){
				l1entity[i]=Link2Class[l1][i];
			}
			for(int i=0;i<Link2Class[l2].length;i++){
				l2entity[i]=Link2Class[l2][i];
			}
			cossim=CosSimilarity.getSimilarity(l1entity, l2entity);
			
			editsim=EditDistance.getSimilarity(index2LinkMap.get(l1), index2LinkMap.get(l2));
			
			sim= 0.9*cossim + 0.1*editsim + semsim;
		}
		return sim;
	}
	
	
	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> runITCC4PatialLinkOrClassSet(int k, int kl, 
			ArrayList<String> userPath){
		if(userPath.size()==0){
			ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo=runITCC4AllLinkandClass(k, kl);
			return allInfo;
		}else{
			ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo=runITCC4UserPath(k, kl, userPath);
			return allInfo;	
		}
	}
	
	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> runITCC4UserPath(int k, int kl, 
			ArrayList<String> userPath){
		HashMap<String, Integer> link2numMap=new HashMap<String, Integer>();
		HashMap<String, Integer> class2numMap=new HashMap<String, Integer>();
		for(String LinkOrClass:userPath){
			if(LinkOrClass.contains("0")||LinkOrClass.contains("1")){
				if(link2numMap.containsKey(LinkOrClass)){
					int num=0;
					num=link2numMap.get(LinkOrClass)+1;
					link2numMap.put(LinkOrClass, num);
				}else{
					link2numMap.put(LinkOrClass, 1);
				}
			}else{
				if(class2numMap.containsKey(LinkOrClass)){
					int num=0;
					num=class2numMap.get(LinkOrClass)+1;
					class2numMap.put(LinkOrClass, num);
				}else{
					class2numMap.put(LinkOrClass, 1);
				}
			}
		}
		HashMap<Integer, ArrayList<String>> num2linkMap=new HashMap<Integer, ArrayList<String>>();
		HashMap<Integer, ArrayList<String>> num2classMap=new HashMap<Integer, ArrayList<String>>();
		for(String l:link2numMap.keySet()){
			if(num2linkMap.containsKey(link2numMap.get(l))){
				num2linkMap.get(link2numMap.get(l)).add(l);
			}else{
				ArrayList<String> lset=new ArrayList<String>();
				lset.add(l);
				num2linkMap.put(link2numMap.get(l), lset);
			}
		}
		
		for(String c:class2numMap.keySet()){
			if(num2classMap.containsKey(class2numMap.get(c))){
				num2classMap.get(class2numMap.get(c)).add(c);
			}else{
				ArrayList<String> cset=new ArrayList<String>();
				cset.add(c);
				num2classMap.put(class2numMap.get(c), cset);
			}
		}
		
		ArrayList<String> finallinkset= new ArrayList<String>();
		ArrayList<String> finalclassset= new ArrayList<String>();
		int maxnumlink=0;
		for(int lnum:num2linkMap.keySet()){
			if(lnum>maxnumlink){
				maxnumlink=lnum;
				finallinkset=num2linkMap.get(lnum);
			}
		}
		int maxnumclass=0;
		for(int cnum:num2classMap.keySet()){
			if(cnum>maxnumclass){
				maxnumclass=cnum;
				finalclassset=num2classMap.get(cnum);
			}
		}
		
		if(finallinkset.size()==0){
			ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo= runITCC4UserSelectedLinkOrClassSet(k, kl,finalclassset);
			return allInfo;
		}else{
			if(finalclassset.size()==0){
				ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
				ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo= runITCC4UserSelectedLinkOrClassSet(k, kl,finallinkset);
				return allInfo;
			}else{
				ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
				ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo= 
				runITCC4FixedLinkAndClassSet(k, kl,finallinkset,finalclassset);
				return allInfo;
			}
		}

	}
		
	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> runITCC4FixedLinkAndClassSet(int k, int kl,
			ArrayList<String> finallinkset, ArrayList<String> finalclassset){
		HashMap<Integer, Integer> newindex2globalLinkIndexMap= new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> newindex2globalClassIndexMap= new HashMap<Integer, Integer>();
		double minsimLL[][],minsimCC[][];
		
		for(int i=0;i<finallinkset.size();i++){
			newindex2globalLinkIndexMap.put(i, link2IndexMap.get(finallinkset.get(i)));
		}
		
		minsimLL=new 
				double[newindex2globalLinkIndexMap.keySet().size()][newindex2globalLinkIndexMap.keySet().size()];
		for(int i=0;i<newindex2globalLinkIndexMap.keySet().size();i++){
			for(int j=0;j<newindex2globalLinkIndexMap.keySet().size();j++){
				Double d=new Double(simLL[newindex2globalLinkIndexMap.get(i)][newindex2globalLinkIndexMap.get(j)]);
				if(!d.isNaN()){
					minsimLL[i][j]=simLL[newindex2globalLinkIndexMap.get(i)][newindex2globalLinkIndexMap.get(j)];
				}
			}
		}	
		
		for(int i=0;i<finalclassset.size();i++){
			newindex2globalClassIndexMap.put(i, class2IndexMap.get(finalclassset.get(i)));
		}
		
		minsimCC=new 
				double[newindex2globalClassIndexMap.keySet().size()][newindex2globalClassIndexMap.keySet().size()];
		
		for(int i=0;i<newindex2globalClassIndexMap.keySet().size();i++){
			for(int j=0;j<newindex2globalClassIndexMap.keySet().size();j++){
				Double d=new Double(simCC[newindex2globalClassIndexMap.get(i)][newindex2globalClassIndexMap.get(j)]);
				if(!Double.isNaN(d)){
					minsimCC[i][j]=d;
				}
			}
		}
			
//			link
			HashMap<Integer, HashSet<Integer>> linkClusterMap = new HashMap<Integer, HashSet<Integer>>();
			if(minsimLL.length==1){
				HashSet<Integer> lset=new HashSet<Integer>();
				lset.add(newindex2globalLinkIndexMap.get(0));
				linkClusterMap.put(0, lset);
			}else{
				int kk=0;
				if(minsimLL.length>k){
					kk=k;
				}else{
					kk=minsimLL.length;
				}
				KMeans2 km = new KMeans2();
				int[] a = new int[kk];
				a=km.kmeans(minsimLL, kk);
				HashMap<Integer, HashSet<Integer>> linkClusterMap0 = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> templinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
				for (int i=0; i<minsimLL.length; i++) {
					if (templinkClusterMap.containsKey(a[i])) {
						templinkClusterMap.get(a[i]).add(i);
					}
					else {
						HashSet<Integer> lset = new HashSet<Integer>();
						lset.add(i);
						templinkClusterMap.put(a[i], lset);
					}
				}
				int newlinkCluKey=0;
				for(int i:templinkClusterMap.keySet()){
					if(templinkClusterMap.get(i).size()>0){
						linkClusterMap0.put(newlinkCluKey, templinkClusterMap.get(i));
						newlinkCluKey++;
					}
				}
				
				for(int key: linkClusterMap0.keySet()) {
					HashSet<Integer> globallinkindexset=new HashSet<Integer>();
					for(int l:linkClusterMap0.get(key)){
						globallinkindexset.add(newindex2globalLinkIndexMap.get(l));
					}
					linkClusterMap.put(key, globallinkindexset);
				}
			}
			
			HashMap<Integer, HashSet<Integer>> classClusterMap = new HashMap<Integer, HashSet<Integer>>();
			if(minsimCC.length==1){
				HashSet<Integer> cset=new HashSet<Integer>();
				cset.add(newindex2globalClassIndexMap.get(0));
				classClusterMap.put(0, cset);
			}else{
				int kkl=0;
				if(minsimCC.length>kl){
					kkl=kl;
				}else{
					kkl=minsimCC.length;
				}
				KMeans2 km2 = new KMeans2();
				int[] b = new int[kkl];
				b=km2.kmeans(minsimCC, kkl);
				HashMap<Integer, HashSet<Integer>> classClusterMap0 = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> tempclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
				for (int i=0; i<minsimCC.length; i++) {
					if (tempclassClusterMap.containsKey(b[i])) {
						tempclassClusterMap.get(b[i]).add(i);
					}
					else {
						HashSet<Integer> cset = new HashSet<Integer>();
						cset.add(i);
						tempclassClusterMap.put(b[i], cset);
					}
				}
				int newclassCluKey=0;
				for(int i:tempclassClusterMap.keySet()){
					if(tempclassClusterMap.get(i).size()>0){
						classClusterMap0.put(newclassCluKey, tempclassClusterMap.get(i));
						newclassCluKey++;
					}
				}
				
				for(int key: classClusterMap0.keySet()) {
					HashSet<Integer> globalclassindexset=new HashSet<Integer>();
					for(int c:classClusterMap0.get(key)){
						globalclassindexset.add(newindex2globalClassIndexMap.get(c));
					}
					classClusterMap.put(key, globalclassindexset);
				}
			}

			ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo=runITCC(k, kl, linkClusterMap, classClusterMap);
			
			return allInfo;
		
	}
	
	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> runITCC4UserSelectedLinkOrClassSet(int k, int kl, ArrayList<String> linkOrclassset){
		HashMap<Integer, Integer> newindex2globalLinkIndexMap= new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> newindex2globalClassIndexMap= new HashMap<Integer, Integer>();
		double minsimLL[][],minsimCC[][];
		
		if(link2IndexMap.keySet().containsAll(linkOrclassset)){
			for(int i=0;i<linkOrclassset.size();i++){
				newindex2globalLinkIndexMap.put(i, link2IndexMap.get(linkOrclassset.get(i)));
			}
			
			minsimLL=new 
					double[newindex2globalLinkIndexMap.keySet().size()][newindex2globalLinkIndexMap.keySet().size()];
			for(int i=0;i<newindex2globalLinkIndexMap.keySet().size();i++){
				for(int j=0;j<newindex2globalLinkIndexMap.keySet().size();j++){
					Double d=new Double(simLL[newindex2globalLinkIndexMap.get(i)][newindex2globalLinkIndexMap.get(j)]);
					if(!d.isNaN()){
						minsimLL[i][j]=simLL[newindex2globalLinkIndexMap.get(i)][newindex2globalLinkIndexMap.get(j)];
					}
				}
			}	

			HashSet<Integer> cglobalindexset=new HashSet<Integer>();
			for(int lnewindex: newindex2globalLinkIndexMap.keySet()){
				for(int c=0;c<Class2Entity.length;c++){
					if(Link2Class[newindex2globalLinkIndexMap.get(lnewindex)][c]!=0){
						cglobalindexset.add(c);
						
					}
				}
			}

			int num=0;
			for(int globalindex:cglobalindexset){
				newindex2globalClassIndexMap.put(num, globalindex);
				num++;
			}
			minsimCC=new 
					double[newindex2globalClassIndexMap.keySet().size()][newindex2globalClassIndexMap.keySet().size()];
			
			
			for(int i=0;i<newindex2globalClassIndexMap.keySet().size();i++){
				for(int j=0;j<newindex2globalClassIndexMap.keySet().size();j++){
					Double d=new Double(simCC[newindex2globalClassIndexMap.get(i)][newindex2globalClassIndexMap.get(j)]);
					if(!Double.isNaN(d)){
						minsimCC[i][j]=d;
					}
				}
			}
		}else{
			
			for(int i=0;i<linkOrclassset.size();i++){
				newindex2globalClassIndexMap.put(i, class2IndexMap.get(linkOrclassset.get(i)));
			}
			
			minsimCC=new 
					double[newindex2globalClassIndexMap.keySet().size()][newindex2globalClassIndexMap.keySet().size()];
			
			for(int i=0;i<newindex2globalClassIndexMap.keySet().size();i++){
				for(int j=0;j<newindex2globalClassIndexMap.keySet().size();j++){
					Double d=new Double(simCC[newindex2globalClassIndexMap.get(i)][newindex2globalClassIndexMap.get(j)]);
					boolean naN = Double.isNaN(d);
					if(!naN){
						minsimCC[i][j]=d;
					}
				}
			}
			
			HashSet<Integer> lglobalindexset=new HashSet<Integer>();
			for(int cnewindex: newindex2globalClassIndexMap.keySet()){
				for(int l=0;l<numLink;l++){
					if(Class2Link[newindex2globalClassIndexMap.get(cnewindex)][l]!=0){
						lglobalindexset.add(l);
					}
				}
			}
			int num=0;
			for(int globalindex:lglobalindexset){
				newindex2globalLinkIndexMap.put(num, globalindex);
				num++;
			}
			minsimLL=new 
					double[newindex2globalLinkIndexMap.keySet().size()][newindex2globalLinkIndexMap.keySet().size()];
			
			for(int i=0;i<newindex2globalLinkIndexMap.keySet().size();i++){
				for(int j=0;j<newindex2globalLinkIndexMap.keySet().size();j++){
					Double d=new Double(simLL[newindex2globalLinkIndexMap.get(i)][newindex2globalLinkIndexMap.get(j)]);
					if(!d.isNaN()){
						minsimLL[i][j]=simLL[newindex2globalLinkIndexMap.get(i)][newindex2globalLinkIndexMap.get(j)];
					}
				}
			}
		}
			
//			link
			HashMap<Integer, HashSet<Integer>> linkClusterMap = new HashMap<Integer, HashSet<Integer>>();
			if(minsimLL.length==1){
				HashSet<Integer> lset=new HashSet<Integer>();
				lset.add(newindex2globalLinkIndexMap.get(0));
				linkClusterMap.put(0, lset);
			}else{
				int kk=0;
				if(minsimLL.length>k){
					kk=k;
				}else{
					kk=minsimLL.length;
				}
				KMeans2 km = new KMeans2();
				int[] a = new int[kk];
				a=km.kmeans(minsimLL, kk);
				HashMap<Integer, HashSet<Integer>> linkClusterMap0 = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> templinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
				for (int i=0; i<minsimLL.length; i++) {
					if (templinkClusterMap.containsKey(a[i])) {
						templinkClusterMap.get(a[i]).add(i);
					}
					else {
						HashSet<Integer> lset = new HashSet<Integer>();
						lset.add(i);
						templinkClusterMap.put(a[i], lset);
					}
				}
				int newlinkCluKey=0;
				for(int i:templinkClusterMap.keySet()){
					if(templinkClusterMap.get(i).size()>0){
						linkClusterMap0.put(newlinkCluKey, templinkClusterMap.get(i));
						newlinkCluKey++;
					}
				}
				
				for(int key: linkClusterMap0.keySet()) {
					HashSet<Integer> globallinkindexset=new HashSet<Integer>();
					for(int l:linkClusterMap0.get(key)){
						globallinkindexset.add(newindex2globalLinkIndexMap.get(l));
					}
					linkClusterMap.put(key, globallinkindexset);
				}
			}
			
//			class
			HashMap<Integer, HashSet<Integer>> classClusterMap = new HashMap<Integer, HashSet<Integer>>();
			if(minsimCC.length==1){
				HashSet<Integer> cset=new HashSet<Integer>();
				cset.add(newindex2globalClassIndexMap.get(0));
				classClusterMap.put(0, cset);
			}else{
				int kkl=0;
				if(minsimCC.length>kl){
					kkl=kl;
				}else{
					kkl=minsimCC.length;
				}
				KMeans2 km2 = new KMeans2();
				int[] b = new int[kkl];
				b=km2.kmeans(minsimCC, kkl);
				HashMap<Integer, HashSet<Integer>> classClusterMap0 = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> tempclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
				for (int i=0; i<minsimCC.length; i++) {
					if (tempclassClusterMap.containsKey(b[i])) {
						tempclassClusterMap.get(b[i]).add(i);
					}
					else {
						HashSet<Integer> cset = new HashSet<Integer>();
						cset.add(i);
						tempclassClusterMap.put(b[i], cset);
					}
				}
				int newclassCluKey=0;
				for(int i:tempclassClusterMap.keySet()){
					if(tempclassClusterMap.get(i).size()>0){
						classClusterMap0.put(newclassCluKey, tempclassClusterMap.get(i));
						newclassCluKey++;
					}
				}
				
				for(int key: classClusterMap0.keySet()) {
					HashSet<Integer> globalclassindexset=new HashSet<Integer>();
					for(int c:classClusterMap0.get(key)){
						globalclassindexset.add(newindex2globalClassIndexMap.get(c));
					}
					classClusterMap.put(key, globalclassindexset);
				}
			}
			
			ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo=runITCC(k, kl, linkClusterMap, classClusterMap);
			
			return allInfo;
		
	}

	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> runITCC4AllLinkandClass(int k, int kl){
		buildsimLLmatrix();
		buildsimCCmatrix();
		
//		link
		KMeans2 km = new KMeans2();
		int[] a = new int[simLL.length];
		a=km.kmeans(simLL, k);
		HashMap<Integer, HashSet<Integer>> linkClusterMap0 = new HashMap<Integer, HashSet<Integer>>();
		HashMap<Integer, HashSet<Integer>> templinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
		for (int i=0; i<simLL.length; i++) {
			if (templinkClusterMap.containsKey(a[i])) {
				templinkClusterMap.get(a[i]).add(i);
			}
			else {
				HashSet<Integer> lset = new HashSet<Integer>();
				lset.add(i);
				templinkClusterMap.put(a[i], lset);
			}
		}
		int newlinkCluKey=0;
		for(int i:templinkClusterMap.keySet()){
			if(templinkClusterMap.get(i).size()>0){
				linkClusterMap0.put(newlinkCluKey, templinkClusterMap.get(i));
				newlinkCluKey++;
			}
		}

		KMeans2 km2 = new KMeans2();
		int[] b = new int[simCC.length];
		b=km2.kmeans(simCC, kl);
		HashMap<Integer, HashSet<Integer>> classClusterMap0 = new HashMap<Integer, HashSet<Integer>>();
		HashMap<Integer, HashSet<Integer>> tempclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
		for (int i=0; i<simCC.length; i++) {
			if (tempclassClusterMap.containsKey(b[i])) {
				tempclassClusterMap.get(b[i]).add(i);
			}
			else {
				HashSet<Integer> cset = new HashSet<Integer>();
				cset.add(i);
				tempclassClusterMap.put(b[i], cset);
			}
		}
		int newclassCluKey=0;
		for(int i:tempclassClusterMap.keySet()){
			if(tempclassClusterMap.get(i).size()>0){
				classClusterMap0.put(newclassCluKey, tempclassClusterMap.get(i));
				newclassCluKey++;
			}
		}

		ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
		ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo=runITCC(k, kl, linkClusterMap0, classClusterMap0);
		
		return allInfo;
		
	}
	
	public ThreeTuple<ArrayList<ArrayList<String>>,ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> runITCC(int k, int kl,HashMap<Integer, HashSet<Integer>> linkClusterMap0,
			HashMap<Integer, HashSet<Integer>> classClusterMap0){
		HashMap<Integer, HashSet<Integer>> finallinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
		HashMap<Integer, HashSet<Integer>> finalclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
		finallinkClusterMap=linkClusterMap0;
		finalclassClusterMap=classClusterMap0;
		
		HashSet<Integer> alllinks=new HashSet<Integer>();
		HashSet<Integer> allclass=new HashSet<Integer>();
		for(int lclukey:linkClusterMap0.keySet()){
			alllinks.addAll(linkClusterMap0.get(lclukey));
		}
		
		for(int cclukey:classClusterMap0.keySet()){
			allclass.addAll(classClusterMap0.get(cclukey));
		}
		
		if(alllinks.size()>k||allclass.size()>kl){
			
			HashMap<Integer, HashSet<Integer>> innertemplinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
			HashMap<Integer, HashSet<Integer>> innertempclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
			innertemplinkClusterMap=linkClusterMap0;
			innertempclassClusterMap=classClusterMap0;
			
			double difflossScore=0.0;
			int t1=10;
			while(t1>0){
				double lossScore=0.0;
				for(int i=0; i<innertemplinkClusterMap.size();i++){
					for(int j=0; j<innertempclassClusterMap.size();j++){
						double pLcluster=0.0, pCcluster=0.0, pLclusterandCcluster=0.0;	
						HashSet<Integer> L2Eset=new HashSet<Integer>();
						for(int lkey: innertemplinkClusterMap.get(i)){
							for(int ekey=0; ekey<Link2Entity[lkey].length; ekey++){
								if(Link2Entity[lkey][ekey]!=0){
									L2Eset.add(ekey);
								}
							}
						}
						pLcluster=(double)L2Eset.size()/entity2IndexMap.size();
						
						HashSet<Integer> C2Eset=new HashSet<Integer>();
						for(int ckey: innertempclassClusterMap.get(j)){
							for(int ekey=0; ekey<Class2Entity[ckey].length; ekey++){
								if(Class2Entity[ckey][ekey]!=0){
									C2Eset.add(ekey);
								}
							}
						}
						pCcluster=(double)C2Eset.size()/entity2IndexMap.size();
						
						L2Eset.retainAll(C2Eset);
						if(L2Eset.size()==0) continue;
						pLclusterandCcluster=(double)L2Eset.size()/(double)entity2IndexMap.size();
								
						for(int lkey: innertemplinkClusterMap.get(i)){
							for(int ckey: innertempclassClusterMap.get(j)){
								if(Link2Class[lkey][ckey]!=0){
									double plinkandclass=0.0;
									HashSet<Integer> l2Eset=new HashSet<Integer>();
									for(int ekey=0; ekey<Link2Entity[lkey].length; ekey++){
										if(Link2Entity[lkey][ekey]!=0){
											l2Eset.add(ekey);
										}
									}	
									double plink=(double)l2Eset.size()/entity2IndexMap.size();
									
									HashSet<Integer> c2Eset=new HashSet<Integer>();
									for(int ekey=0; ekey<Class2Entity[ckey].length; ekey++){
										if(Class2Entity[ckey][ekey]!=0){
											c2Eset.add(ekey);
										}
									}
									double pclass=(double)c2Eset.size()/entity2IndexMap.size();
									
									l2Eset.retainAll(c2Eset);
									plinkandclass=(double)l2Eset.size()/(double)entity2IndexMap.size();
								
									lossScore+=plinkandclass*Math.log((plinkandclass/pLclusterandCcluster)*(pLcluster/plink)*(pCcluster/pclass));
								}	
							}
						}
					}
				}
				
				HashMap<Integer, Integer> link2oldLinkClusterIndexMap = new HashMap<Integer, Integer>();
				for(int linkclusterkey=0; linkclusterkey < innertemplinkClusterMap.size();linkclusterkey++){
					for(int linkindex:innertemplinkClusterMap.get(linkclusterkey)){
						link2oldLinkClusterIndexMap.put(linkindex, linkclusterkey);
					}
				}
				
				HashMap<Integer, Integer> link2newLinkClusterIndexMap = new HashMap<Integer, Integer>();
				for(int lindex: link2oldLinkClusterIndexMap.keySet()){
					int newlinkindex2linkclusterindex=link2oldLinkClusterIndexMap.get(lindex);
					double minDEachclassdiff=1000.0; 
					
					for(int linkclusterkey=0; linkclusterkey<innertemplinkClusterMap.size();linkclusterkey++){
						if(innertemplinkClusterMap.get(linkclusterkey).size()==1&&
								innertemplinkClusterMap.get(linkclusterkey).contains(lindex)) continue;
						HashSet<Integer> newlinkcluster=new HashSet<Integer>();
						newlinkcluster.add(lindex);
						newlinkcluster.addAll(innertemplinkClusterMap.get(linkclusterkey));
						
						double aggDEachclassdiff=0.0; 
						
						for(int ckey: index2ClassMap.keySet()){
							double pclassunderlink=0.0, pclassunderlinkcluster=0.0;
							
							HashSet<Integer> l2Eset=new HashSet<Integer>();
							for(int ekey=0; ekey<Link2Entity[lindex].length; ekey++){
								if(Link2Entity[lindex][ekey]!=0){
									l2Eset.add(ekey);
								}
							}	
							double plink=(double)l2Eset.size()/entity2IndexMap.size();
							HashSet<Integer> c2Eset=new HashSet<Integer>();
							for(int ekey=0; ekey<Class2Entity[ckey].length; ekey++){
								if(Class2Entity[ckey][ekey]!=0){
									c2Eset.add(ekey);
								}
							}
							l2Eset.retainAll(c2Eset);
							double plinkandclass=(double)l2Eset.size()/(double)entity2IndexMap.size();
							
							pclassunderlink=plinkandclass/plink;
							
							HashSet<Integer> L2Eset=new HashSet<Integer>();
							for(int l: newlinkcluster){
								for(int ekey=0; ekey<Link2Entity[l].length; ekey++){
									if(Link2Entity[l][ekey]!=0){
										L2Eset.add(ekey);
									}
								}
							}
							double pLcluster=(double)L2Eset.size()/entity2IndexMap.size();
							L2Eset.retainAll(c2Eset);
							if(L2Eset.size()==0) continue;
							double pclassandlinkcluster=(double)L2Eset.size()/(double)entity2IndexMap.size();
							pclassunderlinkcluster=pclassandlinkcluster/pLcluster;
							
							double DEachclassdiff=pclassunderlink*Math.log(pclassunderlink/pclassunderlinkcluster);

							aggDEachclassdiff=aggDEachclassdiff+DEachclassdiff;
						}
						
						if(aggDEachclassdiff<minDEachclassdiff){
							minDEachclassdiff=aggDEachclassdiff;
							newlinkindex2linkclusterindex=linkclusterkey;
						}
					}
					link2newLinkClusterIndexMap.put(lindex, newlinkindex2linkclusterindex);
				}
				
				HashMap<Integer, Integer> class2oldClassClusterIndexMap = new HashMap<Integer, Integer>();
				for(int classclusterkey=0; classclusterkey < innertempclassClusterMap.size();classclusterkey++){
					for(int classindex:innertempclassClusterMap.get(classclusterkey)){
						class2oldClassClusterIndexMap.put(classindex, classclusterkey);
					}
				}
				
				HashMap<Integer, Integer> class2newClassClusterIndexMap = new HashMap<Integer, Integer>();
				for(int cindex: class2oldClassClusterIndexMap.keySet()){
					int newclassindex2classclusterindex=class2oldClassClusterIndexMap.get(cindex);
					
					double minDEachclassdiff=1000.0; 
					
					for(int classclusterkey=0; classclusterkey<innertempclassClusterMap.size();classclusterkey++){
						if(innertempclassClusterMap.get(classclusterkey).size()==1&&
								innertempclassClusterMap.get(classclusterkey).contains(cindex)) continue;
						HashSet<Integer> newclasscluster=new HashSet<Integer>();
						newclasscluster.add(cindex);
						newclasscluster.addAll(innertempclassClusterMap.get(classclusterkey));
						double aggDEachlinkdiff=0.0; 
						
						for(int lkey: index2LinkMap.keySet()){
							double plinkunderclass=0.0, plinkunderclasscluster=0.0;
							
							HashSet<Integer> c2Eset=new HashSet<Integer>();
							for(int ekey=0; ekey<Class2Entity[cindex].length; ekey++){
								if(Class2Entity[cindex][ekey]!=0){
									c2Eset.add(ekey);
								}
							}	
							double pclass=(double)c2Eset.size()/entity2IndexMap.size();
							HashSet<Integer> l2Eset=new HashSet<Integer>();
							for(int ekey=0; ekey<Link2Entity[lkey].length; ekey++){
								if(Link2Entity[lkey][ekey]!=0){
									l2Eset.add(ekey);
								}
							}
							c2Eset.retainAll(l2Eset);
							double plinkandclass=(double)c2Eset.size()/(double)entity2IndexMap.size();
							
							plinkunderclass=plinkandclass/pclass;
							
							HashSet<Integer> C2Eset=new HashSet<Integer>();
							for(int c: newclasscluster){
								for(int ekey=0; ekey<Class2Entity[c].length; ekey++){
									if(Class2Entity[c][ekey]!=0){
										C2Eset.add(ekey);
									}
								}
							}
							double pCcluster=(double)C2Eset.size()/entity2IndexMap.size();
							C2Eset.retainAll(l2Eset);
							if(C2Eset.size()==0) continue;
							double plinkandclasscluster=(double)C2Eset.size()/(double)entity2IndexMap.size();
							plinkunderclasscluster=plinkandclasscluster/pCcluster;
							
							double DEachclassdiff=plinkunderclass*Math.log(plinkunderclass/plinkunderclasscluster);
							aggDEachlinkdiff=aggDEachlinkdiff+DEachclassdiff;
						}
						
						if(aggDEachlinkdiff<minDEachclassdiff){
							minDEachclassdiff=aggDEachlinkdiff;
							newclassindex2classclusterindex=classclusterkey;
						}
					}
					class2newClassClusterIndexMap.put(cindex, newclassindex2classclusterindex);
				}
				HashMap<Integer, HashSet<Integer>> newlinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> tempnewlinkClusterMap = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> newclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
				HashMap<Integer, HashSet<Integer>> tempnewclassClusterMap = new HashMap<Integer, HashSet<Integer>>();
				for(int lindex:link2newLinkClusterIndexMap.keySet()){
					int lclusterkey=link2newLinkClusterIndexMap.get(lindex);
					if(tempnewlinkClusterMap.containsKey(lclusterkey)){
						tempnewlinkClusterMap.get(lclusterkey).add(lindex);
					}else{
						HashSet<Integer> linkcluster=new HashSet<Integer>();
						linkcluster.add(lindex);
						tempnewlinkClusterMap.put(lclusterkey, linkcluster);
					}
				}		
				int newnewlinkCluKey=0;
				for(int key:tempnewlinkClusterMap.keySet()){
					if(tempnewlinkClusterMap.get(key).size()>0){
						newlinkClusterMap.put(newnewlinkCluKey, tempnewlinkClusterMap.get(key));
						newnewlinkCluKey++;
					}
				}

				for(int cindex:class2newClassClusterIndexMap.keySet()){
					int cclusterkey=class2newClassClusterIndexMap.get(cindex);
					if(tempnewclassClusterMap.containsKey(cclusterkey)){
						tempnewclassClusterMap.get(cclusterkey).add(cindex);
					}else{
						HashSet<Integer> classcluster=new HashSet<Integer>();
						classcluster.add(cindex);
						tempnewclassClusterMap.put(cclusterkey, classcluster);
					}
				}
				int newnewclassCluKey=0;
				for(int i:tempnewclassClusterMap.keySet()){
					if(tempnewclassClusterMap.get(i).size()>0){
						newclassClusterMap.put(newnewclassCluKey, tempnewclassClusterMap.get(i));
						newnewclassCluKey++;
					}
				}				
				
				double lossScore1=0.0;
				for(int i=0; i<newlinkClusterMap.size();i++){
					for(int j=0; j<newclassClusterMap.size();j++){
						double pLcluster=0.0, pCcluster=0.0, pLclusterandCcluster=0.0;	
						HashSet<Integer> L2Eset=new HashSet<Integer>();
						for(int lkey: newlinkClusterMap.get(i)){
							for(int ekey=0; ekey<Link2Entity[lkey].length; ekey++){
								if(Link2Entity[lkey][ekey]!=0){
									L2Eset.add(ekey);
								}
							}
						}
						pLcluster=(double)L2Eset.size()/entity2IndexMap.size();
						
						HashSet<Integer> C2Eset=new HashSet<Integer>();
						for(int ckey: newclassClusterMap.get(j)){
							for(int ekey=0; ekey<Class2Entity[ckey].length; ekey++){
								if(Class2Entity[ckey][ekey]!=0){
									C2Eset.add(ekey);
								}
							}
						}
						pCcluster=(double)C2Eset.size()/entity2IndexMap.size();
						
						L2Eset.retainAll(C2Eset);
						if(L2Eset.size()==0) continue;
						pLclusterandCcluster=(double)L2Eset.size()/(double)entity2IndexMap.size();
						
						
						for(int lkey: newlinkClusterMap.get(i)){
							for(int ckey: newclassClusterMap.get(j)){
								if(Link2Class[lkey][ckey]!=0){
									double plinkandclass=0.0;
									HashSet<Integer> l2Eset=new HashSet<Integer>();
									for(int ekey=0; ekey<Link2Entity[lkey].length; ekey++){
										if(Link2Entity[lkey][ekey]!=0){
											l2Eset.add(ekey);
										}
									}	
									double plink=(double)l2Eset.size()/entity2IndexMap.size();
									
									HashSet<Integer> c2Eset=new HashSet<Integer>();
									for(int ekey=0; ekey<Class2Entity[ckey].length; ekey++){
										if(Class2Entity[ckey][ekey]!=0){
											c2Eset.add(ekey);
										}
									}
									double pclass=(double)c2Eset.size()/entity2IndexMap.size();
									
									l2Eset.retainAll(c2Eset);
									plinkandclass=(double)l2Eset.size()/(double)entity2IndexMap.size();
									lossScore1+=plinkandclass*Math.log((plinkandclass/pLclusterandCcluster)*(pLcluster/plink)*(pCcluster/pclass));
								}	
							}
						}
					}
				}
				difflossScore=lossScore-lossScore1;
				if(difflossScore<=0.0){
					finallinkClusterMap=innertemplinkClusterMap;
					finalclassClusterMap=innertempclassClusterMap;
				   break;	
				} 
				if(difflossScore<0.04){
					finallinkClusterMap=newlinkClusterMap;
					finalclassClusterMap=newclassClusterMap;
					break;
				}
				
				innertemplinkClusterMap.clear();
				innertempclassClusterMap.clear();
				for(int lclukey:newlinkClusterMap.keySet()){
					for(int lkey:newlinkClusterMap.get(lclukey)){
						if(innertemplinkClusterMap.containsKey(lclukey)){
							innertemplinkClusterMap.get(lclukey).add(lkey);
						}else{
							HashSet<Integer> lset=new HashSet<Integer>();
							lset.add(lkey);
							innertemplinkClusterMap.put(lclukey, lset);
						}
					}
				}
				
				for(int cclukey:newclassClusterMap.keySet()){
					for(int ckey:newclassClusterMap.get(cclukey)){
						if(innertempclassClusterMap.containsKey(cclukey)){
							innertempclassClusterMap.get(cclukey).add(ckey);
						}else{
							HashSet<Integer> cset=new HashSet<Integer>();
							cset.add(ckey);
							innertempclassClusterMap.put(cclukey, cset);
						}
					}
				}
				t1--;
			}
		}
		ArrayList<HashSet<String>> linkclusterlist=new ArrayList<HashSet<String>>();
		ArrayList<HashMap<String, HashSet<String>>> classclusterlist=new ArrayList<HashMap<String, HashSet<String>>>();
		ArrayList<ThreeTuple<Integer, Integer, Double>> lclu2ccluweightlist=new ArrayList<ThreeTuple<Integer, Integer, Double>>();
		
		    HashSet<Integer> currentlinkset=new HashSet<Integer>();
			for (int lclukey: finallinkClusterMap.keySet()){
				HashSet<String> linkset=new HashSet<String>();
				for(int lkey: finallinkClusterMap.get(lclukey)){
					linkset.add(index2LinkMap.get(lkey));
					currentlinkset.add(lkey);
				}
				linkclusterlist.add(linkset);
			}
//			current links -->entities
			HashSet<String> currentLinks2Eset=new HashSet<String>();
			for(int lkey: currentlinkset){
				for(int ekey=0; ekey<Link2Entity[lkey].length; ekey++){
					if(Link2Entity[lkey][ekey]!=0){
						currentLinks2Eset.add(index2EntityMap.get(ekey));
					}
				}
			}
			
//			current links -->entities
			HashSet<Integer> currentClass2Eset=new HashSet<Integer>();
			
			for (int cclukey: finalclassClusterMap.keySet()) {
				HashMap<String, HashSet<String>> cclu2entityMap=new HashMap<String, HashSet<String>>();
				for(int ckey: finalclassClusterMap.get(cclukey)){
					String ckeystring=index2ClassMap.get(ckey);
//					classset.add(index2LinkMap.get(ckey))
					HashSet<String> c2Eset=new HashSet<String>();
					for(int ekey=0; ekey<Class2Entity[ckey].length; ekey++){
						if(Class2Entity[ckey][ekey]!=0){
							c2Eset.add(index2EntityMap.get(ekey));
						}
					}
					c2Eset.retainAll(currentLinks2Eset);
					if(c2Eset.size()>0){
						cclu2entityMap.put(ckeystring, c2Eset);
						for(String e:c2Eset){
							currentClass2Eset.add(entity2IndexMap.get(e));
						}
					}
				}
				if(cclu2entityMap.size()>0){
					classclusterlist.add(cclu2entityMap);
				}
			}
			
			ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>> newclassclusterlist=
					new ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>();
			
			
			for(int i=0;i<classclusterlist.size();i++){
				HashMap<String, HashSet<String>> cclu2entityMap=classclusterlist.get(i);
				ArrayList<TwoTuple<String, HashSet<String>>> innercluster=new ArrayList<TwoTuple<String, HashSet<String>>>();
				TreeMap<Integer, HashSet<String>> treemap=new TreeMap<Integer, HashSet<String>>();
				for(String c:cclu2entityMap.keySet()){
					int size=cclu2entityMap.get(c).size();
					if(treemap.containsKey(size)){
						treemap.get(size).add(c);
					}else{
						HashSet<String> cset=new HashSet<String>();
						cset.add(c);
						treemap.put(size, cset);
					}
					
				}
				NavigableSet<Integer> nset=treemap.descendingKeySet();
				for(Integer size:nset){
					HashSet<String> cset=treemap.get(size);
					for(String c:cset){
						HashSet<String> eset=cclu2entityMap.get(c);
						TwoTuple<String, HashSet<String>> inner=new 
								TwoTuple<String, HashSet<String>>(c,eset);
						innercluster.add(inner);
					}
				}
				newclassclusterlist.add(innercluster);
			}
			
			ArrayList<HashSet<String>> newlinkclusterlist=new ArrayList<HashSet<String>>();
			for(int i=0;i<linkclusterlist.size();i++){
				HashSet<String> linkset=new HashSet<String>();
				for(String link:linkclusterlist.get(i)){
					int indexlink=link2IndexMap.get(link);
					for(int eidex:currentClass2Eset){
						if(Link2Entity[indexlink][eidex]!=0){
							linkset.add(link);
						}
					}
				}
				if(linkset.size()>0){
					newlinkclusterlist.add(linkset);
				}
			}
			
			ArrayList<ArrayList<String>> newnewlinkclusterlist=new ArrayList<ArrayList<String>>();
			for(int i=0;i<newlinkclusterlist.size();i++){
				TreeMap<Integer, HashSet<String>> treemap=new TreeMap<Integer, HashSet<String>>();
				ArrayList<String> inner=new ArrayList<String>();
				for(String link:newlinkclusterlist.get(i)){
					int indexlink=link2IndexMap.get(link);
					int numE=0;
					for(int eidex=0;eidex<numEntity;eidex++){
						if(Link2Entity[indexlink][eidex]!=0){
							numE++;
						}
					}
					if(treemap.containsKey(numE)){
						treemap.get(numE).add(link);
					}else{
						HashSet<String> lset=new HashSet<String>();
						lset.add(link);
						treemap.put(numE, lset);
					}
				}
				
				NavigableSet<Integer> nset=treemap.descendingKeySet();
				for(Integer size:nset){
					HashSet<String> lset=treemap.get(size);
					for(String l:lset){
						inner.add(l);
					}
				}
				newnewlinkclusterlist.add(inner);
			}
			
			double[][] weight=new double[newnewlinkclusterlist.size()][newclassclusterlist.size()];
			
			for(int li=0;li<newnewlinkclusterlist.size();li++){	
				for(int cj=0;cj<newclassclusterlist.size();cj++){
					
					HashSet<Integer> lclu2Eset=new HashSet<Integer>();
					for(String link: newnewlinkclusterlist.get(li)){
						int indexlink=link2IndexMap.get(link);
						for(int ekey=0; ekey<Link2Entity[indexlink].length; ekey++){
							if(Link2Entity[indexlink][ekey]!=0){
								lclu2Eset.add(ekey);
							}
						}
					}
					
					HashSet<Integer> cclu2Eset=new HashSet<Integer>();
					for(TwoTuple<String, HashSet<String>> c: newclassclusterlist.get(cj)){
						for(String e:c.getSecond()){
							cclu2Eset.add(entity2IndexMap.get(e));
						}
					}
					
					lclu2Eset.retainAll(cclu2Eset);
					if(lclu2Eset.size()!=0) {
						weight[li][cj]=(double)lclu2Eset.size()/entity2IndexMap.size();
					}
				}
			}		
			
			for(int i=0;i<weight.length;i++){
				for(int j=0;j<weight[0].length;j++){
					if(weight[i][j]>0){
						ThreeTuple<Integer, Integer, Double> lclu2ccluweight=
							new ThreeTuple<Integer, Integer, Double>(i,j,weight[i][j]);
						lclu2ccluweightlist.add(lclu2ccluweight);
					}
				}
			}
			
			ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>> allInfo=new ThreeTuple<ArrayList<ArrayList<String>>,
			ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
			ArrayList<ThreeTuple<Integer, Integer, Double>>>(newnewlinkclusterlist, newclassclusterlist, lclu2ccluweightlist);
					
		return allInfo;
	}

	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> getAllLinkClassCluster() {
		return runITCC4AllLinkandClass(5, 5);
	}

	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> getMinLinkClassCluster(
		ArrayList<String> userPath) {
		return runITCC4PatialLinkOrClassSet(5, 5, userPath);
	}

	public Integer getAllLinkedEntities() {
		return numEntity;
	}

}
