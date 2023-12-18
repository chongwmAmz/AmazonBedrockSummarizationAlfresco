package chongwm.demo.aws.community.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class S3Utils
{
	String bucketName;
	String keyPath;
	
	public S3Utils(String s3Uri)
	{
		this.bucketName = s3Uri.substring(5, s3Uri.indexOf("/", 5));		
		this.keyPath = s3Uri.substring(s3Uri.indexOf(bucketName)+bucketName.length()+1, s3Uri.length());	
	}
	
	public String getBucketName()
	{
		return bucketName;
	}
	
	public String getKeyPath()
	{
		return keyPath;
	}
	
	
	protected void putFileIntoS3(S3Client s3Client, S3Presigner s3Presigner)
	{
		String bucketName = "your-bucket-name";
		String keyName = "your-object-name";
		String uploadFilePath = "path/to/your/file.txt"; // Replace with your file path

		// Upload file to S3
		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();
		s3Client.putObject(putObjectRequest, Paths.get(uploadFilePath));

		// Get a presigned URL to access the object
		GetObjectPresignRequest objectPresignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(10))
															.getObjectRequest(GetObjectRequest.builder()
															.bucket(bucketName).key(keyName).build())
															.build();

	     // Generate the presigned request
	     PresignedGetObjectRequest presignedGetObjectRequest =s3Presigner.presignGetObject(objectPresignRequest);
	     
	     if (presignedGetObjectRequest.isBrowserExecutable())
	         System.out.println("The pre-signed request can be executed using a web browser by " +
	                            "visiting the following URL: " + presignedGetObjectRequest.url());
	     else
	         System.out.println("The pre-signed request has an HTTP method, headers or a payload " +
	                            "that prohibits it from being executed by a web browser. See the S3Presigner " +
	                            "class-level documentation for an example of how to execute this pre-signed " +
	                            "request from Java code.");
		
	}
	
	protected void deleteFileFromS3(S3Client s3Client, String fileName)
	{
		// Delete file to S3
		String s3KeyName = this.keyPath+fileName;
		DeleteObjectRequest delS3File = DeleteObjectRequest.builder().bucket(this.bucketName).key(s3KeyName).build();
		s3Client.deleteObject(delS3File);
	}
	
	
	public void putJsonIntoS3(S3Client s3Client, String key, org.json.JSONObject jsonObject, boolean lines)
	{
		String contentType = lines?"application/jsonl":"application/json"; // As listed as convention in https://jsonlines.org/
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)  
                .key(key) 
                .contentType(contentType)
                .build();
        PutObjectResponse put= s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonObject.toString()));
	}
	
	//application/jsonlines
	

	public URL putInputStreamIntoS3(S3Client s3Client, S3Presigner s3Presigner, String fileName, InputStream contentStream) throws S3Exception, AwsServiceException, SdkClientException, IOException
	{
		// Upload file to S3
		String s3KeyName = this.keyPath+fileName;
		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(this.bucketName).key(s3KeyName).build();		 
		
		s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(contentStream, contentStream.available()));

		// Get a presigned URL to access the object
		GetObjectPresignRequest objectPresignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(10))
															.getObjectRequest(GetObjectRequest.builder()
															.bucket(bucketName).key(s3KeyName).build())
															.build();

	     // Generate the presigned request
	     PresignedGetObjectRequest presignedGetObjectRequest =s3Presigner.presignGetObject(objectPresignRequest);
	     
	     /*
	     if (presignedGetObjectRequest.isBrowserExecutable())
	         System.out.println("The pre-signed request can be executed using a web browser by " +
	                            "visiting the following URL: " + presignedGetObjectRequest.url());
	     else
	         System.out.println("The pre-signed request has an HTTP method, headers or a payload " +
	                            "that prohibits it from being executed by a web browser. See the S3Presigner " +
	                            "class-level documentation for an example of how to execute this pre-signed " +
	                            "request from Java code.");
	                            */
	     return presignedGetObjectRequest.url();
	}

	
	
	public static void main(String[] args)
	{
		
		S3Utils sss = new S3Utils("kendra-alf");
		
		
		

		// Create a random key name
	     
	     
		// Create a thread to upload and delete the object within an hour
		/*
	     Thread uploadAndDeleteThread = new Thread(() ->
		{
			try
			{
				// Upload the object using the presigned URL
				s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(keyName).build(), presignedUrl);

				// Delete the object after 1 hour ( 3600 seconds )
				s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build());

				System.out.println("Object uploaded and deleted successfully.");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		});

		// Start the thread
		uploadAndDeleteThread.start();
		*/
	}
}
/*
 * ```
 * 
 * In this example, we use the `software.amazon.awssdk.services.s3` package to interact with Amazon S3. Here's a breakdown of the code: 1. We import the necessary packages and create an S3 client using the `S3.builder()` method. 2. We define a random bucket name and object key, or you can replace these with your own.
 * 3. We upload the file to the S3 bucket using the `putObject` method. 4. We get a presigned URL for the uploaded object using the `getPresignedUrl` method. The presigned URL allows us to access the object for upload. 5. We create a thread that uploads the object using the presigned URL and deletes the object after 1
 * hour. The `Executors.newSingleThread()` creates a single-threaded executor, and the `run` method runs the upload and delete operation within a synchronized block. 6. Finally, we print a success message when the object is uploaded and deleted successfully.
 * 
 * Please note that you need to replace the placeholders (such as `"your-bucket-name"`, `"your-object-name"`, `"path/to/your/file.txt"`, and so on) with your actual S3 bucket and object details. Additionally, ensure that you have the necessary permissions and configurations set up to perform these operations on your S3
 * bucket.
 * 
 * 
 */