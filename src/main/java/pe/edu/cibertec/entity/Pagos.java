package pe.edu.cibertec.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="pagos")
public class Pagos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idpagos")
	private int codigo;
	 @Column(name = "montodiario")
	    private Double montodiario;
	 @Column(name = "estado")
	    private String estado;
	 
	 @Temporal(TemporalType.DATE)
	    @DateTimeFormat(pattern = "yyyy-MM-dd")
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "America/Lima")
	    @Column(name = "fecha")
	    private Date fecha;
	 
	 @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "idusuario_usuario")
	    private Usuario usuario;
	 
	 @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "idsolicitud_solicitud")
	    private Solicitud solicitud;
	 
	public int getCodigo() {
		return codigo;
	}
	public Solicitud getSolicitud() {
		return solicitud;
	}
	public void setSolicitud(Solicitud solicitud) {
		this.solicitud = solicitud;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public Double getMontodiario() {
		return montodiario;
	}
	public void setMontodiario(Double montodiario) {
		this.montodiario = montodiario;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	 
	 
	 
}
