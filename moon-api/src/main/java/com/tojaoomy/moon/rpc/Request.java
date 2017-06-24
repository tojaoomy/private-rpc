package com.tojaoomy.moon.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Request {

	private String requestId;
	
	private String className;
	
	private String methodName;
	
	private Class<?>[] parameterTypes;
	
	private Object[] parameters;
}
