package ccLinkClassUtil;

import java.util.HashSet;


//item即为用户浏览的实体(uri/uirs)--单uri实体
public class Item {
	//实体项的基本信息
	private String uri_id;
	private String uri_label;
	private HashSet<String> uri_elements;
	private double feature;
	
	public Item(String uri_id2) {
		this.uri_id=uri_id2;
	}
	public Item(Element e) {
		this.uri_id=e.getElements().toString();
	}
	public Item(Item item) {
		this.uri_id= item.getItemID();
	}
	
	public String getItemID() {
		return uri_id;
	}
	public String getItemLabel() {
		return uri_label;
	}
	public HashSet<String> getItemElements() {
		return uri_elements;
	}	
	public Item setItemID(String uri_id) {
		this.uri_id = uri_id;
		return this;
	}
	public Item setItemLabel(String uri_label) {
		this.uri_label = uri_label;
		return this;
	}
	public Item setItemElements(HashSet<String> uri_elements) {
		this.uri_elements = uri_elements;
		return this;
	}
	public Item setFeature(double similarity) {
		this.feature = similarity;
		return this;
	}
	public double getFeature() {
		return feature;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri_id == null) ? 0 : uri_id.hashCode());
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
		Item other = (Item) obj;
		if (uri_elements == null) {
			if (other.uri_elements != null)
			return false;
		}
		if (uri_elements.size()!=other.uri_elements.size()){
			return false;
		}
		if(uri_elements.containsAll(other.uri_elements)){
			return true;
	    }
		return false;  
	}
	
	public int compare(Item p){
		if(feature>p.getFeature()) return 1;
		else if(feature==p.getFeature()) return 0;
		else return -1;
	}
	


    //实体项的用户访问时间列表；对应用户列表
	
	


}
