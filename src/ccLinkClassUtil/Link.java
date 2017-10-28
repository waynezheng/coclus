package ccLinkClassUtil;

import java.util.ArrayList;
import java.util.HashSet;

public class Link {
	private String label;
	private ArrayList<String> elements;
	//是否匹配当前实体或实体集  匹配true;不为false!
	private boolean flag = false;
	
	public Link() {
		
	}
	
	public Link(String label) {
		this.label=label;
	}
	
	public Link(Link item) {
		this.label= item.getLabel();
		this.elements=item.getElements();
	}
	public Link(Element item) {
		this.label= item.getLabel();
		this.elements=item.getElements();
	}
	public String getLabel() {
		return label;
	}
	public ArrayList<String> getElements() {
		return elements;
	}	
	public Link setLabel(String label) {
		this.label = label;
		return this;
	}
	public Link setFlag(boolean flag) {
		this.flag = flag;
		return this;
	}
	public boolean isMatchCurrentEntities() {
		return flag;
	}
	public Link setElements(ArrayList<String> uri_elements) {
		this.elements = uri_elements;
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		Link other = (Link) obj;
		if (elements == null) {
			if (other.elements != null)
			return false;
		}
		if (elements.size()!=other.elements.size()){
			return false;
		}
		if(elements.containsAll(other.elements)){
			return true;
	    }
		return false;  
	}
	
	
	
	


}
