package com.app.suscripciones.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.suscripciones.request.Comentarios;

public interface ComentariosRepository extends MongoRepository<Comentarios, String>{

	@RestResource(path = "find-coment-name")
	public List<Comentarios> findByNombre(@Param("nombre") String nombre);
	
	@RestResource(path = "find-coment-name-username")
	public List<Comentarios> findByNombreAndUsername(@Param("nombre") String nombre,
			@Param("username") String username);
	
	@RestResource(path = "exist-coment-name")
	public Boolean existsByNombre(@Param("nombre") String nombre);
	
	@RestResource(path = "delet-all-coment")
	public void deleteAllByNombre(@Param("nombre") String nombre);
	
}
