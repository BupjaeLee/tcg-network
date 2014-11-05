/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import static javafx.beans.binding.Bindings.*;

import bupjae.tcg.common.ImageCache;
import bupjae.tcg.common.proto.GameObject;
import bupjae.tcg.control.GameObjectTooltip;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author Bupjae
 */
public class HandPane extends FlowPane {

    private static final double RATIO = 8.0 / 11.0;
    private static final int EXPECTED_HAND = 8;
    private final ListProperty<GameObject> hand = new SimpleListProperty<>(this, "hand", FXCollections.observableArrayList());

    private final NumberBinding contentHeight = createDoubleBinding(() -> getHeight() - getInsets().getTop() - getInsets().getBottom(), heightProperty(), insetsProperty());
    private final NumberBinding contentWidth = createDoubleBinding(() -> getWidth() - getInsets().getLeft() - getInsets().getRight(), widthProperty(), insetsProperty());
    private final NumberBinding imageHeight = createDoubleBinding(() -> Math.ceil(min(contentHeight, contentWidth.divide(RATIO).divide(EXPECTED_HAND)).doubleValue()), contentHeight, contentWidth);
    private final NumberBinding imageWidth = createDoubleBinding(() -> Math.ceil(min(contentHeight.multiply(RATIO), contentWidth.divide(EXPECTED_HAND)).doubleValue()), contentHeight, contentWidth);

    public HandPane() {
        hand.sizeProperty().addListener(o -> ensureHand());
        setSnapToPixel(false);
        setMinHeight(0);
        setAlignment(Pos.CENTER);
        hgapProperty().bind(createDoubleBinding(this::calculateHgap, hand.sizeProperty(), heightProperty(), widthProperty(), insetsProperty()));
    }

    private double calculateHgap() {
        int size = hand.getSize();
        Insets insets = getInsets();
        return Math.min(Math.floor((getWidth() - insets.getLeft() - insets.getRight() - size * imageWidth.doubleValue()) / (size - 1)), 5);
    }

    private void ensureHand() {
        while (getChildren().size() < hand.size()) {
            ImageView handView = addImageView();
            ObjectBinding<GameObject> t = valueAt(hand, getChildren().size() - 1);
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
