package com.sumscope.optimus.moneymarket.commons.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.BusinessRuntimeExceptionType;
import com.sumscope.optimus.commons.log.LogManager;

import java.io.IOException;

/**
 * Created by Administrator on 2016/10/20.
 */
public final class JsonNodeUtils {

    public static JsonNode getJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        if (json != null && !"".equals(json)) {
            try {
                return mapper.readTree(json);
            } catch (IOException e) {
                LogManager.error(e.toString());
            }
        }
        throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID,"您的参数不符合json规范");
    }

    private JsonNodeUtils() {
    }
}
