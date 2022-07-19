package com.app.suscripciones.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.suscripciones.clients.EstadisticaFeignClient;
import com.app.suscripciones.clients.NotificacionesFeignClient;
import com.app.suscripciones.models.Comentarios;
import com.app.suscripciones.models.Suscripciones;
import com.app.suscripciones.repository.ComentariosRepository;
import com.app.suscripciones.repository.SuscripcionesRepository;
import com.app.suscripciones.responses.Likes;

@RestController
public class SuscripcionesController {

	private final Logger log = LoggerFactory.getLogger(SuscripcionesController.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	SuscripcionesRepository sRepository;

	@Autowired
	EstadisticaFeignClient eClient;

	@Autowired
	NotificacionesFeignClient nClient;

	@Autowired
	ComentariosRepository cRepository;

//  ****************************	SUSCRIPCIONES 	***********************************  //

	// MICROSERVICIOS PROYECTOS -> CREAR SUSCRIPCIONES
	@PostMapping("/suscripciones/crear/")
	public Boolean crearSuscripciones(@RequestParam("idProyecto") Integer idProyecto) throws IOException {
		try {
			Suscripciones s = new Suscripciones();
			s.setIdProyecto(idProyecto);
			s.setSuscripciones(new ArrayList<String>());
			s.setCuestionarios(new ArrayList<String>());
			s.setLike(new ArrayList<String>());
			s.setDislike(new ArrayList<String>());
			sRepository.save(s);
			return true;
		} catch (Exception e2) {
			throw new IOException("Error crear, suscripciones: " + e2.getMessage());
		}

	}

	// INSCRIBIRSE A UN PROYECTO
	@PutMapping("/suscripciones/inscripcion/verificar/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean inscripcionesProyecto(@PathVariable("idProyecto") Integer idProyecto,
			@RequestParam("username") String username,
			@RequestParam(value = "formulario", defaultValue = "1") Integer formulario) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Suscripciones s = sRepository.findByIdProyecto(idProyecto);
			if (!s.getSuscripciones().contains(username)) {
				List<String> subs = s.getSuscripciones();
				subs.add(username);
				s.setSuscripciones(subs);
				sRepository.save(s);

				if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadisticaProyecto(idProyecto),
						e -> errorConexion(e))) {
					log.info("Creacion Correcta");
				}
			}
			nClient.enviarMensajeSuscripciones(idProyecto, username);
			return true;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El proyecto no existe");
	}

	// ANULAR INSCRIPCION DE UN PROYECTO
	@PutMapping("/suscripciones/inscripcion/anular/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean anularInscripcionesProyecto(@PathVariable("idProyecto") Integer idProyecto,
			@RequestParam("username") String username,
			@RequestParam(value = "formulario", defaultValue = "1") Integer formulario) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Suscripciones s = sRepository.findByIdProyecto(idProyecto);
			if (s.getSuscripciones().contains(username)) {
				List<String> subs = s.getSuscripciones();
				subs.remove(username);
				s.setSuscripciones(subs);
				sRepository.save(s);

				if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadisticaProyecto(idProyecto),
						e -> errorConexion(e))) {
					log.info("Obtencion Estadistica correcta");
				}
				return true;
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"El usuario: " + username + " no está registrado en este proyecto");
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

	// MICROSERVICIO ESTADISTICAS -> OBTENER SUSCRIPCIONES
	// MICROSERVICIO ESTADISTICAS DASHBOARD -> OBTENER SUSCRIPCIONES
	@GetMapping("/suscripciones/{idProyecto}/")
	public Suscripciones obtenerSuscripcionesNombre(@PathVariable("idProyecto") Integer idProyecto) throws IOException {
		try {
			return sRepository.findByIdProyecto(idProyecto);
		} catch (Exception e) {
			throw new IOException("error obtener suscripciones, suscripciones: " + e.getMessage());
		}
	}

	// MICROSERVICIO ESTADISTICAS -> OBTENER COMENTARIOS
	// MICROSERVICIO ESTADISTICAS DASHBOARD -> OBTENER COMENTARIOS
	@GetMapping("/suscripciones/proyecto/comentarios/{idProyecto}/")
	public Integer obtenerComentariosNombre(@PathVariable("idProyecto") Integer idProyecto) throws IOException {
		try {
			return cRepository.findByIdProyecto(idProyecto).size();
		} catch (Exception e) {
			throw new IOException("error obtener comentarios, suscripciones: " + e.getMessage());
		}
	}

	// MICROSERVICIO NOTIFICACIONES -> OBTENER LISTA DE USUARIOS SUSCRITOS
	@GetMapping("/suscripciones/obtener/nombre/lista/suscritos/{idProyecto}")
	public List<String> obtenerListaSuscripciones(@PathVariable("idProyecto") Integer idProyecto)
			throws InterruptedException {
		Suscripciones s = sRepository.findByIdProyecto(idProyecto);
		return s.getSuscripciones();
	}

	// VERIFICAR INSCRIPCION
	@GetMapping("/suscripciones/inscripcion/verificar/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean verificarInscripcion(@PathVariable("idProyecto") Integer idProyecto,
			@RequestParam("username") String username) throws IOException {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Suscripciones s = sRepository.findByIdProyecto(idProyecto);
			return s.getSuscripciones().contains(username);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El proyecto no existe");
	}

	// ELIMINAR SUSCRIPCIONES DE PROYECTO
	@DeleteMapping("/suscripciones/borrar/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean borrarSuscripciones(@PathVariable("idProyecto") Integer idProyecto) throws IOException {
		try {
			Suscripciones s = sRepository.findByIdProyecto(idProyecto);
			eliminarTodosComentarios(idProyecto);
			sRepository.delete(s);
			return true;
		} catch (Exception e) {
			throw new IOException("Error borrar, suscripciones: " + e.getMessage());
		}
	}

