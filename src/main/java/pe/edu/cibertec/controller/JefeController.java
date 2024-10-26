package pe.edu.cibertec.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
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
import pe.edu.cibertec.entity.Rol;
import pe.edu.cibertec.entity.Sector;
import pe.edu.cibertec.entity.Usuario;
import pe.edu.cibertec.service.RolService;
import pe.edu.cibertec.service.SectorServiceImpl;
import pe.edu.cibertec.service.UsuarioService;

@Controller
@RequestMapping("/jefe")
@SessionAttributes({"ENLACES","DATOSDELUSUARIO","IDUSUARIO"})

public class JefeController {
	@Autowired
	private UsuarioService servicioUsu;
	
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
		model.addAttribute("ENLACES",enlaces);
		model.addAttribute("DATOSDELUSUARIO",usu.getNombre()+" "+ usu.getApellido());
		model.addAttribute("IDUSUARIO", usu.getId());
		return "principal";
	}
	
	@RequestMapping("/registrarPrestamista")
	public String registrarPrestamista(Model model){
		
		return "registrarPrestamista";
	}
	
	
	@RequestMapping("/grabarPrestamista")
	public ResponseEntity<String> grabarPrestamista(
						 @RequestParam("id") Integer id,
						 @Valid@RequestParam("nombre") String nom,
						 @Valid@RequestParam("apellido") String ape,
						 @Valid@RequestParam("email") String ema,
						 @Valid@RequestParam("telefono") String tel,
						 @Valid@RequestParam("username") String use,
						 @Valid@RequestParam("password") String pas,
						 @Valid@RequestParam("dni") String dni,
						 @Valid@RequestParam("sector") int sectorId,
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
				
				Sector sector = new Sector();
		        sector.setId(sectorId);
		        usu.setSector(sector);

				Rol r=new Rol();
				r.setCodigo(3);
				usu.setRol(r);
				if(id ==0) {
					usu.setPassword(var);
					servicioUsu.registrar(usu);
					redirect.addFlashAttribute("MENSAJE","Medicamento registrado");
				}
				else {
					usu.setId(id);
					usu.setPassword(pas);
					servicioUsu.actualizar(usu);
					redirect.addFlashAttribute("MENSAJE","Medicamento actualizado");
				}
				
				redirect.addFlashAttribute("MENSAJE","Prestamista registrado");
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("Usuario registrado exitosamente");
	}
	
	@RequestMapping("/actualizarPrestamista")
	public String actualizarPrestamista(
						 @RequestParam("id") Integer id,
						 @Valid@RequestParam("nombre") String nom,
						 @Valid@RequestParam("apellido") String ape,
						 @Valid@RequestParam("email") String ema,
						 @Valid@RequestParam("telefono") String tel,
						 @Valid@RequestParam("username") String use,
						 @Valid@RequestParam("password") String pas,
						 @Valid@RequestParam("dni") String dni,
						 @Valid@RequestParam("sector") int sectorId,
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
				
				Sector sector = new Sector();
		        sector.setId(sectorId);
		        usu.setSector(sector);

				Rol r=new Rol();
				r.setCodigo(3);
				usu.setRol(r);
				if(id ==0) {
					usu.setPassword(var);
					servicioUsu.registrar(usu);
					redirect.addFlashAttribute("MENSAJE","Medicamento registrado");
				}
				else {
					usu.setId(id);
					usu.setPassword(pas);
					servicioUsu.actualizar(usu);
					redirect.addFlashAttribute("MENSAJE","Medicamento actualizado");
				}
				
				redirect.addFlashAttribute("MENSAJE","Prestamista registrado");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/jefe/listarPrestamista";
	}

	
	@ModelAttribute("sectores")
	public List<Sector> getSector(Model model){
		
		Usuario usuejemplo =servicioUsu.buscarPorID((Integer) model.getAttribute("IDUSUARIO"));
		int idsectorejemplo = usuejemplo.getSector().getId();
		
		List<Sector> listaSector = sectorService.listaSectorParaPrestamista(idsectorejemplo);
		return listaSector;
	}
	
	@RequestMapping("/listarPrestamista")
	public String listarPrestamista(Model model){
		
		
		Usuario usuejemplo =servicioUsu.buscarPorID((Integer) model.getAttribute("IDUSUARIO"));
		int idsectorejemplo = usuejemplo.getSector().getId();
		
		model.addAttribute("Prestamista",servicioUsu.listarUsuarioporSectorGrande(idsectorejemplo));
		
		return "listarPrestamistas";
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
		 return "redirect:/jefe/listarPrestamista";
	}
	
	
}
