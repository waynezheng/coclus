package cocluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author jidong
 */
public class KMeans2 {
	
	public static void main(String[] args) {
		double[][] simLC=new double[20][24];
		simLC[0][23]=1.0;
		simLC[1][1]=18.65475810617763;
		simLC[1][5]=15.748015748023622;
		simLC[2][4]=1.7320508075688772;
		simLC[3][4]=1.7320508075688772;
		simLC[4][11]=1.0;
		simLC[4][14]=2.6457513110645907;
		simLC[4][18]=1.4142135623730951;
		simLC[5][1]=13.19090595827292;
		simLC[5][5]=11.135528725660043;
		simLC[6][6]=4.47213595499958;
		simLC[6][13]=0.5;
		simLC[6][14]=5.291502622129181;
		simLC[7][1]=16.155494421403514;
		simLC[7][17]=7.549834435270751;
		simLC[8][1]=9.327379053088816;
		simLC[8][5]=7.874007874011811;
		simLC[9][14]=2.6457513110645907;
		simLC[9][15]=1.0;
		simLC[9][18]=1.4142135623730951;
		simLC[10][2]=2.82842712474619;
		simLC[10][8]=1.0;
		simLC[10][10]=2.82842712474619;
		simLC[10][20]=1.0;
		simLC[10][21]=1.0;
		simLC[10][22]=1.0;
		simLC[11][3]=1.0;
		simLC[11][4]=1.7320508075688772;
		simLC[11][12]=1.0;
		simLC[12][0]=0.17149858514250882;
		simLC[12][1]=54.38749856354859;
		simLC[12][5]=44.56257992320199;
		simLC[12][16]=0.17149858514250882;
		simLC[13][1]=55.964274318532894;
		simLC[13][5]=45.93171259840223;
		simLC[13][19]=0.16666666666666666;
		simLC[14][1]=9.327379053088816;
		simLC[14][17]=4.358898943540674;
		simLC[15][1]=24.67792535850613;
		simLC[15][5]=14.8804761828569;
		simLC[15][7]=1.5118578920369088;
		simLC[15][9]=1.5118578920369088;
		simLC[16][2]=2.82842712474619;
		simLC[16][8]=1.0;
		simLC[16][10]=2.82842712474619;
		simLC[16][20]=1.0;
		simLC[16][21]=1.0;
		simLC[16][22]=1.0;
		simLC[17][6]=3.162277660168379;
		simLC[17][14]=3.7416573867739413;
		simLC[18][1]=13.19090595827292;
		simLC[18][7]=2.82842712474619;
		simLC[18][9]=2.82842712474619;
		simLC[19][1]=39.572717874818764;
		simLC[19][17]=18.493242008906932;
		
		KMeans2 k = new KMeans2();
	
		int[] r = new int[20];	
		r = k.kmeans(simLC, 8);
	}
	
    public int[] initClusterCenters(double[][] features, int K) {
    	int R = features.length;
    	int[] centerIndex = new int[K];
    	ArrayList<double[]> cur = new ArrayList<double[]>(K);
    	HashSet<Integer> ind = new HashSet<Integer>(R);
    	for (int i=0; i<R; i++) {
    		ind.add(i);
    	}
    	Random rd = new Random();
    	int init = Math.abs(rd.nextInt() % (R-1));
    	cur.add(features[init]);
    	ind.remove(init);
    	for (int i=0; i<K-1; i++) {
    		int maxIndex = -1;
    		double maxDistance = -1.0;
    		for (int index: ind) {
    			double d = 0;
    			for (double[] a: cur) {
    				d += distance(features[index], a);
    			}
    			if (d > maxDistance) {
    				maxIndex = index;
    				maxDistance = d;
    			}
    		}
    		
    		if (maxIndex == -1)
    		cur.add(features[maxIndex]);
    		ind.remove(maxIndex);
    		centerIndex[i] = maxIndex; 
    	}
    	return centerIndex;
    }

