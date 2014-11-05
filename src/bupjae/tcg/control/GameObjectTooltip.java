/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.control;

import bupjae.tcg.common.ImageCache;
import bupjae.tcg.common.proto.GameObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

/**
 *
 * @author Bupjae
 */
public class GameObjectTooltip extends Tooltip {

    public static final String KEY_GAME_OBJECT = "bupjae.tcg.control.GAME_OBJECT";
    private static final GameObjectTooltip INSTANCE = new GameObjectTooltip();

    private final ObjectProperty<GameObject> gameObject = new SimpleObjectProperty<>(this, "gameObject", GameObject.getDefaultInstance());
    private final ReadOnlyObjectProperty<Image> image = new ReadOnlyObjectPropertyBase<Image>() {
        {
            gameObject.addListener(o -> fireValueChangedEvent());
        }

        @Override
        public Image get() {
            String url = gameObject.get().getInfo().getImageUrl().trim();
            return (url == null || url.isEmpty()) ? null : ImageCache.getImage(url);
        }

        @Override
        public Object getBean() {
            return GameObjectTooltip.this;
        }

        @Override
        public String getName() {
            return "image";
        }
    };

    private GameObjectTooltip() {
        setOnShowing(e -> {
            if (!(getStyleableParent() instanceof Node)) {
                return;
            }
            Node node = (Node) getStyleableParent();
            if (!(node.getProperties().get(KEY_GAME_OBJECT) instanceof GameObject)) {
                return;
            }
            gameObject.set((GameObject) node.getProperties().get(KEY_GAME_OBJECT));
        });
        this.textProperty().bind(Bindings.selectString(gameObject, "info", "name"));
    }

    public GameObject getGameObject() {
        return gameObject.get();
    }

    public void setGameObject(GameObject g) {
        gameObject.set(g);
    }

    public ObjectProperty<GameObject> gameObjectProperty() {
        return gameObject;
    }

    public static GameObjectTooltip getInstance() {
        return INSTANCE;
    }

    public Image getImage() {
        return image.get();
    }

    public ReadOnlyObjectProperty<Image> imageProperty() {
        return image;
    }
}
