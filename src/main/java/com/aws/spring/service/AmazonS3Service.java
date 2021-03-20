package com.aws.spring.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class AmazonS3Service {

	
	@Value("${application.bucket.name}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 s3Client;
	
	/**
	 * This service is used to upload file in S3 bucket
	 * @param multipartFile
	 * @return
	 */
	public String upload(MultipartFile multipartFile) {
		File file = convertToFile(multipartFile);
		String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
		try {
			s3Client.putObject(new PutObjectRequest("dkjashkd", fileName, file));
			file.delete();
			return "File Uploaded: "+multipartFile.getOriginalFilename();
		} catch (AmazonServiceException e) {
			e.printStackTrace();
			return e.getErrorCode();
		}  catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	/**
	 * This is the utility method for convert MultipartFile to File
	 * @param multipartFile
	 * @return
	 */
	private File convertToFile(MultipartFile multipartFile) {
		File file = new File(multipartFile.getOriginalFilename());
		try(FileOutputStream fos = new FileOutputStream(file)){
			fos.write(multipartFile.getBytes());
			fos.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This Service is used to download file from S3 Bucket with given filename
	 * @param filename
	 * @return
	 */
	public byte[] download(String filename) {
		S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, filename));
		S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
		try {
			byte[] data = IOUtils.toByteArray(s3ObjectInputStream);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String delete(String filename) {
		try {
			s3Client.deleteObject(bucketName, filename);
			return "File deleted: "+filename;
		} catch (AmazonServiceException e) {
			e.printStackTrace();
			return e.getErrorCode();
		}  catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
}
