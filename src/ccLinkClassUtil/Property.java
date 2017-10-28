package ccLinkClassUtil;


/**
 * uri标识的RDF property
 * @author ssgong
 *
 */
public class Property extends AbstractProperty implements Comparable<Property>{
    private String uri;
    private boolean inverse;
    
    public Property(String uri){
    	this.uri = uri;
    	this.inverse = false;
    }
    
    public Property(String uri,boolean inverse){
    	this.uri = uri;
    	this.inverse = inverse;
    }
    
    public Property(String uri,String inverse){
    	this.uri = uri;
    	this.inverse = inverse.equals("1")?true:false;
    }
    
    public String getURI(){
    	return this.uri;
    }
    
    public boolean isInversed(){
    	return inverse;
    }
    
	public String getInverse() {
		return inverse?"1":"0";
	}
    
    public void setInverse(boolean inverse){
    	this.inverse = inverse;
    }
    
	/**
	 * 方向+property uri
	 * @return
	 */
    public String getFuri() {
		return getInverse()+uri;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inverse ? 1231 : 1237);
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Property other = (Property) obj;
		if (inverse != other.inverse)
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	public int compareTo(Property arg0) {
		return this.uri.compareTo(arg0.getURI());
	}
	
	public String toString(){
		return (this.inverse?"1":"0")+this.uri;
	}
    
    
}
