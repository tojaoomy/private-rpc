package com.tojaoomy.moon.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Response {
	
	private String requestId;
	
	private Throwable error;
	
	private Object result;
	
	public boolean isSuccess(){
		return error == null;
	}
	
	public boolean isFail(){
		return !isSuccess();
	}
}
