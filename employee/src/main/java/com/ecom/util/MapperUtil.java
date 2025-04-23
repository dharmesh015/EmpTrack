package com.ecom.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class MapperUtil {
//	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private ObjectMapper mapper;

    // Method to convert a single object
    public  <T> T convertValue(Object frontValue, Class<T> valueType) {
        return mapper.convertValue(frontValue, valueType);
    }

    // Method to convert a list of objects
    public  <T, R> List<R> convertList(List<?> frontList, Class<R> valueType) {
        return frontList.stream()
                        .map(item -> convertValue(item, valueType))
                        .collect(Collectors.toList());
    }
    
  
   
}
