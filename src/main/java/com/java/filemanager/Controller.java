package com.java.filemanager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Controller {
       @FXML
    VBox leftPanel, rightPanel;

    public void btnExitAction (ActionEvent actionEvent){
        Platform.exit();
    }
    public void copyBtnAction(ActionEvent actionEvent) {
        // из левой панели достали ссылку на контроллер левой панели и для правой то же
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFileName() == null && rightPC.getSelectedFileName() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        PanelController srcPC = null, dstPC = null;
        if (leftPC.getSelectedFileName() != null){
            srcPC =leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFileName() != null){
            srcPC =rightPC;
            dstPC = leftPC;
        }
        // мы берем панель запрашиваем путь - запрашиваем файл
        Path srcPath = Paths.get(srcPC.getCurrentPath(),srcPC.getSelectedFileName());
        Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName().toString());
        try{

//            Files.walkFileTree(srcPath,new SimpleFileVisitor<Path>(){
//                @Override
//                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                    Files.copy(srcPath,dstPath);
//                    System.out.println("copy dir: " + dir.toString());
//                    return FileVisitResult.CONTINUE;
//                }
//
//                @Override
//                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                    System.out.println("copy file: " + file.toString());
//                    Files.copy(srcPath,dstPath);
//                    return FileVisitResult.CONTINUE;
//                }
//
//            });

           Files.copy(srcPath,dstPath);
           dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
        } catch (Exception e) {
            // можно добавить запрос о перезаписи
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Перезаписать файл?", ButtonType.OK,ButtonType.CANCEL);
            alert.showAndWait();
            //alert.getButtonTypes().add(ButtonType.CLOSE); Добавить обработку нажатий
            try {
                Files.copy(srcPath,dstPath,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void moveBtnAction(ActionEvent actionEvent) {
        // из левой панели достали ссылку на контроллер левой панели и для правой то же
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFileName() == null && rightPC.getSelectedFileName() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        PanelController srcPC = null, dstPC = null;
        if (leftPC.getSelectedFileName() != null){
            srcPC =leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFileName() != null){
            srcPC =rightPC;
            dstPC = leftPC;
        }
        // мы берем панель запрашиваем путь - запрашиваем файл
        Path srcPath = Paths.get(srcPC.getCurrentPath(),srcPC.getSelectedFileName());
        Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName().toString());
        try{
            Files.move(srcPath,dstPath);
            srcPC.updateList(Paths.get(srcPC.getCurrentPath()));
            dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
        } catch (Exception e) {
            // можно добавить запрос о перезаписи
            Alert alert = new Alert(Alert.AlertType.ERROR,"Не удалось переместить указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }
    public void deleteBtnAction(ActionEvent actionEvent) {
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFileName() == null && rightPC.getSelectedFileName() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        PanelController srcPC = null, dstPC = null;
        if (leftPC.getSelectedFileName() != null){
            srcPC =leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFileName() != null){
            srcPC =rightPC;
            dstPC = leftPC;
        }
        // мы берем панель запрашиваем путь - запрашиваем файл
        //Path srcPath = Paths.get(srcPC.getCurrentPath(),srcPC.getSelectedFileName());
        Path srcPath = Paths.get(srcPC.getCurrentPath());
        try{
           // Files.delete(srcPath); не подходит, так как удаляет только пустые каталоги и файлы
            Files.walkFileTree(srcPath,new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    System.out.println("delete dir: " + dir.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println("delete file: " + file.toString());
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

            });

            srcPC.updateList(Paths.get(srcPC.getCurrentPath()).getParent());
            //dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
        } catch (Exception e) {
            // можно добавить запрос о перезаписи
            Alert alert = new Alert(Alert.AlertType.ERROR,"Не удалось удалить указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
