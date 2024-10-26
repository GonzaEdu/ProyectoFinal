package pe.edu.cibertec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.edu.cibertec.entity.Pagos;
import pe.edu.cibertec.entity.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

	@Query("select s from Solicitud s where s.usuarioRegistro.sector.id=?1 and s.usuarioRegistro.idUsuarioRegistra = ?2")
	public List<Solicitud> listaSolicitudesPorSector(int idSector , int usuRegistra);
	
	
	@Modifying
	@Query("update Solicitud s SET s.estado = 'APROBADO' WHERE s.id = ?1")
	public void actualizarSolicitudAprobado(int id);

	@Modifying
	@Query("update Solicitud s SET s.estado = 'DESAPROBADO' WHERE s.id = ?1")
	public void actualizarSolicitudDesaprobado(int id);
	
	@Query("SELECT s FROM Solicitud s WHERE s.usuarioRegistro.id = ?1")
	public List<Solicitud> listaSolicitudesPorUsuario(Integer usuarioId);
	
	@Query("select s from Solicitud s where s.usuarioRegistro.id=?1 and  s.estado= 'APROBADO'")
	public List<Solicitud> solicitudesaprobadas(Integer d);
	
	
	
	
	
}
