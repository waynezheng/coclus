package cocluster;


public class CosSimilarity
{
    public static double getSimilarity(int a[], int b[])
    {
        double sim = 0.0;
        if(a.length!=0&&b.length!=0){
        	double xy=0.0;
            for(int i=0;i<a.length;i++){
            	xy=xy+a[i]*b[i];
            }
            double x=0.0;
            for(int i=0;i<a.length;i++){
            	x=x+a[i]*a[i];
            }
            double y=0;
            for(int i=0;i<b.length;i++){
            	y=y+b[i]*b[i];
            }
            double cos=0.0;
            cos=xy / (Math.sqrt(x)* Math.sqrt(y));
            Double d = new Double(cos);
            boolean naN = Double.isNaN(d);
            if(!naN){
            	 sim = xy / (Math.sqrt(x)* Math.sqrt(y));
            }else{
            	sim=0.0;
            }
        }
        
        return sim;
    }
}
