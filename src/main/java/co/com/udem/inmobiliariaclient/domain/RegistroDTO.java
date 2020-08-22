package co.com.udem.inmobiliariaclient.domain;

import org.springframework.beans.factory.annotation.Autowired;

public class RegistroDTO {
	
	private Long id;	
	private String numeroIdentificacion;
	private String nombres;
	private String apellidos;
	private String direccion;
	private Long telefono;
	private String email;
	private String password;
	@Autowired
	private TipoIdentificacionDTO tipoIdentificacion;

	
	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}
	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public Long getTelefono() {
		return telefono;
	}
	public void setTelefono(Long telefono) {
		this.telefono = telefono;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RegistroDTO() {
		super();
	}
	public TipoIdentificacionDTO getTipoIdentificacion() {
		return tipoIdentificacion;
	}
	public void setTipoIdentificacion(TipoIdentificacionDTO tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public RegistroDTO(Long id, String numeroIdentificacion, String nombres, String apellidos, String direccion,
			Long telefono, String email, String password,
			TipoIdentificacionDTO tipoIdentificacion) {
		super();
		this.id = id;
		this.numeroIdentificacion = numeroIdentificacion;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.direccion = direccion;
		this.telefono = telefono;
		this.email = email;
		this.password = password;
		this.tipoIdentificacion = tipoIdentificacion;
	}
	
	
	
	
	
	
	

}
