package com.myapp.backend.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class FileUtil {
    private static final Gson gson = new Gson();

    public static <T> void writeListToFile(String path, List<T> data) {
        try (Writer writer = new FileWriter(path)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> readListFromFile(String path, Class<T> clazz) {
        try (Reader reader = new FileReader(path)) {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            System.out.println("File not found, returning empty list.");
            return new java.util.ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
