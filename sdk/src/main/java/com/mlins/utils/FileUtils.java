package com.mlins.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtils {
    public static String getFileContent(File file) {
        String result = "";
        try {
            result = getFileContentWithException(file);
        } catch (FileNotFoundException e) {
            Log.e(FileUtils.class.getName(), "File not found: " + file.getAbsolutePath(), e);
        }

        return result;
    }

    public static String getFileContentWithException(File file) throws FileNotFoundException {
        StringBuilder content = new StringBuilder();
        Scanner scanner = null;
        scanner = new Scanner(file, "UTF-8");
        try {
            while (scanner.hasNext()) {
                content.append(scanner.nextLine());
            }
        } finally {
            scanner.close();
        }
        return content.toString();
    }
}
