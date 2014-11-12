/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common;

import bupjae.tcg.common.data.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Bupjae
 */
public class TcgNetworks extends Application {

    private DataManager dataManager;

    @Override
    public void init() throws Exception {
        dataManager = new DataManager.Builder()
                .registerExtension(bupjae.tcg.chaostcg.proto.Proto::registerAllExtensions)
                .load("/bupjae/tcg/chaostcg/proto/IMT.txt")
                .build();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        root.getProperties().put(DataManager.CURRENT_DATA_MANAGER_KEY, dataManager);
        stage.setMaximized(true);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
