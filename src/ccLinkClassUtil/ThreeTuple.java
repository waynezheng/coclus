package ccLinkClassUtil;
/**
 * 三元对象
 * @author Saisai Gong
 *
 */
public class ThreeTuple<A,B,C> extends TwoTuple<A,B> {
	private C third;
	public ThreeTuple(A a, B b, C c){
		super(a,b);
		this.third = c;
	}
	
	public C getThird(){
		return this.third;
	}
	
	public void setThird(C c){
		this.third = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((third == null) ? 0 : third.hashCode());
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
		
		ThreeTuple threeTuple = (ThreeTuple) obj;
		if (this.getFirst() == null) {
			if (threeTuple.getFirst() != null)
				return false;
		} else if (!this.getFirst().equals(threeTuple.getFirst()))
			return false;
		if (this.getSecond() == null) {
			if (threeTuple.getSecond() != null)
				return false;
		} else if (!this.getSecond().equals(threeTuple.getSecond()))
			return false;
		if (this.third == null) {
			if (threeTuple.third != null)
				return false;
		} else if (!this.third.equals(threeTuple.third))
			return false;
		
		return true;
	}
	public String toString(){
		StringBuffer str = new StringBuffer("(");
		if(this.getFirst() != null){
			str.append(this.getFirst().toString());
			str.append(", ");
		}
		if(this.getSecond() != null){
			str.append(this.getSecond().toString());
			str.append(", ");
		}
		if(this.getThird() != null){
			str.append(this.getThird().toString());
		}
		str.append(")")	;
		return str.toString();
	}
}
