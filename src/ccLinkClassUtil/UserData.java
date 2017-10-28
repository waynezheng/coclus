package ccLinkClassUtil;

import cocluster.LinkClassClusters;

public class UserData{

	private String userName = null;
	private LinkClassClusters linkclasscluster = null;
	private String prevEID = null;
	
	public LinkClassClusters getPreviousLinkClassClusters(String eid){
		this.prevEID = eid;
		if(prevEID != null && prevEID.equals(eid)){
			if(this.linkclasscluster != null && this.linkclasscluster.getCurrentUri().equals(eid)){
				return this.linkclasscluster;
			}
		}
		LinkClassClusters lcc=new LinkClassClusters(eid);
		this.linkclasscluster = lcc;
		return this.linkclasscluster;
	}
	

	public UserData(){
	}

	public String getUserName(){
		return this.userName;
	}
	

}
