package com.java.filemanager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {
    @FXML
    TableView<FileInfo> fileTable;
    @FXML
    ComboBox<String> disksBox;
    @FXML
    TextField pathField;
    // подготовительная работа
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        // обрабатываем (преобразовываем) файл в значение ячейки
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        // обрабатываем (преобразовываем) файл в значение ячейки
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(250);
        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(120);
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes",item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDataColumn = new TableColumn<>("Дата изменения");
        fileDataColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDataColumn.setPrefWidth(120);

        // Добавляем в таблицу
        fileTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDataColumn);
        fileTable.getSortOrder().add(fileTypeColumn);

        // при запуске очищаем комбобокс и сканируем файловую систему
        disksBox.getItems().clear();
        for(Path p : FileSystems.getDefault().getRootDirectories()){
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);

        // переход по папкам по клику мышки
        fileTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount()==2){
                    // стороим путь --
                    //get(pathField.getText()) - получаем текущий путь
                    //resolve(fileTable.getSelectionModel().getSelectedItem().getFileName()) -- запрашиваем имя и добавляем
                    Path path = Paths.get(pathField.getText()).resolve(fileTable.getSelectionModel().getSelectedItem().getFileName());
                    //...но только для директорий
                    if (Files.isDirectory(path)){
                        updateList(path);
                    }
                };
            }
        });
        //Paths.get создание путей
        updateList(Paths.get(".", "/A"));
    }

    public void updateList (Path path){
        // stream api -- берет любую папку вычитывает список файлов - список ФАЙлов преобразует к списку файлинфо и в табл
        // Files.list по указанному пути возвращает поток путей
        // преобразовываем к файл инфо  -- .map(FileInfo::new берем каждый файл и проводим через конструктор файл инфо
        // собираем в список и отдаем в таблицу
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            fileTable.getItems().clear();
            fileTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            fileTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if(upperPath != null){
            updateList(upperPath);
        }

    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    // выбраный файл с проверкой на фокус
    public String getSelectedFileName(){
        if(!fileTable.isFocused()){
            return null;
        }
        return fileTable.getSelectionModel().getSelectedItem().getFileName();
    }
    // запрос текущего пути
    public String getCurrentPath(){
        return pathField.getText();
    }
}
