/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Board;
import bupjae.tcg.common.data.DataManager;
import bupjae.tcg.common.proto.LogMessages;
import bupjae.tcg.control.GameObjectListView;
import com.google.protobuf.ExtensionRegistry;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.controlsfx.control.PopOver;

/**
 * FXML Controller class
 *
 * @author Bupjae
 */
public class BoardController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private BoardBean myBoard;
    @FXML
    private WebView logView;
    private WebEngine logWebEngine;

    private final ObjectProperty<JSObject> window = new SimpleObjectProperty<>(this, "window");
    private final ObjectProperty<DataManager> dataManager = new SimpleObjectProperty<>(this, "dataManager");
    private final ObjectProperty<ExtensionRegistry> registry = new SimpleObjectProperty<>(this, "registry");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        registry.addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                setTestData();
            }
        });
        dataManager.addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                registry.set(newValue.getRegistry());
            } else {
                registry.set(null);
            }
        });
        dataManager.bind(Bindings.createObjectBinding(
                () -> (DataManager) root.getProperties().get(DataManager.CURRENT_DATA_MANAGER_KEY),
                root.getProperties()));

        window.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            LogMessages.Builder builder = LogMessages.newBuilder();
            try (InputStream is = BoardController.class.getResourceAsStream("testlog.txt");
                    Reader r = new InputStreamReader(is, "UTF-8")) {
                com.google.protobuf.TextFormat.merge(r, builder);
                newValue.call("addLogMessages", builder);
            } catch (Exception ex) {
                System.err.println(ex);
            }
        });

        logWebEngine = logView.getEngine();
        logWebEngine.setOnAlert(ev -> System.err.println("alert :: " + ev.getData()));
        logWebEngine.getLoadWorker().stateProperty().addListener((o, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                window.set((JSObject) logWebEngine.executeScript("window"));
            }
        });
        logWebEngine.load(BoardController.class.getResource("log.html").toString());
    }

    public void setTestData() {
        Board.Builder builder = Board.newBuilder();
        try (InputStream is = BoardController.class.getResourceAsStream("testboard.txt");
                Reader r = new InputStreamReader(is, "UTF-8")) {
            com.google.protobuf.TextFormat.merge(r, registry.get(), builder);
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
