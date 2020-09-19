package db;

public class DbIntegrityException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	//Exceção personalizada de integridade referencial (devido a deleção)
	public DbIntegrityException (String msg) {
		super(msg);
	}

}
