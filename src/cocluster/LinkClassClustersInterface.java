package cocluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ccLinkClassUtil.ThreeTuple;
import ccLinkClassUtil.TwoTuple;
import ccLinkClassUtil.UserData;

public interface LinkClassClustersInterface {
	
//  link cluster class cluster
	public ThreeTuple<ArrayList<ArrayList<String>>, ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> getAllLinkClassCluster();
	
// 
	public ThreeTuple<ArrayList<ArrayList<String>>,ArrayList<ArrayList<TwoTuple<String, HashSet<String>>>>, 
	ArrayList<ThreeTuple<Integer, Integer, Double>>> getMinLinkClassCluster
	(ArrayList<String> userPath);
	
	public Integer getAllLinkedEntities();

}