//  ****************************	COMENTARIOS 	***********************************  //

	// CREAR COMENTARIOS
	@PostMapping("/suscripciones/crear/comentarios/{idProyecto}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean crearComentario(@PathVariable("idProyecto") Integer idProyecto,
			@RequestBody @Validated Comentarios coment) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Date n = new Date();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat formatter2 = new SimpleDateFormat("hh:mm:ss");
			coment.setFecha(formatter.format(n));
			coment.setTiempo(formatter2.format(n));
			cRepository.save(coment);
			if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadisticaProyecto(idProyecto),
					e -> errorConexion(e))) {
				log.info("Obtencion Estadistica correcta");
			}
			return true;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

	// EDITAR COMENTARIOS
	@PutMapping("/suscripciones/editar/comentarios/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean editarComentario(@PathVariable("idProyecto") Integer idProyecto,
			@RequestBody @Validated Comentarios comentario) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Optional<Comentarios> c = cRepository.findById(comentario.getId());
			if (c.isPresent()) {
				Comentarios co = c.get();
				co.setComentario(comentario.getComentario());
				co.setUsername(comentario.getUsername());
				co.setIdProyecto(comentario.getIdProyecto());
				co.setAnonimo(comentario.getAnonimo());
				cRepository.save(co);
				return true;
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id no existe");
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

	// VER LISTA COMENTARIOS
	@GetMapping("/suscripciones/ver/comentarios/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Comentarios> verTodosComentarios(@PathVariable("idProyecto") Integer idProyecto) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			if (cRepository.findByIdProyecto(idProyecto) != null) {
				List<Comentarios> listaComentarios = cRepository.findByIdProyecto(idProyecto);
				return listaComentarios;
			}
		}
		return new ArrayList<Comentarios>();
	}

	@DeleteMapping("/suscripciones/eliminar/comentarios/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarComentario(@PathVariable("idProyecto") Integer idProyecto, @RequestParam("id") String id) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			cRepository.deleteById(id);
			return true;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

	// ELIMINAR TODOS LOS COMENTARIOS
	@DeleteMapping("/suscripciones/eliminar/todos/comentarios/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarTodosComentarios(@PathVariable("idProyecto") Integer idProyecto) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			cRepository.deleteAllByIdProyecto(idProyecto);
			return true;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

//  ****************************	LIKES 	***********************************  //

	// AGREGAR O REMOVER LIKES O DISLIKES
	@PutMapping("/suscripciones/likes/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> likes(@PathVariable("idProyecto") Integer idProyecto,
			@RequestParam("username") String username, @RequestParam("likes") Integer likes) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Suscripciones s = sRepository.findByIdProyecto(idProyecto);
			List<String> listaLikes = s.getLike();
			List<String> listaDislikes = s.getDislike();
			String usuario = username;
			if (likes == 0) {
				if (listaDislikes.contains(usuario))
					listaDislikes.remove(usuario);
				if (listaLikes.contains(usuario))
					listaLikes.remove(usuario);
			} else if (likes == 1) {
				if (listaDislikes.contains(usuario))
					listaDislikes.remove(usuario);
				if (!listaLikes.contains(usuario)) {
					listaLikes.add(usuario);
				}
			} else if (likes == -1) {
				if (listaLikes.contains(usuario))
					listaLikes.remove(usuario);
				if (!listaDislikes.contains(usuario)) {
					listaDislikes.add(usuario);
				}
			}
			s.setLike(listaLikes);
			s.setDislike(listaDislikes);
			sRepository.save(s);

			if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadisticaProyecto(idProyecto),
					e -> errorConexion(e))) {
				log.info("Creacion Correcta");
			}

			return ResponseEntity.ok("Añadido exitosamente");
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

	// VER LIKES
	@GetMapping("/suscripciones/likes/ver/{idProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Likes revisarLikes(@PathVariable("idProyecto") Integer idProyecto,
			@RequestParam("username") String username) {
		if (sRepository.existsByIdProyecto(idProyecto)) {
			Suscripciones s = sRepository.findByIdProyecto(idProyecto);
			Likes l = new Likes();
			l.setLikes(s.getLike().size());
			l.setDisLikes(s.getDislike().size());
			if (s.getLike().contains(username))
				l.setUserLike(1);
			else if (s.getDislike().contains(username))
				l.setUserLike(-1);
			else
				l.setUserLike(0);
			return l;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El Proyecto no existe");
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	public Boolean errorConexion(Throwable e) {
		log.info(e.getMessage());
		return false;
	}

}
