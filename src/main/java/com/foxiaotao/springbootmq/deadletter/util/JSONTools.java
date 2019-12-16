package com.foxiaotao.springbootmq.deadletter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * JSON工具类，默认使用Jackson
 * <p>
 * Created by WeiWei on 2017/7/31.
 */
@Slf4j
public class JSONTools {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 序列化
     *
     * @param data
     * @return
     */
    public static String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化
     *
     * @param dataString
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String dataString, TypeReference type) {
        try {
            return (T) objectMapper.readValue(dataString, type);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化
     *
     * @param dataString
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String dataString, Class<T> type) {
        try {
            return objectMapper.readValue(dataString, type);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换数据类型
     *
     * @param data
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T convert(Object data, Class<T> type) {
        return deserialize(JSONTools.serialize(data), type);
    }

    /**
     * 转换数据类型
     *
     * @param data
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T convert(Object data, TypeReference type) {
        return deserialize(JSONTools.serialize(data), type);
    }

    /**
     * 将对象转成Map
     *
     * @param data
     * @return
     */
    public static Map toMap(Object data) {
        return convert(data, Map.class);
    }



}
