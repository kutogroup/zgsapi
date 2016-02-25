package com.zgs.api.utils;

import com.zgs.api.CommonConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by simon on 15-11-24.
 */
public class FileUtils {
    public static File globalFolder = CommonConfig.globalContext.getExternalCacheDir();

    /**
     * @param file: file name
     * @return
     */
    public static File getFile(String file) {
        if (!globalFolder.exists()) {
            if (!globalFolder.mkdirs()) {
                return null;
            }
        }

        return new File(globalFolder, file);
    }

    public static boolean appendFile(File file, String msg) {
        try {
            RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
            randomFile.seek(randomFile.length());
            randomFile.write(msg.getBytes());
            randomFile.close();

            return true;
        } catch (FileNotFoundException e) {
            LogUtils.error("\"" + msg + "\" file not found");
        } catch (IOException e) {
            LogUtils.error("\"" + msg + "\" io exception");
        }

        return false;
    }

    public static boolean copyStreamToFile(InputStream fis, File dst) {
        try {
            OutputStream myOutput = new FileOutputStream(dst);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();

            return true;
        } catch (IOException e) {

        }

        return false;
    }

    public static boolean copyStringToFile(String str, File dst) {
        return copyStreamToFile(new ByteArrayInputStream(str.getBytes()), dst);
    }

    public static String getStringFromFile(File src) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = new FileInputStream(src);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }

            bos.flush();
            bos.close();
        } catch (IOException e) {
            bos.reset();
        }

        return new String(bos.toByteArray());
    }

    /**
     * @param file 删除文件或者文件夹
     */
    public static void delete(File file) {
        if (file.isFile()) {
            LogUtils.info("file=" + file.getPath() + ", delete result=" + file.delete());
            return;
        }

        File[] childFiles = file.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            LogUtils.info("file=" + file.getPath() + ", delete result=" + file.delete());
            return;
        }
        for (int i = 0; i < childFiles.length; i++) {
            delete(childFiles[i]);
        }
        LogUtils.info("file=" + file.getPath() + ", delete result=" + file.delete());
    }

    /**
     * @param file 清空文件夹（不删除）
     */
    public static void empty(File file) {
        if (file.isFile()) {
            return;
        }

        File[] childFiles = file.listFiles();
        if (childFiles == null || childFiles.length == 0) {
            return;
        }

        for (int i = 0; i < childFiles.length; i++) {
            delete(childFiles[i]);
        }
    }

    /**
     * 获取文件或者文件夹大小
     *
     * @param f
     * @return
     */
    public static long getFileSize(File f) {
        if (f.isFile()) {
            return f.length();
        }

        long size = 0;
        File list[] = f.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                size = size + getFileSize(list[i]);
            } else {
                size = size + list[i].length();
            }
        }

        return size;
    }

    /**
     * 根据时间排序
     *
     * @param folder
     * @return
     */
    public static File[] getLastModifiedSortedList(File folder) {
        if (folder != null || folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                List<File> list = Arrays.asList(files);
                Collections.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        if (lhs.lastModified() < rhs.lastModified()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });

                return list.toArray(new File[list.size()]);
            }
        }

        return null;
    }

    /**
     * 控制文件夹大小
     *
     * @param folder
     * @param maxSize
     */
    public static void fitFolderMaxSize(File folder, long maxSize) {
        if (folder == null || folder.isFile()) {
            return;
        }

        long folderSize = getFileSize(folder);
        if (folderSize <= maxSize) {
            return;
        }

        folderSize -= maxSize;
        File[] files = getLastModifiedSortedList(folder);

        if (files != null) {
            for (int n = 0; n < files.length; n++) {
                if (folderSize > 0) {
                    folderSize -= files[n].length();
                    files[n].delete();
                } else {
                    break;
                }
            }
        }
    }
}
