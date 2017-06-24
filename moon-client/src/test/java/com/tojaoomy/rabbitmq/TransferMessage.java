package com.tojaoomy.rabbitmq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Wither
public class TransferMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4583997087725330320L;

	private String msg;
	
	private java.util.Date Date;
}
