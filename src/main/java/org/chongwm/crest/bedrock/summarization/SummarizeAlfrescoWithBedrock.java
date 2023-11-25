package org.chongwm.crest.bedrock.summarization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.chongwm.hyland.alfresco.search.pojo.json2kt.Content;
import org.chongwm.hyland.alfresco.search.pojo.json2kt.Entries;
import org.chongwm.hyland.alfresco.search.pojo.json2kt.Entry;
import org.chongwm.hyland.alfresco.search.pojo.json2kt.Properties;
import org.chongwm.hyland.alfresco.search.pojo.json2kt.SearchResults;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * This class uses Amazon Bedrock to provide summaries of documents in an Alfresco Content Services repository. Foundation model prompt, temperature and token length for summarization can be provided for each document. Amazon Bedrock must be available in the same region as this
 * Lambda function.
 * 
 * Queries an Alfresco Content Services repository for documents with crestBedrock:generateSummary property set true. See AAAAA for the GenAi aspect that provides the property. After summarization is completed, the crestBedrock:generateSummary is reset to false. The AI generated
 * summary is stored in crestBedrock:summary. The foundation model used for summary is specified in crestBedrock:fm. (Currently only Anthropic Claude v2 is supported).
 */
public class SummarizeAlfrescoWithBedrock implements RequestHandler<Map<String, Object>, Integer>
{

	protected static String awsSessionToken = System.getenv("AWS_SESSION_TOKEN");
	protected static String awsSecretsExtensionHTTPPort = System.getenv("PARAMETERS_SECRETS_EXTENSION_HTTP_PORT");
	protected static String url = System.getenv("alfrescoHost");
	protected static String userId= System.getenv("alfrescoSA"); //ignored if Lambda environment variable awsSecretsManagerSecretArn is populated
	protected static String password=System.getenv("alfrescoPass"); //ignored if Lambda environment variable awsSecretsManagerSecretArn is populated
	protected static Arn awsSecretsManagerSecretArn = Arn.fromString(System.getenv("awsSecretsManagerSecretArn"));
	protected static String s3BucketNamePath = System.getenv("s3Uri");
	protected static String queryJson = System.getenv("queryJson");
	protected static boolean httpProtocol = ("https".compareToIgnoreCase(System.getenv("alfrescoHostProtocol")) == 0) ? true : false;
	protected static S3Client s3Client = S3Client.create();
	protected static S3Presigner s3Presigner = S3Presigner.create();
	protected static BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.create();
	protected static SimpleDateFormat alfrescoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");;
	private String ticket;
	protected String encodedTicket;
	protected CloseableHttpClient httpClient = null;
	protected HttpPost searchHttpPost = null;
	protected HttpPut nodeUpdateHttpPut = null;
	protected S3Utils s3Utils;
	private LambdaLogger logger;

	/****************************
	 * Constructor
	 * 
	 * @param url
	 *            The host/domain name of the where the Alfresco Content Services repository service can be found. Do not provide the protocol nor trailing paths. eg. alfservice.amazonaws.com
	 * @param https
	 *            True if the Alfresco service should be accessed with HTTPS, false otherwise.
	 * @param userId
	 *            Username of privileged Alfresco service account that has the permission query for and update all nodes to be summarized.
	 * @param password
	 *            Password of aforementioned privileged Alfresco service account.
	 * @param s3BucketNamePath
	 *            S3Uri to use to as transient storage to stage content files. The S3Uri must end with a slash. The region of the bucket has to be the same as that of this Lambda function. eg. s3://alftransient/BedrockTempFolder/
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws InterruptedException
	 * @throws Exception
	 */

	// public SummarizeAlfrescoWithBedrock(String s3BucketNamePath) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	public SummarizeAlfrescoWithBedrock() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, InterruptedException
	{
		this.s3Utils = new S3Utils(s3BucketNamePath);
		// initClass(url, userId, password);
	}

