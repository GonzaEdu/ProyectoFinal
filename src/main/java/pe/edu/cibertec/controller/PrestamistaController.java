package pe.edu.cibertec.controller;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import pe.edu.cibertec.entity.Enlace;
import pe.edu.cibertec.entity.Pagos;
import pe.edu.cibertec.entity.Rol;
import pe.edu.cibertec.entity.Sector;
import pe.edu.cibertec.entity.Solicitud;
import pe.edu.cibertec.entity.Usuario;
import pe.edu.cibertec.service.PagosService;
import pe.edu.cibertec.service.RolService;
import pe.edu.cibertec.service.SectorServiceImpl;
import pe.edu.cibertec.service.SolicituServiceImpl;
import pe.edu.cibertec.service.UsuarioService;

@Controller
@RequestMapping("/prestamista")
@SessionAttributes({"ENLACES","DATOSDELUSUARIO","IDUSUARIO","IDSECTORUSUARIO"})

public class PrestamistaController {
	
	@Autowired
	private UsuarioService servicioUsu;
	
	@Autowired
	private SolicituServiceImpl servicioSol;
	
	@Autowired
	private PagosService pagosservice;
	
	@Autowired
	private RolService servicioRol;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private SectorServiceImpl sectorService;
	
	@RequestMapping("/principal")
	public String intranet(Authentication auth,Model model) {
		String nomRol=auth.getAuthorities().stream()
			      .map(GrantedAuthority::getAuthority)
			      .collect(Collectors.joining(","));
		List<Enlace> enlaces=servicioUsu.enlacesDelUsuario(nomRol);
		
		Usuario usu=servicioUsu.sesionUsuario(auth.getName());

		//atributo
		model.addAttribute("IDSECTORUSUARIO", usu.getSector());
		model.addAttribute("ENLACES",enlaces);
		model.addAttribute("DATOSDELUSUARIO",usu.getNombre()+" "+ usu.getApellido());
		model.addAttribute("IDUSUARIO", usu.getId());

		return "principal";
	}
	
