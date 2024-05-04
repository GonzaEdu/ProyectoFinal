package pe.edu.cibertec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.dao.SectorRepository;
import pe.edu.cibertec.dao.SolicitudRepository;
import pe.edu.cibertec.entity.Solicitud;
import pe.edu.cibertec.entity.Usuario;

@Service
public class SolicituServiceImpl {
    @Autowired
    private SolicitudRepository repository;
    public void registrar(Solicitud m) {
        repository.save(m);
    }
    
    public List<Solicitud> listaSolicitudesPorPrestatariosdePrestamista(Integer cod) {
		return repository.listaSolicitudesPorSector(cod);
	}

    public void aprobarSolicitud(int cod) {
		repository.actualizarSolicitudAprobado(cod);
	}
    public void desaprobarSolicitud(int cod) {
		repository.actualizarSolicitudDesaprobado(cod);
	}
    
}
