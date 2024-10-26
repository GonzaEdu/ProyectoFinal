package pe.edu.cibertec.dao;
import org.springframework.data.repository.query.Param;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import pe.edu.cibertec.entity.Enlace;
import pe.edu.cibertec.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

	@Query("select u from Usuario u where u.username=?1")
	public Usuario iniciarSesion(String vLogin);
	@Query("select e from RolEnlace re join re.enlace e where re.rol.descripcion=?1")
	public List<Enlace> traerEnlacesDelUsuario(String desRol);

	@Query("select u from Usuario u where u.rol.codigo=?1 ")
	public List<Usuario> listarUsuarioporRol(int codRol);
	
	@Query("select u from Usuario u where u.sector.idsectorsuperior=?1 and u.rol.codigo=3 ")
	public List<Usuario> listaUsuarioPorSectorGrande(int cod);
	
	@Query("select u from Usuario u where u.sector.id=?1 and u.rol.codigo=4 ")
	public List<Usuario> listaUsuarioPorSector(int cod);

	@Query("SELECT u FROM Usuario u WHERE u.nombre = ?1")
	public Usuario findByNombre(String nombre);
	@Query("select e from Usuario e where e.nombre = ?1 ")
	public abstract List<Usuario> listaPorUsuarioIgualRegistra(String nombre);
	
	public Usuario findByEmail(String correo);
	
	public Usuario findByUsername(String username);
	
	public Usuario findByDni(String dni);
}
