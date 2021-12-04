package com.app.suscripciones.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.app.suscripciones.models.Suscripciones;

@FeignClient("app-estadistica")
public interface EstadisticaFeignClient {

	@PostMapping("/estadistica/suscripciones/crear/")
	public Boolean crearSuscripciones(@RequestBody Suscripciones pr);

	@PutMapping("/estadistica/suscripciones/editar/")
	public Boolean editarSuscripciones(@RequestBody Suscripciones pr);

	@PutMapping("/estadistica/obtenerEstadistica/{nombre}")
	public Boolean obtenerEstadistica(@PathVariable("nombre") String nombre);

}
