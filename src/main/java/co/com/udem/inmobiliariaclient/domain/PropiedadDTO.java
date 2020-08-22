package co.com.udem.inmobiliariaclient.domain;

import org.springframework.beans.factory.annotation.Autowired;

public class PropiedadDTO {
	
	private Long id;	
	private double area;
	private int numerohabitaciones;
	private int numerobaños;
	private String tipopropiedad;
	private double valor;
	@Autowired
	private RegistroDTO registro;

	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getArea() {
		return area;
	}
	public void setArea(double area) {
		this.area = area;
	}
	public int getNumerohabitaciones() {
		return numerohabitaciones;
	}
	public void setNumerohabitaciones(int numerohabitaciones) {
		this.numerohabitaciones = numerohabitaciones;
	}
	public int getNumerobaños() {
		return numerobaños;
	}
	public void setNumerobaños(int numerobaños) {
		this.numerobaños = numerobaños;
	}
	public String getTipopropiedad() {
		return tipopropiedad;
	}
	public void setTipopropiedad(String tipopropiedad) {
		this.tipopropiedad = tipopropiedad;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	public RegistroDTO getRegistro() {
		return registro;
	}
	public void setRegistro(RegistroDTO registro) {
		this.registro = registro;
	}
	public PropiedadDTO() {
		super();
	}
	public PropiedadDTO(Long id, double area, int numerohabitaciones, int numerobaños, String tipopropiedad,
			double valor, RegistroDTO registro) {
		super();
		this.id = id;
		this.area = area;
		this.numerohabitaciones = numerohabitaciones;
		this.numerobaños = numerobaños;
		this.tipopropiedad = tipopropiedad;
		this.valor = valor;
		this.registro = registro;
	}

	
	
	

	

}
