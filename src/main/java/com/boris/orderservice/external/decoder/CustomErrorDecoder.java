package com.boris.orderservice.external.decoder;

import com.boris.orderservice.exception.CustomException;
import com.boris.orderservice.external.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        log.info("::{}", response.request().url());
        log.info("::{}", response.request().headers());


        try {
            if (response.body() != null) {
                log.info("::{}", response.body().toString());
                ErrorResponse erorResponse =
                        objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
                return new CustomException(erorResponse.getErrorMessage(), erorResponse.getErrorCode(), response.status());
            }else {
                return new CustomException("Response is Null", "INTERNAL_SERVER_ERROR",500);
            }

        } catch (IOException e) {
            throw new CustomException("Internal Server Error", "INTERNAL_SERVER_ERROR",500);
        }

    }
}
