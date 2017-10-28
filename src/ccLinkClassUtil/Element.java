package ccLinkClassUtil;

import java.util.ArrayList;

public class Element {
	//瀹炰綋椤圭殑鍩烘湰淇℃伅
	private String label="";
	ArrayList<String> elements=new 	ArrayList<String>();
	//Chain:鍚湁绌虹櫧鑺傜偣涓簍rue;涓嶅惈涓篺alse!
	private  boolean flag = false;
	private  int blockID = 0;
	//浜ょ殑link涓簍rue;闈炰氦鐨勪负false!
	private  boolean Intersectionflag = false;
	
	public Element() {
	}
	
	public Element(String label) {
		this.label=label;
	}
	
	public Element(Element item) {
		this.label= item.getLabel();
		this.elements=item.getElements();
	}
	public Element(Link item) {
		this.label= item.getLabel();
		this.elements=item.getElements();
	}
	public Element(Feature f) {
		this.label= f.getFeatureName();
		for(Property p: f.getMembers()){
			String s=p.getFuri();
			this.elements.add(s);
		}
	}
	
	public String getLabel() {
		return label;
	}

	public ArrayList<String> getElements() {
		return elements;
	}
	public boolean haveBNode() {
		return flag;
	}
	public boolean isIntersectionLink() {
		return Intersectionflag;
	}
	
	public int getblockID() {
		return blockID;
	}
	
	public Element setLabel(String label) {
		this.label = label;
		return this;
	}

	public Element setElements(ArrayList<String> elements) {
		this.elements = elements;
		return this;
	}
	public Element setFlag(boolean flag) {
		this.flag = flag;
		return this;
	}
	public Element setIntersectionFlag(boolean flag) {
		this.Intersectionflag = flag;
		return this;
	}
	public Element setBlockID(int blockId) {
		this.blockID = blockId;
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
//		for(String element:elements){
//			result = prime * result + element.hashCode();
//		}
		for(int i=0;i<elements.size();i++){
			result = prime * result + elements.get(i).hashCode();
		}
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
		Element other = (Element) obj;
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
