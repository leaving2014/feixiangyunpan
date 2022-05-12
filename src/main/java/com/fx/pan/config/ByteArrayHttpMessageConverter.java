package com.fx.pan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

/**
 * @author leaving
 * @date 2022/3/11 22:50
 * @version 1.0
 */

@Configuration
public class ByteArrayHttpMessageConverter extends AbstractHttpMessageConverter<byte[]> {
    @Override
    protected boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    protected byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return new byte[0];
    }

    @Override
    protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    }

    @Override
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return super.getSupportedMediaTypes(clazz);
    }
}
