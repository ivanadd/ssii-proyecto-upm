package proyecto_ssii.models;

@SuppressWarnings("unused")
public class Usuario {
	private String nombre;
	private String dni;
	private String matricula;
	
	public Usuario(String nombre, String dni, String matricula) {
		this.nombre = nombre;
		this.dni = dni;
		this.matricula = matricula;
	}
	
}
