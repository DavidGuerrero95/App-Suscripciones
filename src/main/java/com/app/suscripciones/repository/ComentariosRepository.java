package com.app.suscripciones.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.suscripciones.models.Comentarios;

public interface ComentariosRepository extends MongoRepository<Comentarios, String> {

	@RestResource(path = "find-coment-name")
	public List<Comentarios> findByIdProyecto(@Param("idProyecto") Integer idProyecto);

	@RestResource(path = "find-coment-name-username")
	public List<Comentarios> findByIdProyectoAndUsername(@Param("idProyecto") Integer idProyecto,
			@Param("username") String username);

	@RestResource(path = "exist-coment-name")
	public Boolean existsByIdProyecto(@Param("idProyecto") Integer idProyecto);

	@RestResource(path = "delet-all-coment")
	public void deleteAllByIdProyecto(@Param("idProyecto") Integer idProyecto);

}