	private void getAwsSecretAndAlfrescoTicket() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException, InterruptedException
	{
		if ((this.awsSecretsManagerSecretArn != null))
		{
			getSecretValueFromLambdaLayer(this.awsSecretsManagerSecretArn.toString()); // this function populates userId and password
		}
		//else this.userId and this.password would be populated statically when the this class is created  before lambda handler is called.
			
		this.ticket = this.getAlfrescoTicket();
		this.encodedTicket = Base64.getEncoder().encodeToString(this.ticket.getBytes());
	}

	public static String getSecretValueFromLambdaLayer(String secretToGet)
	{
		//		System.out.println("Token length is " + System.getenv("AWS_SESSION_TOKEN").length() + " Port is " + System.getenv("PARAMETERS_SECRETS_EXTENSION_HTTP_PORT"));
		HttpClient httpClient = HttpClients.createDefault();

		// Create an HTTP GET request with the specified endpoint and request header
		HttpGet httpGet = new HttpGet("http://localhost:" + awsSecretsExtensionHTTPPort + "/secretsmanager/get?secretId=" + secretToGet);
		httpGet.setHeader("X-Aws-Parameters-Secrets-Token", awsSessionToken);
		//System.out.println("Endpoint is " + httpGet.toString()+" X-Aws-Parameters-Secrets-Token is " + awsSessionToken);

		// Send the HTTP GET request
		HttpResponse response = null;
		// Process the response (e.g., parse the JSON response)
		try
		{
			response = httpClient.execute(httpGet);
			String responseBody = EntityUtils.toString(response.getEntity());
			if (responseBody.startsWith("{"))
			{
				//System.out.println("JSON response Body: " + responseBody);
				Gson gson = new Gson();
		        JsonObject secretStringJson = gson.fromJson(gson.fromJson(responseBody, JsonObject.class).get("SecretString").getAsString(), JsonObject.class);

				for (String key : secretStringJson.keySet())
				{
					password = secretStringJson.get(key).getAsString();
					userId = key;
				}
			}
			else
				System.out.println("No JSON, response Body: " + responseBody);
		} catch (Exception e)
		{
			System.out.println("Error processing response: " + e.getMessage());
		} finally
		{
			httpClient = null;
		}
		return password;
	}

	public Integer handleRequest(Map<String, Object> event, Context context)
	{
		this.logger = context.getLogger();
		String logStr = "Lambda function triggered with alfrescoHostProtocol=" + httpProtocol + " alfrescoHost=" + url+ " s3Uri=" + s3BucketNamePath + " queryJson=" + queryJson;
		if (this.awsSecretsManagerSecretArn == null)
			logStr = logStr + " alfrescoSA=" + userId;
		else
			logStr = logStr + " awsSecretsArn=" + this.awsSecretsManagerSecretArn.toString();

		this.logger.log(logStr);
		int summarizationsDone = 0;
		try
		{
			getAwsSecretAndAlfrescoTicket();
			this.logger.log("Starting Alfresco query with Alfresco userId "+this.userId);
			searchAlfresco(queryJson);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException | InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return summarizationsDone;
	}

	/****
	 * This method trusts all certificates in order to accommodate websites that use self-signed certificates
	 * 
	 * @return Alfresco ticket
	 * @throws KeyStoreException,
	 *             NoSuchAlgorithmException, KeyManagementException, IOException
	 */
	protected String getAlfrescoTicket() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		String idVal = null;
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build(); // SSL context that trusts all certificates

		// Use the above SSL context to create a HTTP client that accepts self-signed certificates.
		//this.httpClient = HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier((hostname, session) -> true).build();
		this.httpClient=HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).setSSLContext(sslContext).setSSLHostnameVerifier((hostname, session) -> true).build();

