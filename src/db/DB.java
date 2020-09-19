package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
	
	private static Connection conn = null;
	
	//retorna a conexão, caso seja nula ele conecta ao banco de dados
	public static Connection getConnection() {
		if(conn == null) {
			try {
				Properties props = loadProperties();	//Execut o método loadProperties
				String url = props.getProperty("dburl");	//Retorna a dburl definida no arquivo dbproperties
				conn = DriverManager.getConnection(url,props);	//Conecta a um banco de dados e salva na variavel conn
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
			
		}
		return conn;
	}
	
	//Fecha a conexão
	public static void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	//Esse método faz a leitura do arquivo db.properties e armazena em um objeto
	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fs);	//Faz a leitura do arquivo e guarda os dados no objeto props
			return props;
		}catch(IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
}
