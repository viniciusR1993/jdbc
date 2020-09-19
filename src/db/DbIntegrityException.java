package db;

public class DbIntegrityException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	//Exce��o personalizada de integridade referencial (devido a dele��o)
	public DbIntegrityException (String msg) {
		super(msg);
	}

}
