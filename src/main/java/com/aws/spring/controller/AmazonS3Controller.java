package com.aws.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aws.spring.service.AmazonS3Service;

@RestController
@RequestMapping(value="/file")
public class AmazonS3Controller {

	@Autowired
	private AmazonS3Service amazonS3Service;
	
	@PostMapping("/upload")
	public ResponseEntity<String> upload(@RequestParam MultipartFile multipartFile) {
		return new ResponseEntity<>(amazonS3Service.upload(multipartFile), HttpStatus.OK);
	}
	
	@GetMapping("/download/{filename}")
	public ResponseEntity<ByteArrayResource> download(@PathVariable String filename) {
		byte[] data = amazonS3Service.download(filename);
		ByteArrayResource byteArrayResource = new ByteArrayResource(data);
		return ResponseEntity
				.ok()
				.contentLength(data.length)
				.header("content-type", "appplication/octet-stream")
				.header("content-disposition", "attachment; filename=\""+filename+"\"")
				.body(byteArrayResource);
	}
	
	@GetMapping("/delete/{filename}")
	public ResponseEntity<String> delete(@PathVariable String filename) {
		return new ResponseEntity<>(amazonS3Service.delete(filename), HttpStatus.OK);
	}
}
