package com.tojaoomy.moon.backend.impl;

import com.tojaoomy.moon.annotation.Service;
import com.tojaoomy.moon.api.HelloService;

@Service( name = "helloService", value = HelloService.class)
public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		if("test".equals(name)){
			throw new RuntimeException("test exception");
		}
		return "Hello , " + name;
	}

}
