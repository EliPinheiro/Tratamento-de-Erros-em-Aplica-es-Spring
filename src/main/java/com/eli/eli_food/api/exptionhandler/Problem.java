package com.eli.eli_food.api.exptionhandler;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter	
@Setter
@Builder
@JsonInclude(Include.NON_NULL) // So vai incluir na representação do erro os campos que não estiverem nulo
public class Problem {
	
	private Integer status;
	private String type;
	private String title;
	private String detail;
	
	
	private String userMessage;
	private LocalDateTime timastamp;


}
