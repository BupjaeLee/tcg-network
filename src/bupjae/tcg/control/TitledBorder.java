/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.control;

import java.io.IOException;
import javafx.beans.DefaultProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Bupjae
 */
@DefaultProperty("innerComponents")
public class TitledBorder extends StackPane {

    @FXML
    private Label titleLabel;
    @FXML
    private StackPane innerPane;

    public TitledBorder() {
        FXMLLoader fxmlLoader = new FXMLLoader(TitledBorder.class.getResource("TitledBorder.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    public void setTitle(String title) {
        titleLabel.textProperty().set(title);
    }

    public String getTitle() {
        return titleLabel.textProperty().getValueSafe();
    }

    public ObservableList<Node> getInnerComponents() {
        return innerPane.getChildren();
    }

    public ObservableList<Node> getInnerComponentsUnmodifiable() {
        return innerPane.getChildrenUnmodifiable();
    }
}
