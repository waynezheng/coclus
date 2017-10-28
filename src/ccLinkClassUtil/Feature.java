package ccLinkClassUtil;

import java.util.ArrayList;
import java.util.HashSet;


public class Feature {
	private int featureId=0;
	private String featureName="";
	private ArrayList<Property> members;
	private int favor_num = 0;
	private boolean isAvailable = false;
	//feature的hash
	private String hash = null;
	
	public Feature(HashSet<String> fprops){
		if(fprops==null || fprops.size()==0){
			throw new IllegalArgumentException("The members of the feature can not be null");
		}
		this.members = new ArrayList<Property>();
		for(String fprop:fprops){
			Property prop = new Property(fprop.substring(1),fprop.substring(0, 1));
			this.members.add(prop);
		}
	}
	
	public Feature(int id, String name, ArrayList<Property> members){
		if(members==null || members.size()==0){
			throw new IllegalArgumentException("The members of the feature can not be null");
		}
		this.featureId = id;
		this.featureName = name;
		this.members = members;
	}

	public int getFeatureId() {
		return featureId;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String attributeName) {
		this.featureName = attributeName;
	}
	
	public String getHash(){
		return  this.hash;
	}
	
	public void setHash(String h){
		this.hash = h;
	}

	public ArrayList<Property> getMembers() {
		return new ArrayList<Property>(members);
	}
	
	/**
	 * 得到一个feature中包含的所有属性
	 * @return
	 */
	public HashSet<String> getProperties() {
		HashSet<String>  result = new HashSet<String>();
		for(Property  p: this.members){
			result.add(p.getFuri());
		}
		return result;
	}


	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	public int getFavor_num() {
		return favor_num;
	}

	public void setFavor_num(int favor_num) {
		this.favor_num = favor_num;
	}

	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		HashSet<Property> set = null;
		if(members!=null){
			set = new HashSet<Property>(this.members);
		}
		result = prime * result + ((set == null) ? 0 : set.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		HashSet<Property> set1 = new HashSet<Property>(members);
		HashSet<Property> set2 = new HashSet<Property>(other.members);
		return set1.equals(set2);
	}

	public String toString(){
		return this.members.toString();
	}
}
