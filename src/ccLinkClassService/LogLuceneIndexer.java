package ccLinkClassService;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class LogLuceneIndexer {
	private static Directory directory = null;
	
	private static IndexReader ir = null;//IndexReader.open(directory);//====
	private static IndexSearcher is = null;//new IndexSearcher(ir);
	private static String filePath = "c://logIndexPath_forRecom";//PropertiesReader.getProperty("logIndexPath_forRecom");//LuceneIndexer.LOG_INDEX_PATH;
	
	public static final String FIELD_URI = "uri";

	public static HashMap<String, HashSet<String>> findProperty2ValueMap(String currentUri){
		HashMap<String,HashSet<String>> allPropertyMap= new HashMap<String,HashSet<String>>();
		IndexSearcher is = LogLuceneIndexer.getIndexSearcher();
		
		Term term = new Term(FIELD_URI,currentUri); 
		TermQuery luceneQuery = new TermQuery(term); 
		TopDocs hitdocs = null;
		try {
			hitdocs = is.search(luceneQuery, 1);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 

        if(hitdocs!=null){
        	for(ScoreDoc sd:hitdocs.scoreDocs){
        			int index=sd.doc;
			        Document doc;
			        try {
				        doc = is.doc(index);
				        for(Fieldable field: doc.getFields()){
				        	if(!field.name().equals(FIELD_URI)){
				        		String fieldvalue=doc.get(field.name()).toString();
				        		if(fieldvalue.contains("http:")){
				        				HashSet<String> endset=new HashSet<String>();
				        				String ss[]=fieldvalue.split(" ");
				        				for (int i = 0; i < ss.length; i++) {
				        					 endset.add(ss[i]); 
				        				}
				        				allPropertyMap.put(field.name(), endset);
				        		}
				        	}
				        }    
		            } catch (CorruptIndexException e) {
				        e.printStackTrace();
			        } catch (IOException e) {
				        e.printStackTrace();
			        }	
          }
        }
		return allPropertyMap;		
	}
    
	public static boolean isIndexExist(){
		File file = new File(filePath);
		return file.exists();
	}

	public static TopDocs search(String field, String content, int topnum, Boolean analyze){//================
		TopDocs hitdocs = null;
		if(!isIndexExist()){
			return null;
		}
		try {
			IndexSearcher is = getIndexSearcher();
			if((analyze!=null && !analyze)||(analyze==null&&field.equals(FIELD_URI))){//(field.equals(FIELD_URI)){ //==not analyze
				Term term = new Term(field, content);
				TermQuery tq = new TermQuery(term);
				hitdocs = is.search(tq,topnum);
			}else{
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
				QueryParser qp = new  QueryParser(Version.LUCENE_35, field,analyzer);
				Query q = qp.parse(content);
				hitdocs = is.search(q,topnum);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return hitdocs;
	}
	
	public static IndexSearcher getIndexSearcher(){		
		File file = new File(filePath);
		try {
			directory = FSDirectory.open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ir = IndexReader.open(directory);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		is = new IndexSearcher(ir);
		return is;
	}
	
	public synchronized static Directory getDirectory(){
				if(directory == null){
					if(isIndexExist()){
						try {
							File file = new File(filePath);
							directory = new RAMDirectory(FSDirectory.open(file));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else{
						File file = new File(filePath);
						try {
							directory = FSDirectory.open(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		return directory;
	}
	
	
	
}
