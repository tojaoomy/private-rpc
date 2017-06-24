package com.tojaoomy.moon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tojaoomy.moon.api.HelloService;
import com.tojaoomy.moon.discovery.ServiceFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class HelloServiceTest {

	@Autowired
	private ServiceFactory facotry;

	@Test
	public void helloTest() {
		HelloService helloService = facotry.create(HelloService.class);
		String result = helloService.hello("test");
		System.out.println("helloService.hello() result : " + result);
	}
}