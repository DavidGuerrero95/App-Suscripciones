package com.app.suscripciones.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.suscripciones.clients.EstadisticaFeignClient;
import com.app.suscripciones.clients.NotificacionesFeignClient;
import com.app.suscripciones.models.Suscripciones;
import com.app.suscripciones.repository.SuscripcionesRepository;

@RestController
public class SuscripcionesController {

	private final Logger logger = LoggerFactory.getLogger(SuscripcionesController.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	SuscripcionesRepository sRepository;

	@Autowired
	EstadisticaFeignClient eClient;

	@Autowired
	NotificacionesFeignClient nClient;

	@PostMapping("/suscripciones/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean crearSuscripciones(@RequestParam("nombre") String nombre) throws IOException {
		try {
			Suscripciones s = new Suscripciones();
			s.setNombre(nombre);
			s.setSuscripciones(new ArrayList<String>());
			s.setCuestionarios(new ArrayList<String>());
			s.setLike(new ArrayList<String>());
			s.setDislike(new ArrayList<String>());
			s.setComentarios(new ArrayList<List<String>>());
			sRepository.save(s);
			if (cbFactory.create("suscripciones").run(() -> nClient.crearSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			if (cbFactory.create("suscripciones").run(() -> eClient.crearSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			return true;
		} catch (Exception e2) {
			throw new IOException("Error crear, suscripciones: " + e2.getMessage());
		}

	}

	@GetMapping("/suscripciones/obtener/nombre/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Suscripciones obtenerSuscripcionesNombre(@PathVariable("nombre") String nombre) throws IOException {
		try {
			return sRepository.findByNombre(nombre);
		} catch (Exception e) {
			throw new IOException("error obtener suscripciones, suscripciones: " + e.getMessage());
		}
	}

	@GetMapping("/suscripciones/obtener/nombre/lista/suscritos/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<String> obtenerListaSuscripciones(@PathVariable("nombre") String nombre) throws InterruptedException {
		Suscripciones s = sRepository.findByNombre(nombre);
		return s.getSuscripciones();
	}

	@PutMapping("/suscripciones/inscripcion/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void inscripcionesProyecto(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username) {
		Suscripciones s = sRepository.findByNombre(nombre);
		if (!s.getSuscripciones().contains(username)) {
			List<String> subs = s.getSuscripciones();
			subs.add(username);
			s.setSuscripciones(subs);
			sRepository.save(s);
			if (cbFactory.create("suscripciones").run(() -> nClient.editarSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadistica(nombre),
					e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			if (cbFactory.create("suscripciones").run(() -> eClient.editarSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}

			nClient.enviarMensajeSuscripciones(nombre, username);
		}

	}

	@GetMapping("/suscripciones/inscripcion/verificar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean verificarInscripcion(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username) throws IOException {
		try {

			Suscripciones s = sRepository.findByNombre(nombre);
			return s.getSuscripciones().contains(username);
		} catch (Exception e) {
			throw new IOException("Error verificar inscripcion, suscripciones: " + e.getMessage());
		}
	}

	@PutMapping("/suscripciones/inscripcion/anular/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> anularInscripcionesProyecto(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username) {
		if (sRepository.existsByNombre(nombre)) {
			Suscripciones s = sRepository.findByNombre(nombre);
			if (s.getSuscripciones().contains(username)) {
				List<String> subs = s.getSuscripciones();
				subs.remove(username);
				s.setSuscripciones(subs);
				sRepository.save(s);
				if (cbFactory.create("suscripciones").run(() -> eClient.editarSuscripciones(s),
						e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}
				if (cbFactory.create("suscripciones").run(() -> nClient.editarSuscripciones(s),
						e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}
				if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadistica(nombre),
						e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}

				return ResponseEntity.ok("Eliminar suscripcion de proyecto: " + nombre + " de manera Exitosa!");
			} else {
				return ResponseEntity.badRequest()
						.body("El usuario: " + username + " no está registrado en este proyecto");
			}
		}
		return ResponseEntity.badRequest().body("Proyecto: " + nombre + " no existe");
	}

	@PutMapping("/suscripciones/comentarios/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> comentariosProyecto(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username, @RequestParam("comentario") String comentario) {
		if (sRepository.existsByNombre(nombre)) {
			Suscripciones s = sRepository.findByNombre(nombre);
			List<List<String>> listaComentarios = s.getComentarios();
			List<String> usuarios = new ArrayList<String>();
			Calendar c = Calendar.getInstance();

			String dia = Integer.toString(c.get(Calendar.DATE));
			String mes = Integer.toString(c.get(Calendar.MONTH));
			String annio = Integer.toString(c.get(Calendar.YEAR));
			String fecha = dia + "/" + mes + "/" + annio;

			Integer hora = c.get(Calendar.HOUR_OF_DAY);
			Integer minutos = c.get(Calendar.MINUTE);
			String tiempo = hora + ":" + minutos;
			usuarios.add(username);
			usuarios.add(comentario);
			usuarios.add(fecha);
			usuarios.add(tiempo);
			listaComentarios.add(usuarios);
			sRepository.save(s);
			if (cbFactory.create("suscripciones").run(() -> eClient.editarSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			if (cbFactory.create("suscripciones").run(() -> nClient.editarSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}
			if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadistica(nombre),
					e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}

			return ResponseEntity.ok("Comentario añadido al proyecto: " + nombre + " de forma Exitosa!");
		}
		return ResponseEntity.badRequest().body("Proyecto: " + nombre + " no existe");
	}

	@GetMapping("/suscripciones/comentarios/ver/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<List<String>> verComentarios(@PathVariable("nombre") String nombre) {
		if (sRepository.existsByNombre(nombre)) {
			Suscripciones s = sRepository.findByNombre(nombre);
			return s.getComentarios();
		}
		return null;
	}

	@PutMapping("/suscripciones/cuestionario/inscribir/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean inscribirCuestionario(@PathVariable String nombre, @RequestParam("usuario") String usuario)
			throws IOException {
		try {
			Suscripciones s = sRepository.findByNombre(nombre);
			List<String> listaCuestionarios = s.getCuestionarios();
			if (!listaCuestionarios.contains(usuario)) {
				listaCuestionarios.add(usuario);
				s.setCuestionarios(listaCuestionarios);
				sRepository.save(s);
				if (cbFactory.create("suscripciones").run(() -> eClient.editarSuscripciones(s),
						e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}
				if (cbFactory.create("suscripciones").run(() -> nClient.editarSuscripciones(s),
						e -> errorConexion(e))) {
					logger.info("Creacion Correcta");
				}
				nClient.enviarMensajeInscripciones(nombre, usuario);
				return true;
			}
			return false;
		} catch (Exception e2) {
			throw new IOException("Error inscribir cuestionario, suscripciones: " + e2.getMessage());
		}
	}

	@GetMapping("/suscripciones/cuestionario/verificar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean verificarCuestionario(@PathVariable("nombre") String nombre,
			@RequestParam("username") String username) {
		try {
			Suscripciones s = sRepository.findByNombre(nombre);
			return s.getCuestionarios().contains(username);
		} catch (Exception e) {
			return false;
		}
	}

	@PutMapping("/suscripciones/likes/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> likes(@PathVariable("nombre") String nombre, @RequestParam("username") String username,
			@RequestParam("likes") Integer likes) {
		if (sRepository.existsByNombre(nombre)) {
			Suscripciones s = sRepository.findByNombre(nombre);
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
			} else if (likes == 2) {
				if (listaLikes.contains(usuario))
					listaLikes.remove(usuario);
				if (!listaDislikes.contains(usuario)) {
					listaDislikes.add(usuario);
				}
			}
			s.setLike(listaLikes);
			s.setDislike(listaDislikes);
			sRepository.save(s);
			if (cbFactory.create("suscripciones").run(() -> nClient.editarSuscripciones(s), e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}

			if (cbFactory.create("suscripciones").run(() -> eClient.obtenerEstadistica(nombre),
					e -> errorConexion(e))) {
				logger.info("Creacion Correcta");
			}

			return ResponseEntity.ok("Añadido exitosamente");
		}
		return ResponseEntity.badRequest().body("Proyecto: " + nombre + " no existe");
	}

	@GetMapping("/suscripciones/likes/ver/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Integer revisarLikes(@PathVariable("nombre") String nombre, @RequestParam("username") String username) {
		Suscripciones s = sRepository.findByNombre(nombre);
		if (s.getLike().contains(username)) {
			return 1;
		} else if (s.getDislike().contains(username)) {
			return 2;
		} else {
			return 0;
		}
	}

	@DeleteMapping("/suscripciones/borrar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean borrarSuscripciones(@PathVariable("nombre") String nombre) throws IOException {
		try {
			Suscripciones s = sRepository.findByNombre(nombre);
			sRepository.delete(s);
			return true;
		} catch (Exception e) {
			throw new IOException("Error borrar, suscripciones: " + e.getMessage());
		}
	}

	public Boolean errorConexion(Throwable e) {
		logger.info(e.getMessage());
		return false;
	}

}
