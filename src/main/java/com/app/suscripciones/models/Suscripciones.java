package com.app.suscripciones.models;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "suscripciones")
@Data
@NoArgsConstructor
public class Suscripciones {

	@Id
	@JsonIgnore
	private String id;

	@NotNull(message = "Name cannot be null")
	@Indexed(unique = true)
	private Integer idProyecto;

	private List<String> suscripciones;
	private List<String> cuestionarios;
	private List<String> like;
	private List<String> dislike;

}
