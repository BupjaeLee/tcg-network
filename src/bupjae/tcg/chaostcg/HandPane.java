/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import bupjae.tcg.common.ImageCache;
import bupjae.tcg.common.proto.GameObject;
import bupjae.tcg.control.GameObjectTooltip;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.fxmisc.easybind.EasyBind;

/**
 *
 * @author Bupjae
 */
public class HandPane extends FlowPane {

    private static final double RATIO = 8.0 / 11.0;
    private static final int EXPECTED_HAND = 8;
    private final ListProperty<GameObject> hand = new SimpleListProperty<>(this, "hand", FXCollections.observableArrayList());

    private final Binding<Double> contentHeight = EasyBind.combine(heightProperty(), insetsProperty(), (h, i) -> h.doubleValue() - i.getTop() - i.getBottom());
    private final Binding<Double> contentWidth = EasyBind.combine(widthProperty(), insetsProperty(), (w, i) -> w.doubleValue() - i.getLeft() - i.getRight());
    private final Binding<Double> imageHeight = EasyBind.combine(contentHeight, contentWidth, (h, w) -> Math.ceil(Math.min(h, w / (RATIO * EXPECTED_HAND))));
    private final Binding<Double> imageWidth = EasyBind.combine(contentHeight, contentWidth, (h, w) -> Math.ceil(Math.min(h * RATIO, w / EXPECTED_HAND)));

    public HandPane() {
        hand.sizeProperty().addListener(o -> ensureHand());
        setSnapToPixel(false);
        setMinHeight(0);
        setAlignment(Pos.CENTER);
        hgapProperty().bind(EasyBind.combine(hand.sizeProperty(), contentWidth, imageWidth,
                (n, cw, iw) -> Math.min(Math.floor((cw - n.intValue() * iw) / (n.intValue() - 1)), 5)));
    }

    private void ensureHand() {
        while (getChildren().size() < hand.size()) {
            ImageView handView = addImageView();
            ObjectBinding<GameObject> t = Bindings.valueAt(hand, getChildren().size() - 1);
            handView.imageProperty().bind(ImageCache.bind(t));
            t.addListener(o -> handView.getProperties().put(GameObjectTooltip.KEY_GAME_OBJECT, t.get()));
        }
    }

    private ImageView addImageView() {
        ImageView ret = new ImageView();
        ret.setPreserveRatio(true);
        ret.fitHeightProperty().bind(imageHeight);
        ret.fitWidthProperty().bind(imageWidth);
        getChildren().add(ret);
        Tooltip.install(ret, GameObjectTooltip.getInstance());
        return ret;
    }

    public ObservableList<GameObject> getHand() {
        return hand.get();
    }

    public void setHand(ObservableList<GameObject> hand) {
        this.hand.set(hand);
    }

    public ListProperty<GameObject> handProperty() {
        return hand;
    }
}
