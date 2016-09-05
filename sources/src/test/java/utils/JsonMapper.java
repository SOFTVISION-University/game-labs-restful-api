package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaSV.gameLabz.utils.JsonViews;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.io.IOException;

public class JsonMapper {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String objectToJson(Object obj) {

        return objectToJsonWithView(obj, JsonViews.Default.class);
    }

    public static String objectToJsonWithView(Object obj, Class view) {

        try {
            return mapper.writerWithView(view).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to convert " + obj.getClass().getSimpleName() + " to json!");
            return null;
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {

        validateEmptyJson(json, clazz);

        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            Assert.fail("Failed to convert " + json + " to " + clazz.getSimpleName());
            return null;
        }
    }

    public static <T> T jsonToObject(String json, TypeReference<T> type) {

        validateEmptyJson(json, type.getClass());

        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            Assert.fail("Failed to convert " + json + " to " + type.getClass().getSimpleName());
            return null;
        }
    }


    private static <T> void validateEmptyJson(String json, Class<T> clazz){
        if (StringUtils.isBlank(json)) {
            Assert.fail("String is empty! Failed at class: " + clazz.getSimpleName());
        }
    }
}
