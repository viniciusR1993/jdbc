package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import db.DB;
import db.DbException;

public class Program {

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		//############################################################
		//Setando variaveis e inciando conex�o com banco de dados
		//############################################################
		Connection conn = null;	//Declaramos como null no principio para ser fechado essas conex�es no bloco finally
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			conn = DB.getConnection();	//Conecta no banco de dados. Toda parte dificil de conex�o est� na outra classe

			
			//############################################################
			//Recuperando dados de um Bando de dados
			//############################################################
			st = conn.createStatement();	//Seta o statement (para ler arquivos do Banco de dados)
			rs = st.executeQuery("select * from department");	//recebe uma string com comando sql e retorna a tabela sql (� iniciada no 0, que n�o possui dados)
			//Se utiliazrmos o first ele move para posi��o 1, beforeFirst ele move para posi��o 0, next vai para proxima linha da tabela e absolute(int) move para posi��o dada, lembrando que os dados reais come�am com 1
			System.out.println("Imprimindo a tabela department");
			while(rs.next()) {	//Pega  aproxima linha da taleba sql
				System.out.println(rs.getInt("Id") + ", " + rs.getString("name"));	//Retorna um Inteiro no campo Id
			}
			System.out.println();
			System.out.println();
			
			
			//############################################################
			//Inserindo dados em um Banco de dados
			//############################################################					
			//Inserindo da tabela seller
			System.out.println("Adicionando dados a tabela seller");
			pst = conn.prepareStatement(		//Seta o preparedStatements, recebe codigo em SQL que mostra onde e como sera inserido os dados
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId)"
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",		//Place holder (lugar onde depois vai colocar o valor)
					Statement.RETURN_GENERATED_KEYS);	//Esse segundo parametro � opcional, ele retorna a chev gerada
			pst.setString(1, "Carl Purple");	//Troca o primeiro ? por Carl Purple
			pst.setString(2, "carl@gmail.com");	//Troca o segundo ? por carl@gmail.com
			pst.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));	//O jdbc recebe um java.sql.Date, por isso o c�digo enorme para fazer as transforma��o
			pst.setDouble(4, 3000.0);
			pst.setInt(5, 4); 
			//Executa o comando criado acima e retorna a quantidade de linhas afetada
			int rowsAffected = pst.executeUpdate();	
			//retorno da inser��o
			if(rowsAffected > 0) {
				System.out.println("Done! Rows affected: " + rowsAffected);
				ResultSet rs2 = pst.getGeneratedKeys();	//retorna um resultSet com uma tabela de apenas uma coluna contendo os ID (d� certo devido o RETURN_GENERATED_KEYS)
				while(rs2.next()) {	//Para percorrer a tabela o ResultSet acima (nesse caso tem apenas uma isenr��o)
					int id = rs2.getInt(1);	//Pega a primeira coluna
					System.out.println("Done! Id = " + id);
				}
			}else {
				System.out.println("No rown affected!");
			}
			rs = st.executeQuery("select * from seller");
			while(rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name") + ", " + rs.getString("Email") + ", " + rs.getDate("BirthDate") + ", " + rs.getDouble("BaseSalary") + ", " + rs.getInt("DepartmentId"));
			}
			System.out.println();
			System.out.println();
			
			System.out.println("Adicionando dados a tabela department");
			//Aqui insere na tabela departamente (n�o usa o ?). Esse � um exemplo de que o retorna da tabela ResultSet retorna v�rios valores
			pst = conn.prepareStatement(
					"insert into department (name) value ('D1'),('D2')", 
					Statement.RETURN_GENERATED_KEYS);
			//Executa o comando criado acima e retorna a quantidade de linhas afetada
			rowsAffected = pst.executeUpdate();
			//retorno da inser��o
			if(rowsAffected > 0) {
				System.out.println("Done! Rows affected: " + rowsAffected);
				ResultSet rs2 = pst.getGeneratedKeys();	//retorna um resultSet com uma tabela de apenas uma coluna contendo os ID (d� certo devido o RETURN_GENERATED_KEYS)
				while(rs2.next()) {	//Para percorrer a tabela o ResultSet acima (nesse caso tem apenas uma isenr��o)
					int id = rs2.getInt(1);	//Pega a primeira coluna
					System.out.println("Done! Id = " + id);
				}
			}else {
				System.out.println("No rown affected!");
			}
			rs = st.executeQuery("select * from department");
			while(rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
			}
			System.out.println();
			System.out.println();
			
			
			//############################################################
			//Atualizando dados em um Banco de dados
			//############################################################
			System.out.println("Atualizando dados na tabela department (BaseSalary)");
			PreparedStatement pst1 = conn.prepareStatement(
					"UPDATE seller "		//Atualiza a tabela seller
					+ "SET BaseSalary = BaseSalary + ?"	//Atualiza a coluna BaseSalary para BaseSalary + ?
					+ "WHERE "	//Sepre use o WHERE para n�o atualizar todos os campos
					+ "(DepartmentId = ?)");	//Atualiza quando DepartmentId igual a ? (condi��o where)
			pst1.setDouble(1, 200.0);	//Substitui o primeiro ? por 200.0
			pst1.setInt(2, 2);	//Substitui o segundo ? por 2
			
			rowsAffected = pst1.executeUpdate();	//Executa o comando acima e retorna a quantidade de linhas alteradas
			
			System.out.println("Done! Rows affected: " + rowsAffected);
			rs = st.executeQuery("select * from seller");
			while(rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name") + ", " + rs.getString("Email") + ", " + rs.getDate("BirthDate") + ", " + rs.getDouble("BaseSalary") + ", " + rs.getInt("DepartmentId"));
			}
			System.out.println();
			System.out.println();
			//Desfazendo a atualiza��o
			pst1.setDouble(1, -200.0);
			pst1.setInt(2, 2);
			pst1.executeUpdate();
			
			//############################################################
			//Deletando dados em um Banco de dados
			//############################################################
			//falha de integridade referencial quando voc� apaga uma linha que possui referencia em outra tabela, isso gera um exce��a no java (nesse caso criamos uma classe pra tratar ela)
			System.out.println("Deletando D1 e D2 da tabela department");
			PreparedStatement pst2 = conn.prepareStatement(
					"DELETE FROM department "
					+ "WHERE "	//Tamb�m n�o deixe de colocar o Where, se n�o apaga todo mundo
					+ "Name = ?"
					);
			pst2.setString(1, "D1");	//Apaga o departmetn D1 criando antes
			rowsAffected = pst2.executeUpdate();
			System.out.println("Done! Rows affected: " + rowsAffected);
			
			pst2.setString(1, "D2");	//Apaga o departmetn D2 criando antes
			rowsAffected = pst2.executeUpdate();
			System.out.println("Done! Rows affected: " + rowsAffected);
			
			pst2 = conn.prepareStatement(
					"DELETE FROM seller "
					+ "WHERE "	//Tamb�m n�o deixe de colocar o Where, se n�o apaga todo mundo
					+ "Name = ?"
					);
			rs = st.executeQuery("select * from department");
			while(rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
			}
			System.out.println();
			System.out.println();
			
			System.out.println("Deletando Carl Purple da tabela seller");
			pst2.setString(1, "Carl Purple"); //Apaga o seller criando antes
			rowsAffected = pst2.executeUpdate();
			System.out.println("Done! Rows affected: " + rowsAffected);
			rs = st.executeQuery("select * from seller");
			while(rs.next()) {
				System.out.println(rs.getInt("Id") + ", " + rs.getString("Name") + ", " + rs.getString("Email") + ", " + rs.getDate("BirthDate") + ", " + rs.getDouble("BaseSalary") + ", " + rs.getInt("DepartmentId"));
			}
			System.out.println();
			System.out.println();
			
			
			//############################################################
			//Transa��o: � um opera��o que mantem a consistencia do Banco de dados (Propriedades: 1. Atomica - ou acontece tudo ou nada, 2. consistente, 3. isolada, 4. dur�vel)
			//############################################################
			System.out.println("Fazendo transa��o na tabela selle");
			conn.setAutoCommit(false);	//Esse comando significa que todas as transa��es deve ficar pendentes com uma confirma��o
			st = conn.createStatement();
			int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1");	//Atualiza salario do departamento 1
			/*A ideia e mostrar que executar o comando acima n�o atualiza o banco de dados se n�o foir utilizado o commt()
			Se retirarmos o coment�rio desse trecho ele vai gerar a exce��o e n�o vai atualizar o banco de dados
			if(true) {
				throw new SQLException("Fake Error");	//Estamos for�ando um erro para ele n�o executar a primeira atualiza��o
			}*/
					
			int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2");
			conn.commit(); //Confirma a transa��o
			System.out.println("Done! Rows affected: " + rows1);
			System.out.println("Done! Rows affected: " + rows2);
			
			
			
		}catch(SQLException e) {
			try {
				conn.rollback();	//rollback para desfazer a transa��o	
				throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
			}
			catch(SQLException e1) {
				throw new DbException("Erro trying to rollback!: " + e1.getMessage());
			}

		}catch(java.text.ParseException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);	//Fecha o result set
			DB.closeStatement(st);	//fecha o statement
			DB.closeConnection();	//Fecha a conex�o (sempre feche a conex�o).
		}
	}
}
