package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ErrorLogDAO {
	
    DataSource dataSource = null;
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    
	public ErrorLogDAO() {
        try {
            // 드라이버 로딩
            Class.forName("oracle.jdbc.driver.OracleDriver");
            
            // 데이터베이스 연결 설정
            Context context = new InitialContext();
            this.dataSource = (DataSource) context.lookup("java:comp/env/jdbc/your_datasource_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("ErrorLogDAO() : dataSource=" + (dataSource != null));
	}
	
    public List<ErrorLog> getAllErrorLogs() {
        List<ErrorLog> errorLogs = new ArrayList<>();
        
        int petot = getProductPetot();
        int etot = getProductEtot();
        
        try {
            connection = this.dataSource.getConnection();
            
            // 데이터 조회
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ERROR_LOG");
            
            // 결과 처리
            while (resultSet.next()) {
                String productCode = resultSet.getString("P_CODE");
                String productName = resultSet.getString("P_NAME");
                String errorCode = resultSet.getString("E_CODE");
                String errorName = resultSet.getString("E_NAME");
                int errorQuantity = resultSet.getInt("ERROR_COUNT");
                String errorDate = resultSet.getString("ERROR_DATE");
                
                ErrorLog errorLog = new ErrorLog(productCode, productName, errorCode, errorName, errorQuantity, errorDate, petot, etot);
                errorLogs.add(errorLog);
            }
            
            // 연결 및 리소스 정리
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        	try {
	        	if(statement != null) {
	        		statement.close();
	        	}
	        	
	        	if(connection != null) {
	        		connection.close();
	        	}
        	}
        	catch(SQLException e) {
        		System.out.println("[LOG]connection, statement exception: " + e.getMessage());
        		
        	}
        }
        
        return errorLogs;
    }
    
    // 생산실적 총합
    public int getProductPetot() {
        
        int petot = 0;
        
        try {
            connection = this.dataSource.getConnection();
            
            // 데이터 조회
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT SUM(PTOT) as PETOT FROM (SELECT DISTINCT P_NAME, COUNT(*) OVER(PARTITION BY P_NAME) as PTOT FROM PRODUCT WHERE P_NAME IN ('여름 반팔A','여름 반팔B', '여름 반팔C'))");
            
            // 결과 처리
            while (resultSet.next()) {
                petot = resultSet.getInt("PETOT");
            }
            
            // 연결 및 리소스 정리
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        	try {
        		if(resultSet != null) {
        			resultSet.close();
        		}
	        	if(statement != null) {
	        		statement.close();
	        	}
	        	
	        	if(connection != null) {
	        		connection.close();
	        	}
        	}
        	catch(SQLException e) {
        		System.out.println("[PETOT]connection, statement exception: " + e.getMessage());
        		
        	}
        }
        
        return petot;
    }    
    
    // 부적합 총합
	public int getProductEtot() {
    
    int etot = 0;
    
    try {
        connection = this.dataSource.getConnection();
        
        // 데이터 조회
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT SUM(ERROR_COUNT) as ETOT FROM ERROR_LOG");
        
        // 결과 처리
        while (resultSet.next()) {
            etot = resultSet.getInt("ETOT");
        }
        
        // 연결 및 리소스 정리
    } catch (Exception e) {
        e.printStackTrace();
    }
    finally {
    	try {
    		if(resultSet != null) {
    			resultSet.close();
    		}
        	if(statement != null) {
        		statement.close();
        	}
        	
        	if(connection != null) {
        		connection.close();
        	}
    	}
    	catch(SQLException e) {
    		System.out.println("[ETOT]connection, statement exception: " + e.getMessage());
    		
    	}
    }
    
    return etot;
	}    
}