package com.app.suscripciones.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.suscripciones.models.Suscripciones;

@FeignClient(name = "app-notificaciones")
public interface NotificacionesFeignClient {

	@PostMapping("/notificaciones/suscripciones/crear/")
	public Boolean crearSuscripciones(@RequestBody Suscripciones s);

	@PutMapping("/notificaciones/suscripciones/editar/")
	public Boolean editarSuscripciones(@RequestBody Suscripciones s);

	@PutMapping("/notificaciones/suscripciones/")
	public void enviarMensajeSuscripciones(@RequestParam String nombre, @RequestParam String username);

	@PostMapping("/notificaciones/inscripciones/")
	public void enviarMensajeInscripciones(@RequestParam String nombre, @RequestParam String username);

}
