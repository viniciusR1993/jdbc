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
		//Setando variaveis e inciando conexão com banco de dados
		//############################################################
		Connection conn = null;	//Declaramos como null no principio para ser fechado essas conexões no bloco finally
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			conn = DB.getConnection();	//Conecta no banco de dados. Toda parte dificil de conexão está na outra classe

			
			//############################################################
			//Recuperando dados de um Bando de dados
			//############################################################
			st = conn.createStatement();	//Seta o statement (para ler arquivos do Banco de dados)
			rs = st.executeQuery("select * from department");	//recebe uma string com comando sql e retorna a tabela sql (é iniciada no 0, que não possui dados)
			//Se utiliazrmos o first ele move para posição 1, beforeFirst ele move para posição 0, next vai para proxima linha da tabela e absolute(int) move para posição dada, lembrando que os dados reais começam com 1
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
					Statement.RETURN_GENERATED_KEYS);	//Esse segundo parametro é opcional, ele retorna a chev gerada
			pst.setString(1, "Carl Purple");	//Troca o primeiro ? por Carl Purple
			pst.setString(2, "carl@gmail.com");	//Troca o segundo ? por carl@gmail.com
			pst.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));	//O jdbc recebe um java.sql.Date, por isso o código enorme para fazer as transformação
			pst.setDouble(4, 3000.0);
			pst.setInt(5, 4); 
			//Executa o comando criado acima e retorna a quantidade de linhas afetada
			int rowsAffected = pst.executeUpdate();	
			//retorno da inserção
			if(rowsAffected > 0) {
				System.out.println("Done! Rows affected: " + rowsAffected);
				ResultSet rs2 = pst.getGeneratedKeys();	//retorna um resultSet com uma tabela de apenas uma coluna contendo os ID (dá certo devido o RETURN_GENERATED_KEYS)
				while(rs2.next()) {	//Para percorrer a tabela o ResultSet acima (nesse caso tem apenas uma isenrção)
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
			//Aqui insere na tabela departamente (não usa o ?). Esse é um exemplo de que o retorna da tabela ResultSet retorna vários valores
			pst = conn.prepareStatement(
					"insert into department (name) value ('D1'),('D2')", 
					Statement.RETURN_GENERATED_KEYS);
			//Executa o comando criado acima e retorna a quantidade de linhas afetada
			rowsAffected = pst.executeUpdate();
			//retorno da inserção
			if(rowsAffected > 0) {
				System.out.println("Done! Rows affected: " + rowsAffected);
				ResultSet rs2 = pst.getGeneratedKeys();	//retorna um resultSet com uma tabela de apenas uma coluna contendo os ID (dá certo devido o RETURN_GENERATED_KEYS)
				while(rs2.next()) {	//Para percorrer a tabela o ResultSet acima (nesse caso tem apenas uma isenrção)
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
					+ "WHERE "	//Sepre use o WHERE para não atualizar todos os campos
					+ "(DepartmentId = ?)");	//Atualiza quando DepartmentId igual a ? (condição where)
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
			//Desfazendo a atualização
			pst1.setDouble(1, -200.0);
			pst1.setInt(2, 2);
			pst1.executeUpdate();
			
			//############################################################
			//Deletando dados em um Banco de dados
			//############################################################
			//falha de integridade referencial quando você apaga uma linha que possui referencia em outra tabela, isso gera um exceçõa no java (nesse caso criamos uma classe pra tratar ela)
			System.out.println("Deletando D1 e D2 da tabela department");
			PreparedStatement pst2 = conn.prepareStatement(
					"DELETE FROM department "
					+ "WHERE "	//Também não deixe de colocar o Where, se não apaga todo mundo
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
					+ "WHERE "	//Também não deixe de colocar o Where, se não apaga todo mundo
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
			//Transação: é um operação que mantem a consistencia do Banco de dados (Propriedades: 1. Atomica - ou acontece tudo ou nada, 2. consistente, 3. isolada, 4. durável)
			//############################################################
			System.out.println("Fazendo transação na tabela selle");
			conn.setAutoCommit(false);	//Esse comando significa que todas as transações deve ficar pendentes com uma confirmação
			st = conn.createStatement();
			int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1");	//Atualiza salario do departamento 1
			/*A ideia e mostrar que executar o comando acima não atualiza o banco de dados se não foir utilizado o commt()
			Se retirarmos o comentário desse trecho ele vai gerar a exceção e não vai atualizar o banco de dados
			if(true) {
				throw new SQLException("Fake Error");	//Estamos forçando um erro para ele não executar a primeira atualização
			}*/
					
			int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2");
			conn.commit(); //Confirma a transação
			System.out.println("Done! Rows affected: " + rows1);
			System.out.println("Done! Rows affected: " + rows2);
			
			
			
		}catch(SQLException e) {
			try {
				conn.rollback();	//rollback para desfazer a transação	
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
			DB.closeConnection();	//Fecha a conexão (sempre feche a conexão).
		}
	}
}
