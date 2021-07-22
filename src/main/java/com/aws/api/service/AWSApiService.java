package com.aws.api.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.aws.api.model.AWSConnectionRequest;

public interface AWSApiService {
	
	AWSCredentials getConnectionFromBasicAWSCredentials(AWSConnectionRequest request);
	String getConnectionFromEnvironmentVariableCredentialsProvider();
	String getS3Connection(AWSConnectionRequest request);
	String getEC2Connection(AWSConnectionRequest request);
}
