package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {
    public static void main (String[] args) throws Exception{
        final Schema schema = new Schema(1, "nestedternary.project.database.schema");

        final Entity binLocations = schema.addEntity("BinLocations");
        binLocations.addIdProperty();

        binLocations.addStringProperty("name");
        binLocations.addStringProperty("address");
        binLocations.addDoubleProperty("latitude");
        binLocations.addDoubleProperty("longtitude");

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }
}