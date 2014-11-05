/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Board;
import bupjae.tcg.common.data.DataManager;
import bupjae.tcg.control.GameObjectListView;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PopOver;

/**
 * FXML Controller class
 *
 * @author Bupjae
 */
public class BoardController implements Initializable {

    @FXML
    private BoardBean myBoard;
    @FXML
    private BorderPane main;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTestData();
    }

    public void setTestData() {
        Board.Builder builder = Board.newBuilder();
        try (InputStream is = BoardController.class.getResourceAsStream("testboard.txt");
                Reader r = new InputStreamReader(is, "UTF-8")) {
            com.google.protobuf.TextFormat.merge(r, DataManager.getRegistry(), builder);
            myBoard.set(builder);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    @FXML
    private void test(ActionEvent ev) {
        GameObjectListView view = new GameObjectListView();
        view.listProperty().set(myBoard.getWaitingRoom());
        PopOver popup = new PopOver(view);
        popup.setArrowSize(10);
        popup.setArrowLocation(PopOver.ArrowLocation.LEFT_BOTTOM);
        popup.show((Node) ev.getTarget());
    }
}
