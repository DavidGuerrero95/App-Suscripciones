package com.app.suscripciones.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Likes {

	private Integer likes;
	private Integer disLikes;
	private Integer userLike;

}
