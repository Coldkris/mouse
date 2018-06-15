/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.site.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author kris
 * @version $Id: JsonBuilder.java, v 0.1 2018年6月15日 下午3:42:55 kris Exp $
 */
public class JsonBuilder {

    private FieldNamingStrategy fieldNamingStrategy = new FieldNamingStrategy() {

                                                        @Override
                                                        public String translateName(Field f) {
                                                            String name = f.getName();

                                                            if (name.startsWith("m_")) {
                                                                return name.substring(2);
                                                            } else {
                                                                return name;
                                                            }
                                                        }
                                                    };

    private Gson                gson                = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss")
        .setFieldNamingStrategy(fieldNamingStrategy).create();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object parse(String json, Class clz) {
        return gson.fromJson(json, clz);
    }

    public String toJson(Object o) {
        return gson.toJson(o);
    }

    public String toJsonWithEnter(Object o) {
        return gson.toJson(o) + "\n";
    }

    public class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonPrimitive)) {
                throw new JsonParseException("日期应该是一个字符串值");
            }

            try {
                Date date = format.parse(json.getAsString());
                return new Timestamp(date.getTime());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
            String dateFormatAsString = format.format(new Date(src.getTime()));
            return new JsonPrimitive(dateFormatAsString);
        }

    }

}
