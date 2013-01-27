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
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gson custom deserializer class to make sure PojoActions are properly
 * deserialized.
 */
public class PojoActionDeSerializer implements JsonDeserializer<PojoAction> {

    private Logger LOGGER = this.LOGGER = LoggerFactory.getLogger(PojoActionDeSerializer.class);
    /**
     * Internal method that deserializes a PojoAction object. It is necessary
     * because otherwise the target is cast as a Gson String Map instead of the
     * type it needs to be.
     *
     * @param json
     * @param typeOfT
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public PojoAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = (JsonObject) json;
        PojoAction newPA = new PojoAction();
        newPA.setAction(PojoStick.Action.valueOf(jobject.get("action").getAsString()));
        newPA.setType(jobject.get("type").getAsString());
        try {
            newPA.setTarget(context.deserialize(jobject.get("target"), Class.forName(newPA.getType())));
        }
        catch (ClassNotFoundException ex) {
            LOGGER.error("Class not found error: " + ex);
        }
        if (jobject.get("newTarget") != null) {
            try {
                newPA.setNewVersion(context.deserialize(jobject.get("newTarget"), Class.forName(newPA.getType())));
            }
            catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(PojoStick.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return newPA;
    }
}