    public int[] kmeans(double[][] features, int K) {
    	int R = features.length;
    	int C = features[0].length;
    	
    	double[][] clusterCenters = new double[K][C];
    	int[] clusterIndex = new int[R];

    	int[] rd = new int[K];
    	rd = initClusterCenters(features, K);
        for (int i=0; i<rd.length; i++) {
        	clusterCenters[i] =features[rd[i]]; 
        }
        boolean isContinue = true; 
        double[][] temp = new double[clusterCenters.length][clusterCenters[0].length]; //用于记录当前质心集合
        double[][] newClusterCenters= new double[clusterCenters.length][clusterCenters[0].length]; //用于记录新生成的质心集合

        int num = 0;
        while (isContinue) {
        	copy(temp, clusterCenters);
	        for (int i=0; i<R; i++) {
	        	double[] dist = new double[K];
	        	for (int j=0; j<K; j++) {
	        		dist[j] = distance(features[i], clusterCenters[j]);
	        	}
	        	clusterIndex[i] = minIndex(dist);
	        }

	        ArrayList<ArrayList<double[]>> clusters = new ArrayList<ArrayList<double[]>>(K);
	        for (int i=0; i<K; i++) {
	        	ArrayList<double[]> a = new ArrayList<double[]>();
	        	clusters.add(a);
	        }
	        for (int i=0; i<clusterIndex.length; i++) {
	        	clusters.get(clusterIndex[i]).add(features[i]);
	        }	        
	        for (int i=0; i<clusters.size(); i++) {
	        	newClusterCenters[i] = centroids(clusters.get(i), C);
	        }
	        copy(clusterCenters, newClusterCenters);
	        /*
	         * 判断质心集合是否变化
	         * 没有变化或者迭代到达10次则终止循环
	         */
	        num++;
	        if (isSame(clusterCenters, temp) || num == 10) {
	        	isContinue = false;
	        }
        }
        return clusterIndex;
    }
    
    /*
     * 随机在min到max的范围内生出n个随机整数
     */
    private int[] randomArray(int min, int max, int n) {
    	int len = max - min + 1;
    	if (max < min || n > len) {
    		return null;
    	}
    	int[] source = new int[len];
    	for (int i=min; i<min+len; i++) {
    		source[i-min] = i;
    	}
    	int[] result = new int[n];
    	Random rd = new Random();
    	int index = 0;
    	for (int i=0; i<result.length; i++) {
    		index = Math.abs(rd.nextInt() % len--);
    		result[i] = source[index];
    		source[index] = source[len];
    	}
    	return result;
    }
    
    /*
     * 计算两个一维数组的欧式距离
     */
    private double distance (double[] a, double[] b) {
    	double sum = 0.0;
    	for (int i=0; i<a.length; i++) {
    		sum += (a[i]-b[i]) * (a[i]-b[i]);
    	}
    	sum = Math.sqrt(sum);
    	return sum;
    }
    
    /*
     * 查找数组中最小的元素
     * 返回它的下标
     */
    private int minIndex(double[] a) {
    	double min = 9999999.9;
    	int index = 0;
    	for (int i=0; i<a.length; i++) {
    		if (a[i] < min) {
    			min = a[i];
    		} 
    	}
    	for (int i=0; i<a.length; i++) {
    		if (a[i] == min) {
    			index = i;
    			break;
    		}
    	}
    	return index;
    }

    /*
     * 计算二维数组的质心元组
     */
    private double[] centroids(ArrayList<double[]> a, int col) {
    	double[] b = new double[col];
    	for (int i=0; i<b.length; i++) {
    		b[i] = 0.0;
    	}
    	for (int i=0; i<a.size(); i++) {
    		for (int j=0; j<a.get(0).length; j++) {
    			b[j] += a.get(i)[j];
    		}
    	}
    	for (int i=0; i<b.length; i++) {
    		b[i] = b[i] / (double)a.size();
    	}
    	return b;
    }
    
    /*
     * 判断两个二维数组是否相同
     */
    private boolean isSame(double[][] a, double[][] b) {
    	boolean same = true;
    	for (int i=0; i<a.length; i++) {
    		if (!isSame(a[i], b[i])) {
    			same = false;
    			break;
    		}
    	}
    	return same;
    }
    
    /*
     * 判断两个一维数组是否相同
     */
    private boolean isSame(double[] a, double[] b) {
    	boolean same = false;
    	double delta = distance(a, b);
    	if (delta <= 0.1) {
    		same = true;
    	}
    	return same;
    }
    
    /*
     * 复制二维数组
     */
    private void copy(double[][] a, double[][] b) {
    	for (int i=0; i<a.length; i++) {
    		for (int j=0; j<a[0].length; j++) {
    			a[i][j] = b[i][j];
    		}
    	}
    }
    
    /*
     * 寻找每个属性列的最大属性值
     * 用于归一化
     */
    private double[] maxCol(double[][] a) {
    	double[] max = new double[a[0].length];
    	for (int i=0; i<max.length; i++) {
    		max[i] = 0.0;
    	}
    	for (int i=0; i<a.length; i++) {
    		for (int j=0; j<a[0].length; j++) {
    			if (a[i][j] > max[j])
    				max[j] = a[i][j];
    		}
    	}
    	return max;
    }
    
}
