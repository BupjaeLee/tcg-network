/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common;

import bupjae.tcg.common.proto.CardInfo;
import bupjae.tcg.common.data.DataManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Bupjae
 */
public class MainController implements Initializable {

    @FXML
    private TableView<CardInfo> masterTable;
    @FXML
    private TableView<DataManager.DeckWrapper.EntryWrapper> userTable;
    @FXML
    private ImageView cardView;

    private FileChooser userFileChooser;
    private DataManager dataManager;
    private Stage stage;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        masterTable.setItems(dataManager.getMaster());
        userTable.setItems(dataManager.getDeck());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userFileChooser = new FileChooser();
        userFileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("덱 파일 (*.dek)", "*.dek"),
                new FileChooser.ExtensionFilter("모든 파일 (*.*)", "*.*")
        );
        userFileChooser.setSelectedExtensionFilter(userFileChooser.getExtensionFilters().get(0));
        masterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            cardView.setImage(ImageCache.getImage(newValue.getImageUrl()));
        });
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            cardView.setImage(ImageCache.getImage(newValue.getImageUrl()));
        });
    }

    @FXML
    private void openDeck() throws IOException {
        File file = userFileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        dataManager.readDeck(file);
    }

    @FXML
    private void boardTest() throws IOException {
        Stage boardStage = new Stage();
        boardStage.initOwner(stage);
        boardStage.initModality(Modality.WINDOW_MODAL);
        boardStage.initStyle(StageStyle.UTILITY);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bupjae/tcg/chaostcg/Board.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        boardStage.setScene(scene);
        boardStage.setMaximized(true);
        boardStage.show();
    }
}
