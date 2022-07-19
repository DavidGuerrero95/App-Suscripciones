package com.app.suscripciones.models;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "comentarios")
@Data
@NoArgsConstructor
public class Comentarios {

	@Id
	private String id;

	@NotNull(message = "id proyecto cannot be null")
	@Indexed(unique = false)
	private Integer idProyecto;

	@NotNull(message = "username proyecto cannot be null")
	private String username;

	@NotNull(message = "anonimo proyecto cannot be null")
	private Boolean anonimo;

	private String fecha;

	private String tiempo;

	@NotNull(message = "comentario proyecto cannot be null")
	private String comentario;

}
