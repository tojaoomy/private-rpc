package com.tojaoomy.moon.bootstrap;

import java.util.Scanner;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSON;
import com.tojaoomy.moon.backend.contant.PropertyConfig;

/**
 * Hello world!
 *
 */
public class Application 
{
    public static void main( String[] args )
    {
    	ClassPathResource resource = new ClassPathResource("application-context.xml");
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(resource.getPath());
        System.out.println(applicationContext.getBeanDefinitionCount());
        System.out.println(JSON.toJSONString(applicationContext.getBean(PropertyConfig.class)));
        applicationContext.close();
        Scanner scanner = new Scanner(System.in);
        String nextLine = "";
		while(!"exit".equals(nextLine = scanner.nextLine())){
        	System.out.println("read content : " + nextLine);
        };
        scanner.close();
    }
}
