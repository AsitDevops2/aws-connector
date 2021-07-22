package com.aws.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.aws.api.model.AWSConnectionRequest;
import com.aws.api.model.Response;
import com.aws.api.service.AWSApiService;

import lombok.extern.slf4j.Slf4j;
/**
 * @author sirisha.annamneedi
 *
 */
@Slf4j(topic = "AWS_CONNECTION_CONTROLLER")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/aws")
public class AWSConnectController {
	@Autowired
	private AWSApiService service;
	
	@PostMapping("/getConnectionFromBasicAWSCredentials")
	public Response connectionCheck(@RequestBody AWSConnectionRequest request){
		AWSCredentials creds = service.getConnectionFromBasicAWSCredentials(request);
		if(creds!=null) {
			log.info("Connected Successfully");
			return new Response(HttpStatus.OK.value(),"OK" , "Connected Successfully");
			
		}
			log.error("Failed to connect.Please check Credentials");
			return new Response(HttpStatus.BAD_REQUEST.value(),"Failed", "Connection Failed");
	
		
	}
	
	@PostMapping("/getConnectionFromEnvironmentVariableCredentialsProvider")
	public Response connectionCheckEnvCreds(){
		String status = service.getConnectionFromEnvironmentVariableCredentialsProvider();
		if(status.equals("success")) {
			log.info("Connected Successfully");
			return new Response(HttpStatus.OK.value(),"OK" , "Connected Successfully");
			
		}
			log.error("Failed to connect.Please check Credentials");
			return new Response(HttpStatus.BAD_REQUEST.value(),status, "Connection Failed");
	}
	
	@PostMapping("/getS3Connection")
	public Response getS3Connection(@RequestBody AWSConnectionRequest request){
		String status = service.getS3Connection(request);
		if(status.equals("success")) {
			log.info("Connected Successfully");
			return new Response(HttpStatus.OK.value(),"OK" , "Connected Successfully");
			
		}
			log.error("Failed to connect.Please check Credentials");
			return new Response(HttpStatus.BAD_REQUEST.value(),status, "Connection Failed");	
	}
	
	@PostMapping("/getEC2Connection")
	public Response getEC2Connection(@RequestBody AWSConnectionRequest request){
		String status = service.getEC2Connection(request);
		if(status.equals("success")) {
			log.info("Connected Successfully");
			return new Response(HttpStatus.OK.value(),"OK" , "Connected Successfully");
			
		}
			log.error("Failed to connect.Please check Credentials");
			return new Response(HttpStatus.BAD_REQUEST.value(),status, "Connection Failed");	
	}
}
