package application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DB;
import db.DbException;

public class Program {

	public static void main(String[] args) {
		Connection conn = null;	//Declaramos como null no principio para ser fechado essas conexões no bloco finally
		Statement st = null;
		ResultSet rs = null;
		try{
			conn = DB.getConnection();	//Conecta no banco de dados. Toda parte dificil de conexão está na outra classe
			st = conn.createStatement();	//Seta o statement
			rs = st.executeQuery("select * from department");	//recebe uma string com comando sql e retorna a tabela sql (é iniciada no 0, que não possui dados)
			while(rs.next()) {	//Pega  aproxima linha da taleba sql
				System.out.println(rs.getInt("Id") + ", " + rs.getString("name"));	//Retorna um Inteiro no campo Id
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);	//Fecha o result set
			DB.closeStatement(st);	//fecha o statement
			DB.closeConnection();	//Fecha a conexão (sempre feche a conexão).
		}
		
		

	}

}
