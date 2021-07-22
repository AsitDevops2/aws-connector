package com.aws.api.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.EC2MetadataUtils.IAMSecurityCredential;
import com.aws.api.exception.BadRequestException;
import com.aws.api.model.AWSConnectionRequest;

import lombok.extern.slf4j.Slf4j;
@Slf4j(topic="AWS_SERVICE_IMPL")
@Service
public class AWSApiServiceImpl implements AWSApiService {

	public String getConnectionFromEnvironmentVariableCredentialsProvider() {
		String connectionStatus = "success";
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
					.withCredentials(new EnvironmentVariableCredentialsProvider()).build();
			for (Bucket bucket : s3Client.listBuckets()) {
				System.out.println(" - " + bucket.getName());
			}
			;
		} catch (Exception error) {
			connectionStatus = error.getMessage();
			log.error(connectionStatus);
			throw new BadRequestException("Invalid credentials for AWS Connection");

		}

		return connectionStatus;
	}

	public AWSCredentials getConnectionFromBasicAWSCredentials(AWSConnectionRequest request) {
		
		AWSCredentials credentials = new BasicAWSCredentials(request.getAccess_key_id(),request.getSecret_access_key());
		
		return credentials;
		
	}
	public String getS3Connection(AWSConnectionRequest request) {
		String connectionStatus="success";
		try {
		AWSCredentials creds=getConnectionFromBasicAWSCredentials(request);
		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(creds);

		// create bucket - name must be unique for all S3 users
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		};
		}catch(Exception error) {
			connectionStatus = error.getMessage();
			log.error(connectionStatus);
			throw new BadRequestException("Invalid credentials for AWS Connection");
		}
		return connectionStatus;
	}
	public String getEC2Connection(AWSConnectionRequest request) {
		String connectionStatus="success";
		try {
		AWSCredentials creds=getConnectionFromBasicAWSCredentials(request);
		// create a client connection based on credentials
		AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(Regions.US_EAST_1)
                .build();

		// get Ec2 Instances
		boolean done = false;
		DescribeInstancesRequest descRequest = new DescribeInstancesRequest();
		while(!done) {
		    DescribeInstancesResult response = ec2Client.describeInstances(descRequest);

		    for(Reservation reservation : response.getReservations()) {
		        for(Instance instance : reservation.getInstances()) {
		            System.out.printf(
		                "Found instance with id %s, " +
		                "AMI %s, " +
		                "type %s, " +
		                "state %s " +
		                "and monitoring state %s",
		                instance.getInstanceId(),
		                instance.getImageId(),
		                instance.getInstanceType(),
		                instance.getState().getName(),
		                instance.getMonitoring().getState());
		        }
		    }

		    descRequest.setNextToken(response.getNextToken());

		    if(response.getNextToken() == null) {
		        done = true;
		    }
		}
		}catch(Exception error) {
			connectionStatus = error.getMessage();
			log.error(connectionStatus);
			throw new BadRequestException("Invalid credentials for AWS Connection");
		}
		return connectionStatus;
	}
	
}
