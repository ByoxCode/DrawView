package com.byox.drawviewproject.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ing. Oscar G. Medina Cruz on 20/07/2016.
 */
public class FileUtils {

    // DIRECTORIES
    public static String FILE_DIRECTORY = "/FILES";

    // FILES
    public static String PET_APPOINTMENTS_JSON = "/pet_appointments.json";

    public static String ReadAssetFileAsString(AssetManager assetManager, String fileToRead) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = assetManager.open(fileToRead);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;

        while ((line = bufferedReader.readLine()) != null)
            stringBuilder.append(line);

        bufferedReader.close();

        return stringBuilder.toString();
    }

    public static String ReadFileAsString(String fileToRead) throws IOException {
        File file = new File(fileToRead);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;

        while ((line = bufferedReader.readLine()) != null)
            stringBuilder.append(line);

        bufferedReader.close();

        return stringBuilder.toString();
    }

    public static boolean CheckFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static void WriteFile(Context context, String folderName, String fileName,
                                 String fileContent) throws IOException {
        File fileDirectory = new File(context.getFilesDir().getAbsolutePath() + folderName);
        if (!fileDirectory.exists())
            fileDirectory.mkdirs();

        File file = new File(context.getFilesDir().getAbsolutePath() + folderName + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(fileContent.getBytes());
        fileOutputStream.close();
    }

    public static List<File> GetImageList(File parentDir) {
        List<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();

        try {
            for (File file : files) {
                if (file.isDirectory() &&
                        !file.getName().toLowerCase().endsWith("android") &&
                        !file.getName().toLowerCase().endsWith("thumbnails") &&
                        !file.getName().toLowerCase().startsWith(".")) {
                    if (file.listFiles().length > 0)
                        inFiles.addAll(GetImageList(file));
                } else {
                    if (file.getName().toLowerCase().endsWith("jpg") ||
                            file.getName().toLowerCase().endsWith("png"))
                        inFiles.add(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inFiles;
    }

    public static List<File> GetSortedFilesByDate(List<File> fileList) {
        File[] files = new File[fileList.size()];
        for (int i = 0; i < files.length; i++)
            files[i] = (File) fileList.get(i);
        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }

        });
        fileList = Arrays.asList(files);
        files = null;
        return fileList;
    }

    public static byte[] GetFileAsByteArray(File file) {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            return bFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] GetByteArrayFromURL(String url) throws IOException {
        URL imageUrl = new URL(url);
        InputStream inputStream = imageUrl.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        return baos.toByteArray();
    }

    public static File SaveBitmapInTemporaryFile(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        File file = File.createTempFile("temp", ".jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, quality, fileOutputStream);
        return file;
    }

    public static File SaveBitmapInTemporaryFile(Bitmap bitmap, String fileName, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        File file = File.createTempFile(fileName, ".jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(compressFormat, quality, fileOutputStream);
        return file;
    }
}
