package com.app.suscripciones.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.suscripciones.models.Suscripciones;

public interface SuscripcionesRepository extends MongoRepository<Suscripciones, String> {

	@RestResource(path = "buscar-id")
	public Suscripciones findByIdProyecto(@Param("idProyecto") Integer idProyecto);

	@RestResource(path = "exists-id")
	public Boolean existsByIdProyecto(@Param("idProyecto") Integer idProyecto);

	@RestResource(path = "delete-id")
	public void deleteByIdProyecto(@Param("idProyecto") Integer idProyecto);

}