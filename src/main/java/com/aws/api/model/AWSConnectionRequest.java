package com.aws.api.model;

import lombok.Data;

@Data
public class AWSConnectionRequest {
	private String access_key_id;
	private String secret_access_key;

}
