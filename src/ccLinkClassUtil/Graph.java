package ccLinkClassUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;


public class Graph {
	
	public static final int INFINITY=Integer.MAX_VALUE;
	
	private HashMap<Object,Vertex> vertexMap=new HashMap<Object,Vertex>();
	
	public ArrayList<Vertex> toExpand = new ArrayList<Vertex>();
	public ArrayList<Vertex>  getAllpathelement(){
		return toExpand;
	}
	
	//璁﹙ertexMap鍙栧緱瀵筕ertex瀵硅薄鐨勫紩鐢�
	private Vertex getVertex(Object vertexName){
	   Vertex v=vertexMap.get(vertexName);
	   if(v==null)
	   {
		   v=new Vertex(vertexName);
		   vertexMap.put(vertexName,v);
	   }
	   return v;
	}
	//灏嗚窛绂诲垵濮嬪寲
	private void clearAll(){
		for(Iterator< Vertex> itr=vertexMap.values().iterator();itr.hasNext();)
		{
			itr.next().reset();
		}
	}
	//鏄剧ず瀹為檯鏈�鐭矾寰�
	private void printPath(Vertex dest){
		
		if(dest.path!=null)
		{
			toExpand.add(dest);
			printPath(dest.path);
		}
	}
	
	//娣诲姞涓�鏉℃柊鐨勮竟
	public void addEdge(Object sourceName,Object destName){
		
		Vertex v=getVertex(sourceName);
		Vertex w=getVertex(destName);
		v.adj.add(w);
	}
	//鏄剧ず涓�鏉¤矾寰�
	public void printPath(Object destName) throws NoSuchElementException{
		
		Vertex	w=vertexMap.get(destName);
		
		if(w==null)
			throw new NoSuchElementException("Destination vertex not found!");
		else if(w.dist==INFINITY){
			
		}
		//涓嶅彲杈剧殑瑕佺壒娈婂鐞嗕笅锛侊紒
		else {
			toExpand.clear();
			printPath(w);
		}
	}
	
	//鏃犳潈鏈�鐭矾寰勮绠�
	public void unweighted(Object startName){
		
		clearAll();
		Vertex start=vertexMap.get(startName);
		if(start==null)
			throw new NoSuchElementException("Start vertex not found!");
		LinkedList<Vertex> q=new LinkedList<Vertex>();
		q.addLast(start);
		start.dist=0;
		
		while(!q.isEmpty())
		{
			Vertex v=q.removeFirst();
			for(Iterator<Vertex> itr=v.adj.iterator();itr.hasNext();)
			{
				Vertex w=itr.next();
				if(w.dist==INFINITY)
				{
					w.dist=v.dist+1;
					w.path=v;
					q.addLast(w);
				}
				
			}
		}		
	}
}