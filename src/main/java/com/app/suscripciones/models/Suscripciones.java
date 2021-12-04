package com.app.suscripciones.models;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "suscripciones")
public class Suscripciones {

	@Id
	private String id;

	@NotBlank(message = "Name cannot be null")
	@Size(max = 50)
	@Indexed(unique = true)
	private String nombre;

	private List<String> suscripciones;
	private List<String> cuestionarios;
	private List<String> like;
	private List<String> dislike;
	private List<List<String>> comentarios;

	public Suscripciones() {
	}

	public Suscripciones(String nombre, List<String> suscripciones, List<String> cuestionarios, List<String> like,
			List<String> dislike, List<List<String>> comentarios) {
		super();
		this.nombre = nombre;
		this.suscripciones = suscripciones;
		this.cuestionarios = cuestionarios;
		this.like = like;
		this.dislike = dislike;
		this.comentarios = comentarios;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<String> getSuscripciones() {
		return suscripciones;
	}

	public void setSuscripciones(List<String> subscripciones) {
		this.suscripciones = subscripciones;
	}

	public List<String> getCuestionarios() {
		return cuestionarios;
	}

	public void setCuestionarios(List<String> cuestionarios) {
		this.cuestionarios = cuestionarios;
	}

	public List<String> getLike() {
		return like;
	}

	public void setLike(List<String> like) {
		this.like = like;
	}

	public List<String> getDislike() {
		return dislike;
	}

	public void setDislike(List<String> dislike) {
		this.dislike = dislike;
	}

	public List<List<String>> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<List<String>> comentarios) {
		this.comentarios = comentarios;
	}

}
