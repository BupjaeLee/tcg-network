/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.control;

import bupjae.tcg.common.proto.GameObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

/**
 *
 * @author Bupjae
 */
public class GameObjectListView extends StackPane {

    private final ListProperty<GameObject> list = new SimpleListProperty<>(this, "list");
    private final ListView<GameObject> view = new ListView<>();

    public GameObjectListView() {
        Bindings.bindBidirectional(list, view.itemsProperty());
        view.setCellFactory(TextFieldListCell.forListView(new StringConverter<GameObject>() {

            @Override
            public String toString(GameObject object) {
                return object.getInfo().getName();
            }

            @Override
            public GameObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported");
            }
        }));
        view.setFixedCellSize(24);
        view.setPrefWidth(500);
        view.prefHeightProperty().bind(view.fixedCellSizeProperty().multiply(Bindings.min(list.sizeProperty().add(1), 10)));
        view.focusModelProperty().get().focusedItemProperty().addListener((bean, oldValue, newValue) -> GameObjectTooltip.getInstance().setGameObject(newValue));
        setPadding(new Insets(15));
        getChildren().add(view);
    }

    public ObservableList<GameObject> getList() {
        return list.get();
    }

    public void setList(ObservableList<GameObject> list) {
        this.list.set(list);
    }

    public ListProperty<GameObject> listProperty() {
        return list;
    }
}
