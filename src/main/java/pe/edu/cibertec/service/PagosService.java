package pe.edu.cibertec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.dao.PagosRepository;
import pe.edu.cibertec.entity.Pagos;
import pe.edu.cibertec.entity.Sector;

@Service
public class PagosService {
	@Autowired
	private PagosRepository repositorio;
	
	public void registrar(Pagos m) {
		repositorio.save(m);
	}
	
	 public List<Pagos> buscarCronogramaPorPrestatario(Integer cod,int idSoli) {
			return repositorio.buscarCronogramaPorPrestatario(cod,idSoli);
		}
	 
	 
}
