package com.app.suscripciones.request;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comentarios")
public class Comentarios {

	@Id
	private String id;

	private String nombre;
	private String username;
	private Boolean anonimo;
	private String fecha;
	private String tiempo;
	private String comentario;

	public Comentarios() {
	}

	public Comentarios(String nombre, String username, Boolean anonimo, String fecha, String tiempo,
			String comentario) {
		super();
		this.nombre = nombre;
		this.username = username;
		this.anonimo = anonimo;
		this.fecha = fecha;
		this.tiempo = tiempo;
		this.comentario = comentario;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombreProyecto() {
		return nombre;
	}

	public void setNombreProyecto(String nombre) {
		this.nombre = nombre;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean isAnonimo() {
		return anonimo;
	}

	public void setAnonimo(Boolean anonimo) {
		this.anonimo = anonimo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTiempo() {
		return tiempo;
	}

	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

}
