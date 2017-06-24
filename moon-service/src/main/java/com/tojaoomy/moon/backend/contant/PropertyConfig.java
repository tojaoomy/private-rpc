package com.tojaoomy.moon.backend.contant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class PropertyConfig {

	@Value("${moon.registry.server}")
	private String registryServer;
	
	@Value("${moon.server.address}")
	private String serverAddress;
}
