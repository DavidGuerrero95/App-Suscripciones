package com.app.suscripciones.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificacionesFeignClient {

	@PutMapping("/notificaciones/suscripciones/")
	public void enviarMensajeSuscripciones(@RequestParam Integer idProyecto, @RequestParam String username);

	@PutMapping("/notificaciones/inscripciones/")
	public void enviarMensajeInscripciones(@RequestParam("idProyecto") Integer idProyecto, @RequestParam String nombre,
			@RequestParam String username);
}
