package co.com.udem.inmobiliariaclient.domain;

public class TipoIdentificacionDTO {
	
	private Long id;
	private String tipoDocumento;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public TipoIdentificacionDTO(Long id, String tipoDocumento) {
		super();
		this.id = id;
		this.tipoDocumento = tipoDocumento;
	}
	public TipoIdentificacionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
