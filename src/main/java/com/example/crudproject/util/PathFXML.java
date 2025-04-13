package com.example.crudproject.util;

import java.nio.file.Paths;

public class PathFXML {
    public static String pathBase() {
        return Paths.get("C:\\Users\\pheli\\OneDrive\\Desktop\\crudProject\\src\\main\\resources\\view").toAbsolutePath().toString();
    }
}
