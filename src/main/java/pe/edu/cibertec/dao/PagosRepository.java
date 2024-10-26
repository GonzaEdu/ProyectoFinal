package pe.edu.cibertec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.edu.cibertec.entity.Pagos;
import pe.edu.cibertec.entity.Sector;

public interface PagosRepository extends JpaRepository<Pagos, Integer>{

	@Query("select p from Pagos p where p.usuario.id=?1 and p.solicitud.id =?2")
	public List<Pagos> buscarCronogramaPorPrestatario(int idUsuario, int idSolictud);
	
	
}
