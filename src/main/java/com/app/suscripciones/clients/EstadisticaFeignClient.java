package com.app.suscripciones.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient("app-estadistica")
public interface EstadisticaFeignClient {

	@PutMapping("/estadisticas/{idProyecto}/")
	public Boolean obtenerEstadisticaProyecto(@PathVariable("idProyecto") Integer idProyecto);

}
