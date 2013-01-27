/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arminhammer.pojostick;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
     * Gson custom deserializer class to make sure PojoActions are properly
     * deserialized.
     */
    public class PojoItemDeSerializer implements JsonDeserializer<PojoItem> {

        private Logger LOGGER = this.LOGGER = LoggerFactory.getLogger(PojoItemDeSerializer.class);
        /**
         * Internal method that deserializes a PojoItem object.
         *
         * @param json
         * @param typeOfT
         * @param context
         * @return
         * @throws JsonParseException
         */
        @Override
        public PojoItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jobject = (JsonObject) json;
            PojoItem newPI = new PojoItem();
            newPI.setType(jobject.get("type").getAsString());
            try {
                newPI.setItem(context.deserialize(jobject.get("item"), Class.forName(newPI.getType())));
            }
            catch (ClassNotFoundException ex) {
                LOGGER.error("Class not found error: " + ex);
            }
            return newPI;
        }
    }
