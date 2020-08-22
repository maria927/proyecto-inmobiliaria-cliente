package co.com.udem.inmobiliariaclient.domain;

public class FiltroDTO {

	private double precioInicial;
	private double precioFinal;
	private int numeroHabitaciones;
	private double area;
	public double getPrecioInicial() {
		return precioInicial;
	}
	public void setPrecioInicial(double precioInicial) {
		this.precioInicial = precioInicial;
	}
	public double getPrecioFinal() {
		return precioFinal;
	}
	public void setPrecioFinal(double precioFinal) {
		this.precioFinal = precioFinal;
	}
	public int getNumeroHabitaciones() {
		return numeroHabitaciones;
	}
	public void setNumeroHabitaciones(int numeroHabitaciones) {
		this.numeroHabitaciones = numeroHabitaciones;
	}
	public double getArea() {
		return area;
	}
	public void setArea(double area) {
		this.area = area;
	}
	public FiltroDTO(double precioInicial, double precioFinal, int numeroHabitaciones, double area) {
		super();
		this.precioInicial = precioInicial;
		this.precioFinal = precioFinal;
		this.numeroHabitaciones = numeroHabitaciones;
		this.area = area;
	}
	public FiltroDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
