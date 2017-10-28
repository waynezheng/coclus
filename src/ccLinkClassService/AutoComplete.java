package ccLinkClassService;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AutoComplete {
	private static Directory directory = null;
	private static String filePath = "c://logIndexPath_forAutoComplete";
	static Version matchVersion = Version.LUCENE_35;
	
	public static JSONArray getResults(String pre) {
		JSONArray ja = new JSONArray();
		try {
			Directory directory = FSDirectory.open(new File(filePath));
			IndexReader reader = IndexReader.open(directory);
			@SuppressWarnings("resource")
			IndexSearcher indexSearcher = new IndexSearcher(reader);
			BooleanQuery query = new BooleanQuery();
			
			BooleanQuery prefixQuery = new BooleanQuery();
			BooleanQuery termQuery = new BooleanQuery();
			
			prefixQuery.setBoost((float) 2);
			termQuery.setBoost((float) 1.5);
			QueryParser qp_name = new QueryParser(Version.LUCENE_35, "localname", new StandardAnalyzer(Version.LUCENE_35));
			Query psq_name = null;
			try {
				psq_name = qp_name.parse(pre);
				psq_name.setBoost((float)3.0);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String[] kws = pre.split(" ");

			for (String kw : kws) {
				kw = kw.toLowerCase();
				if (kw.equals("http") || kw.equals("ftp")) {
					continue;
				}
				Query kwpq = new PrefixQuery(new Term("localname", kw));
				Query kwtq = new TermQuery(new Term("localname", kw));
				prefixQuery.add(kwpq, Occur.SHOULD);
				termQuery.add(kwtq, Occur.SHOULD);
			}
			
			query.add(psq_name, Occur.SHOULD);
			query.add(termQuery, Occur.SHOULD);
			query.add(prefixQuery, Occur.SHOULD);
			TopDocs topDocs = indexSearcher.search(query, 10);;
			ScoreDoc scoreDocs[] = topDocs.scoreDocs;
			for (int i=0; i<scoreDocs.length; i++) {
				Document doc = indexSearcher.doc(scoreDocs[i].doc);
				JSONObject jo = new JSONObject();
				jo.put("localname", doc.get("localname"));
				jo.put("uri", doc.get("uri"));
				ja.add(jo);
			}
			directory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ja;
	}
	
	public static IndexWriter getIndexWriter() {
		IndexWriter iw = null;
		Analyzer analyzer = new StandardAnalyzer(matchVersion);
		IndexWriterConfig iwc = new IndexWriterConfig(matchVersion, analyzer);
		try {
			iw = new IndexWriter(getDirectory(), iwc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iw;
	}

	private synchronized static Directory getDirectory() {
		if (directory == null) {
			if (isIndexExist()) {
				File file = new File(filePath);
				try {
					directory = new RAMDirectory(FSDirectory.open(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
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

	private static boolean isIndexExist() {
		File file = new File(filePath);
		return file.exists();
	}
	
	public static String getLocalName(String uri){
		String localname = null;
		int posHash = uri.lastIndexOf('#');
		if(posHash!=-1){
			if(posHash!=uri.length()-1)
				localname = uri.substring(posHash+1);
			else{
				localname = getLocalName(uri.substring(0,posHash));
			}
			
		}else{
			while(uri.lastIndexOf("/")!=-1&&uri.substring(uri.lastIndexOf("/")+1).equals(""))
				uri = uri.substring(0,uri.length()-1);
			if(uri.lastIndexOf("/")!=-1)
				localname = uri.substring(uri.lastIndexOf("/")+1);
			else if(uri.lastIndexOf(":")!=-1)
				localname = uri.substring(uri.lastIndexOf(":")+1);
			else 
				localname = uri;
		}
		localname = localname.replaceAll("_", " ");
		return localname;
	}
}
