package com.app.suscripciones.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.suscripciones.models.Suscripciones;
import com.app.suscripciones.request.Comentarios;

@FeignClient("app-estadistica")
public interface EstadisticaFeignClient {

	@PostMapping("/estadistica/suscripciones/crear/")
	public Boolean crearSuscripciones(@RequestBody Suscripciones pr);

	@PutMapping("/estadistica/suscripciones/editar/")
	public Boolean editarSuscripciones(@RequestBody Suscripciones pr);
	
	@PutMapping("/estadistica/suscripciones/comentarios/editar/")
	public Boolean editarSuscripcionesComentarios(@RequestBody Comentarios pr);

	@PutMapping("/estadistica/obtenerEstadistica/{nombre}")
	public Boolean obtenerEstadistica(@PathVariable("nombre") String nombre);
	
	@DeleteMapping("/estadistica/suscripciones/comentarios/eliminar/")
	public Boolean eliminarComentarioId(@RequestParam String id);
	
	@DeleteMapping("/estadistica/suscripciones/comentarios/eliminar/todos/")
	public Boolean eliminarAllComentario(@RequestParam String nombre);
	
}
