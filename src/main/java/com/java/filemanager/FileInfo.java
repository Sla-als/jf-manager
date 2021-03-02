package com.java.filemanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

// класс для описания файлов
public class FileInfo {
    // вложенный класс
    public enum FileType{
        FILE("F"), DIRECTORY("D");

        public String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name=name;
        }
    }

    private String fileName;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;


    public String getFileName() {
        return fileName;
    }

    // Переименовать файл
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public FileInfo(Path path){
        try {
            this.fileName = path.getFileName().toString();
            this.size= Files.size(path);
            this.type= Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            // для сортировки
            if (this.type == FileType.DIRECTORY){
                this.size = -1L;
            }
            this.lastModified=LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }


}