		// try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier((hostname, session) -> true).build())
		{
			String httpTicketUrl = "://" + this.url + "/alfresco/api/-default-/public/authentication/versions/1/tickets";
			httpTicketUrl = this.httpProtocol ? "https" + httpTicketUrl : "http" + httpTicketUrl;
			HttpPost request = new HttpPost(httpTicketUrl);
			String json = "{\"userId\":\"" + this.userId + "\",\"password\":\"" + this.password + "\"}";
			StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
			entity.setContentType("application/json");
			request.setEntity(entity);
			CloseableHttpResponse httpResponse = this.httpClient.execute(request);
			HttpEntity httpEntity = httpResponse.getEntity();
			JSONObject jsonResponse = new JSONObject(EntityUtils.toString(httpEntity));
			idVal = jsonResponse.getJSONObject("entry").getString("id");
			httpResponse.close();
		}
		return idVal;
	}

	protected CloseableHttpResponse getAlfrescoHttpGetResponseNodeContent(String nodeId) throws ClientProtocolException, IOException
	{
		// https://acs.ireeks.duckdns.org/alfresco/api/-default-/public/alfresco/versions/1/nodes/796a2728-4018-460c-9705-b14581f6fe29/content?attachment=false
		String getContentUrl = "://" + this.url + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId + "/content?attachment=false";
		getContentUrl = this.httpProtocol ? "https" + getContentUrl : "http" + getContentUrl;
		// Create GET request with JSON payload
		HttpGet httpGet = new HttpGet(getContentUrl);
		httpGet.setHeader("Content-Type", "application/json");
		httpGet.setHeader("Accept", "application/json");
		httpGet.setHeader("Authorization", "Basic " + this.encodedTicket);
		return httpClient.execute(httpGet);
	}

	/**
	 * Retrieves Alfresco content as text string.
	 * 
	 * @param nodeId
	 *            Alfresco provided Id of node to get content of.
	 * @return String representation of content.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected String getAlfrescoContent(String nodeId) throws ClientProtocolException, IOException
	{
		// Send the request and receive the response
		CloseableHttpResponse response = getAlfrescoHttpGetResponseNodeContent(nodeId);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}
		response.close();
		return builder.toString();
	}

	/**
	 * Stages Alfresco content into S3, provides presigned URL to the staged content for 10mins.
	 * 
	 * @param nodeId
	 * @param nodeName
	 * @return Presigned URL of staged content.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected URL putAlfrescoContentOnS3(String nodeId, String nodeName) throws ClientProtocolException, IOException
	{
		// Send the request and receive the response
		CloseableHttpResponse response = getAlfrescoHttpGetResponseNodeContent(nodeId);

		HttpEntity entity = response.getEntity();
		// Get the response body as a string
		InputStream contentStream = entity.getContent();
		URL retUrl = s3Utils.putInputStreamIntoS3(this.s3Client, this.s3Presigner, nodeId + "|" + nodeName, contentStream);
		response.close();
		return retUrl;
	}

	/**
	 * Queries Alfresco for documents and invokes Amazon Bedrock to summarize each of them.
	 * 
	 * @param queryJson
	 *            Base query- {"query":{"language":"afts","query":"TYPE:'cm:content' AND ASPECT:'crestBedrock:GenAI' AND crestBedrock:generateSummary:'true' AND name:*"},"include":["properties"]} Adapt as required. This is passed in as the Lambda environment variable LLLLLLL
	 */
	protected int searchAlfresco(String queryJson)
	{
		int summarizationsDone = 0;
		if (searchHttpPost == null)
		{
			// Create POST request with JSON payload
			String searchUrl = "://" + this.url + "/alfresco/api/-default-/public/search/versions/1/search";
			searchUrl = this.httpProtocol ? "https" + searchUrl : "http" + searchUrl;
			HttpPost httpPost = new HttpPost(searchUrl);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Authorization", "Basic " + this.encodedTicket);
			this.searchHttpPost = httpPost;
		}
		// Create JSON payload
		try
		{
			this.searchHttpPost.setEntity(new StringEntity(queryJson));
			CloseableHttpResponse response = this.httpClient.execute(this.searchHttpPost); // Error is java.lang.IllegalStateException:Connection pool shut down
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			Gson gson = new Gson();
			SearchResults sr = gson.fromJson(responseString, SearchResults.class);
			List<Entries> entries = sr.getList().getEntries();
			response.close();
			this.logger.log("Alfresco query returned "+entries.size()+" nodes marked for summarization.");
			for (int e = 0; e < entries.size(); e++)
			{
				Entry entry = entries.get(e).getEntry();
				Properties nodeProps = entry.getProperties();
				if (nodeProps.getCrestBedrock_fm().startsWith("anthropic"))
				{
					String nodeMimeType = entry.getContent().getMimeType();
					String aiResponse;
					Date timeWhenBedrockInferred = new Date();
					// System.out.println("Processing #"+e+" "+entry.getName()+":"+entry.getId());
					this.logger.log("Processing #" + e + " " + entry.getName() + ":" + entry.getId());
					this.httpClient.close();
					this.ticket = getAlfrescoTicket(); // Since each HttpClient only provides 2 routes, a new one will have to be created.
					if (nodeMimeType.equalsIgnoreCase(Content.MIME_TEXTDoc))
					{
						// get content and send to Bedrock
						JSONObject bedrockReply = BedrockInvokeClaude(nodeProps.getCrestBedrock_prompt(), nodeProps.getCrestBedrock_responseLength(), nodeProps.getCrestBedrock_temperature(), getAlfrescoContent(entry.getId()));
						aiResponse = bedrockReply.get("completion").toString();

					}
					else
						if (nodeMimeType.equalsIgnoreCase(Content.MIME_MSWordDoc) || nodeMimeType.equalsIgnoreCase(Content.MIME_MSWordXDoc) || nodeMimeType.equalsIgnoreCase(Content.MIME_PDFDoc) || nodeMimeType.equalsIgnoreCase(Content.MIME_RFTDoc)
								|| nodeMimeType.equalsIgnoreCase(Content.MIME_TEXTDoc))
						{
							// stage content onto S3 and send to Bedrock using S3 presignedURL
							URL presignedDoc = putAlfrescoContentOnS3(entry.getId(), entry.getName());
							JSONObject bedrockReply = BedrockInvokeClaude(nodeProps.getCrestBedrock_prompt(), nodeProps.getCrestBedrock_responseLength(), nodeProps.getCrestBedrock_temperature(), presignedDoc);
							s3Utils.deleteFileFromS3(this.s3Client, entry.getId() + "|" + entry.getName());
							aiResponse = bedrockReply.get("completion").toString();

						}
						else
						{
							// TODO mark in Alfresco entry that MIME is not compatible for summmarization
							aiResponse = null;
						}
					// Claude usually titles its responses, let's remove the first line.
					if (aiResponse == null)
						aiResponse = "";
					else
						aiResponse = removeFirstLine(aiResponse, true);

					nodeProps.setCrestBedrock_generateSummary(false);
					nodeProps.setCrestBedrock_summary(aiResponse);
					nodeProps.setCrestBedrock_summaryTime(timeWhenBedrockInferred);
					// System.out.print("Updating "+entry.getId());
					updateAlfrescoNode(nodeProps, entry.getId());
					// System.out.println(". Completed. Summarization took " +((new Date()).getTime() - timeWhenBedrockInferred.getTime())/1000+" seconds.");
					this.logger.log(entry.getId() + ". Completed. Summarization took " + ((new Date()).getTime() - timeWhenBedrockInferred.getTime()) / 1000 + " seconds.");
					summarizationsDone++;
				}
				else
					// System.out.println("Non Anthropic FMs currently not supported");
					this.logger.log("Non Anthropic FMs currently not supported");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return summarizationsDone;
	}

	public static String removeFirstLine(String input, boolean emptyLinesAfterFirstLine)
	{
		// Remove the first line (up to the first newline character)
		int indexOfFirstNewline = input.indexOf('\n');
		String removedTopLine = input.substring(indexOfFirstNewline + 1);
		if (emptyLinesAfterFirstLine && removedTopLine.startsWith("\n"))
			removedTopLine = removeFirstLine(removedTopLine, emptyLinesAfterFirstLine);
		return removedTopLine;
	}

	protected String performPutRestApiCall(String endpoint, JsonObject jsonObject) throws HttpResponseException, IOException, JsonSyntaxException
	{

		HttpPut httpPut = new HttpPut(endpoint);

		httpPut.setHeader("Accept", "application/json");
		httpPut.setHeader("Content-Type", "application/json");
		httpPut.setHeader("Authorization", "Basic " + this.encodedTicket);
		httpPut.setEntity(new StringEntity(jsonObject.toString()));

		HttpResponse response = this.httpClient.execute(httpPut);
		String returnMsg = null; // if null, update is successful
		if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
		{
			switch (response.getStatusLine().getStatusCode())
			{
				case HttpURLConnection.HTTP_BAD_REQUEST :
					returnMsg = "The update request is invalid or nodeId is not a valid format or nodeBodyUpdate is invalid.";
					break;
				case HttpURLConnection.HTTP_UNAUTHORIZED :
					returnMsg = "Authentication failed.";
					break;
				case HttpURLConnection.HTTP_FORBIDDEN :
					returnMsg = "Current user does not have permission to update nodeId.";
					break;
				case HttpURLConnection.HTTP_NOT_FOUND :
					returnMsg = "nodeId does not exist.";
					break;
				default :
					returnMsg = "Unexpected error.";
			}

		}
		return returnMsg;
	}

	protected void updateAlfrescoNode(Properties prop, String nodeId) throws HttpResponseException, IOException
	{
		String alfrescoNodeUpdateRestEndpoint = "://" + this.url + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId;
		alfrescoNodeUpdateRestEndpoint = this.httpProtocol ? "https" + alfrescoNodeUpdateRestEndpoint : "http" + alfrescoNodeUpdateRestEndpoint;

		JsonObject propBody = new JsonObject();
		propBody.addProperty("crestBedrock:summary", prop.getCrestBedrock_summary());
		propBody.addProperty("crestBedrock:generateSummary", prop.getCrestBedrock_generateSummary());
		propBody.addProperty("crestBedrock:summaryTime", alfrescoDateFormat.format(prop.getCrestBedrock_summaryTime()));
		JsonObject jsonBody = new JsonObject();
		jsonBody.add("properties", propBody);

		String returnMsg = performPutRestApiCall(alfrescoNodeUpdateRestEndpoint, jsonBody);
		if (!(returnMsg == null))
		{
			// System.out.println(returnMsg+" returned by Alfresco repository when attempting to update node "+nodeId);
			this.logger.log(returnMsg + " returned by Alfresco repository when attempting to update node " + nodeId);
		}

	}

	protected JSONObject BedrockInvokeClaude(JSONObject jsonBody)
	{
		SdkBytes body = SdkBytes.fromUtf8String(jsonBody.toString());
		InvokeModelRequest request = InvokeModelRequest.builder().modelId("anthropic.claude-v2").body(body).build();
		InvokeModelResponse response = this.bedrockClient.invokeModel(request);
		JSONObject jsonObject = new JSONObject(response.body().asString(StandardCharsets.UTF_8));
		return jsonObject;
	}

	protected JSONObject BedrockInvokeClaude(String prompt, int responseLength, float temperature, String textToInfer)
	{
		JSONObject jsonBody = new JSONObject().put("prompt", "Human: " + prompt + " the following- " + textToInfer + " Assistant:").put("temperature", temperature).put("max_tokens_to_sample", responseLength);
		return BedrockInvokeClaude(jsonBody);
	}

	protected JSONObject BedrockInvokeClaude(String prompt, int responseLength, float temperature, URL contentLink)
	{
		JSONObject jsonBody = new JSONObject().put("prompt", "Human: " + prompt + " document found in " + contentLink.toExternalForm() + " Assistant:").put("temperature", temperature).put("max_tokens_to_sample", responseLength);
		return BedrockInvokeClaude(jsonBody);
	}

	/*
	 * public static void main(String[] args) throws Exception { if (System.getenv("AWS_REGION") == null) if (System.getProperty("aws.region") == null) System.setProperty("aws.region", Region.US_EAST_1.toString());
	 * 
	 * String alfrescoSearchQuery = System.getenv("alfrescoSearchQuery"); if (alfrescoSearchQuery==null) alfrescoSearchQuery = args[4];
	 * 
	 * AlfrescoReSTAPIAuthentication ara = new AlfrescoReSTAPIAuthentication(args[0], true, args[1], args[2], args[3]); ara.searchAlfresco(alfrescoSearchQuery); }
	 */
}
