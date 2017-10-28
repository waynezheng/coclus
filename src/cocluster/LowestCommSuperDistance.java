package cocluster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LowestCommSuperDistance {

	/**
	 * @param args
	 */
	public static double getSimilarity(String c1, String c2, Connection conn){
		double similarity = 0.0;
		if(c1.equals(c2)){
			similarity=1.0;
		}
		else{
//			int depth1 = 0;
//			int depth2 = 0;
//			int depth3 = 0;
//			ImportLocalHierarchy a=new ImportLocalHierarchy();
//			depth1=a.getDepthOfClassFromDB(c1, conn);
//			depth2=a.getDepthOfClassFromDB(c2, conn);
//			depth3=a.getDepthOfLCAFromDB(c1, c2, conn);
//			
//			similarity=(2.0*(double)depth3)/((double)depth1+(double)depth2);
		
			PreparedStatement ps;
			try {
				ps = conn.prepareStatement("SELECT sim from dbpedia_type_similarity "+
						"where type1=? and type2=?");
				ps.setString(1,c1);
				ps.setString(2,c2);
				java.sql.ResultSet rs = ps.executeQuery();//灏嗘煡璇㈢粨鏋滄斁鍒癛esultSet绫诲０鏄庣殑瀵硅薄RS閲�
				if(rs.next()){
					similarity = rs.getDouble("sim");
				}else{
					similarity=0.0;
				}
				rs.close();     
				ps.close();	
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return similarity;
	}
	
	public static void main(String[] args) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://114.212.86.204:3306/dbpedia2014";
		String username = "jdjiang";
		String password = "jdjiang";
		try {
			Class.forName(driver);
			Connection conn = null;
			try {
				conn = DriverManager.getConnection(url, username, password);
				double a= getSimilarity("http://dbpedia.org/ontology/Cartoon", "http://dbpedia.org/ontology/Work",  conn);
				
				
				conn.close();
		    }
			catch (SQLException e) {
					e.printStackTrace();
			}	
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
