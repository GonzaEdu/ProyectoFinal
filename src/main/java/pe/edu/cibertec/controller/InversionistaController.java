package pe.edu.cibertec.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import pe.edu.cibertec.entity.Enlace;
import pe.edu.cibertec.entity.Rol;
import pe.edu.cibertec.entity.Sector;
import pe.edu.cibertec.entity.Usuario;
import pe.edu.cibertec.service.RolService;
import pe.edu.cibertec.service.UsuarioService;

@Controller
@RequestMapping("/inversionista")
@SessionAttributes({"ENLACES","DATOSDELUSUARIO","IDUSUARIO"})

public class InversionistaController {
	@Autowired
	private UsuarioService servicioUsu;
	
	@Autowired
	private RolService servicioRol;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
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
	
	//METODO PARA QUE EL EL INVERSIONISTA REGISTRE AL JEFE DE PRESTAMISTAS
		@RequestMapping("/registrarJefe")
		public String registrarJefe(Model model){
			
			return "registrarJefe";
		}
		
		@RequestMapping("/grabarJefe")
		public String grabarJefe(
				 @RequestParam("id") Integer id,
				 @RequestParam("nombre") String nom,
				 @RequestParam("apellido") String ape,
				 @RequestParam("email") String ema,
				 @RequestParam("telefono") String tel,
				 @RequestParam("username") String use,
				 @RequestParam("password") String pas,
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
							
						
							Rol r=new Rol();
							r.setCodigo(2);
							usu.setRol(r);

							Sector sector=new Sector();
							sector.setId(1);
							usu.setSector(sector);

							if(id ==0) {
								usu.setPassword(var);
								servicioUsu.registrar(usu);
								redirect.addFlashAttribute("MENSAJE","Jefe de Prestamista registrado");
							}
							else {
								usu.setId(id);
								usu.setPassword(pas);
								servicioUsu.actualizar(usu);
								redirect.addFlashAttribute("MENSAJE","Jefe Prestamista actualizado");
							}
							
							redirect.addFlashAttribute("MENSAJE","Prestamista registrado");
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						return "redirect:/inversionista/listarJefe";
		}
		
		@RequestMapping("/listarJefe")
		public String listarSolicitudes(Model model){
			model.addAttribute("Jefes",servicioUsu.listarUsuarioporRol(2));
			
			return "listarJefes";
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
			 return "redirect:/sesion/principal";
		}
	
	
	
	
	
	
}