	@RequestMapping("/registrarPrestatarios")
	public String registrarPrestatarios(Model model){
		
		return "registrarPrestatario";
	}
	@RequestMapping("/listarPrestatarios")
	public String listarPrestatarios(Model model){
		Usuario usuejemplo =servicioUsu.buscarPorID((Integer) model.getAttribute("IDUSUARIO"));
		int idsectorejemplo = usuejemplo.getSector().getId();
		model.addAttribute("Prestatarios",servicioUsu.listarUsuarioporSector(idsectorejemplo));
		
		return "listarPrestatarios";
	}
	
	
	@RequestMapping("/grabarPrestatario")
	public ResponseEntity<String>  grabarPrestatarios(Model model,
						 @RequestParam("id") Integer id,
						 @Valid@RequestParam("nombre") String nom,
						 @Valid@RequestParam("apellido") String ape,
						 @Valid@RequestParam("email") String ema,
						 @Valid@RequestParam("dni") String dni,
						 @Valid@RequestParam("telefono") String tel,
						 @Valid@RequestParam("username") String use,
						 @Valid@RequestParam("password") String pas,
						 RedirectAttributes redirect,HttpServletRequest request) {		
		try {
			String var;
			var = encoder.encode(pas);
			Usuario usu=new Usuario();
			
			if(servicioUsu.buscaDni(dni) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"El DNI ya está registrado\"}");
			}else if(servicioUsu.buscarPorNombre(nom) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"El Nombre ya está registrado\"}");
			}else if(servicioUsu.buscaUsername(use) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"El Username ya está registrado\"}");
			}else if(servicioUsu.buscaEmail(ema) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"El Correo ya está registrado\"}");
			}else {
			
			usu.setNombre(nom);
			usu.setApellido(ape);
			usu.setEmail(ema);
			usu.setTelefono(tel);
			usu.setUsername(use);
			usu.setDni(dni);
			usu.setIdUsuarioRegistra((Integer) model.getAttribute("IDUSUARIO"));
			
			
			Usuario usuejemplo =servicioUsu.buscarPorID((Integer) model.getAttribute("IDUSUARIO"));
			int idsectorejemplo = usuejemplo.getSector().getId();
		            Sector sector = new Sector();
		            sector.setId(idsectorejemplo);
		            
		            usu.setSector(sector);
		       

			Rol r=new Rol();
			r.setCodigo(4);
			usu.setRol(r);
			if(id ==0) {
				usu.setPassword(var);
				servicioUsu.registrar(usu);
				redirect.addFlashAttribute("MENSAJE","Prestamista registrado");
			}
			else {
				usu.setId(id);
				usu.setPassword(pas);
				servicioUsu.actualizar(usu);
				redirect.addFlashAttribute("MENSAJE","Prestatario actualizado");
			}
			
			redirect.addFlashAttribute("MENSAJE","Prestatario registrado");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("Usuario registrado exitosamente");
	}
	
	@RequestMapping("/actualizarPrestatario")
	public String  actualizarPrestatario(Model model,
						 @RequestParam("id") Integer id,
						 @Valid@RequestParam("nombre") String nom,
						 @Valid@RequestParam("apellido") String ape,
						 @Valid@RequestParam("email") String ema,
						 @Valid@RequestParam("dni") String dni,
						 @Valid@RequestParam("telefono") String tel,
						 @Valid@RequestParam("username") String use,
						 @Valid@RequestParam("password") String pas,
						 RedirectAttributes redirect,HttpServletRequest request) {		
		try {
			String var;
			var = encoder.encode(pas);
			Usuario usu=new Usuario();
			
			
			usu.setNombre(nom);
			usu.setApellido(ape);
			usu.setEmail(ema);
			usu.setTelefono(tel);
			usu.setUsername(use);
			usu.setDni(dni);
			usu.setIdUsuarioRegistra((Integer) model.getAttribute("IDUSUARIO"));
			
			
			Usuario usuejemplo =servicioUsu.buscarPorID((Integer) model.getAttribute("IDUSUARIO"));
			int idsectorejemplo = usuejemplo.getSector().getId();
		            Sector sector = new Sector();
		            sector.setId(idsectorejemplo);
		            
		            usu.setSector(sector);
		       

			Rol r=new Rol();
			r.setCodigo(4);
			usu.setRol(r);
			
			
				usu.setId(id);
				usu.setPassword(pas);
				servicioUsu.actualizar(usu);
				redirect.addFlashAttribute("MENSAJE","Prestatario actualizado");
			
			
			redirect.addFlashAttribute("MENSAJE","Prestatario registrado");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  "redirect:/prestamista/listarPrestatarios";
	}
	
	@ModelAttribute("sectores")
	public List<Sector> getSector(){
		List<Sector> listaSector = sectorService.listarTodos();
		return listaSector;
	}
	

	
	@RequestMapping("/consultaPorID")
	@ResponseBody
	public Usuario consultaPorID(@RequestParam("id") Integer id){
		return servicioUsu.buscarPorID(id);
	}
	
	
	
	
	@RequestMapping("/eliminarPorID")
	public String eliminar(@RequestParam("codigo") Integer cod,RedirectAttributes redirect) {
		 
		 try {
	            servicioUsu.eliminar(cod);
	            redirect.addFlashAttribute("MENSAJE", "Usuario eliminado con éxito");
	        } catch (Exception e) {
	            e.printStackTrace();
	            redirect.addFlashAttribute("ERROR", "Error al eliminar el usuario");
	        }		
		 return "redirect:/prestamista/listarPrestatarios";
	}
	
	@RequestMapping("/listarSolicitudes")
	public String listarSolicitudes(Model model){
		Usuario usuejemplo =servicioUsu.buscarPorID((Integer) model.getAttribute("IDUSUARIO"));
		int idsectorejemplo = usuejemplo.getSector().getId();
		
		model.addAttribute("Solicitudes",servicioSol.listaSolicitudesPorPrestatariosdePrestamista(idsectorejemplo,usuejemplo.getId()));

		return "listarSolicitudes";
	}
	
	@Transactional
	@RequestMapping("/aprobarEstadoSolicitud")
	public String aprobarEstadoSolicitud(@RequestParam("codigo") Integer cod,
	        @RequestParam("montodiario") String monto,
	        @RequestParam("idusu") String idusu,
	        @RequestParam("fec1") String fec1Str,
	        @RequestParam("fec2") String fec2Str,
	        @RequestParam("idsoli") String idsoli,
	        RedirectAttributes redirect) {

	    // Convertir las cadenas de fecha a objetos LocalDate
	    LocalDate fec1 = LocalDate.parse(fec1Str);
	    LocalDate fec2 = LocalDate.parse(fec2Str);

	    long diferenciaDias = ChronoUnit.DAYS.between(fec1, fec2);

	    for (int i = 0; i < diferenciaDias; i++) {
	        LocalDate fechaPago = fec1.plusDays(i);
	        if (fechaPago.getDayOfWeek() != DayOfWeek.SATURDAY && fechaPago.getDayOfWeek() != DayOfWeek.SUNDAY) {
	            Pagos pagosin = new Pagos();
	            pagosin.setMontodiario(Double.parseDouble(monto));
	            pagosin.setEstado("Pendiente");
	            pagosin.setFecha(Date.from(fechaPago.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	            Solicitud soli = new Solicitud ();
	            soli.setId(Integer.parseInt(idsoli));
	            pagosin.setSolicitud(soli);
	            
	            Usuario usu = new Usuario();
	            usu.setId(Integer.parseInt(idusu));
	            pagosin.setUsuario(usu);

	            pagosservice.registrar(pagosin);
	        }
	    }
        servicioSol.aprobarSolicitud(cod);

	    return "redirect:/prestamista/listarSolicitudes";
	}
	
	@Transactional
	@RequestMapping("/desaprobarEstadoSolicitud")
	public String desaprobarEstadoSolicitud(@RequestParam("codigo") Integer cod,RedirectAttributes redirect) {
		 
		 
	            servicioSol.desaprobarSolicitud(cod);
	       	
		 return "redirect:/prestamista/listarSolicitudes";
	}
}
