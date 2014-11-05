/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import static javafx.beans.binding.Bindings.*;

import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel;
import bupjae.tcg.common.ImageCache;
import bupjae.tcg.common.proto.GameObject;
import bupjae.tcg.control.GameObjectTooltip;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 *
 * @author Bupjae
 */
public class CharacterPane extends Pane {

    private static final double RATIO = 7.0 / 12.0;
    private static final String DOWNFACE_URL = CharacterPane.class.getResource("backface.png").toString();

    private final ObjectProperty<CharacterBean> character = new SimpleObjectProperty<>(this, "character", new CharacterBean());

    private final NumberBinding effSize = min(widthProperty(), heightProperty().multiply(RATIO));
    private final NumberBinding deltaY = effSize.divide(12.0);
    private final NumberBinding midX2 = createDoubleBinding(() -> getLayoutBounds().getWidth() + getInsets().getLeft() - getInsets().getRight(), layoutBoundsProperty(), insetsProperty());

    private final ListBinding<GameObject> modifierImage = new ListBinding<GameObject>() {
        private ObservableList<GameObject> l;
        private ObservableList<GameObject> s;

        {
            character.addListener(o -> rebind());
            rebind();
        }

        private void rebind() {
            if (l != null) {
                unbind(l);
            }
            if (s != null) {
                unbind(s);
            }
            bind(l = character.get().getLevel(), s = character.get().getSet());
            invalidate();
        }

        @Override
        protected ObservableList<GameObject> computeValue() {
            return Stream.concat(l.stream(), s.stream())
                    .collect(Collectors.collectingAndThen(
                                    Collectors.toCollection(FXCollections::observableArrayList),
                                    FXCollections::unmodifiableObservableList));
        }
    };

    public CharacterPane() {
        ImageView characterImage = addImageView();
        characterImage.rotateProperty().bind(new DoubleBinding() {
            private final ObjectBinding<ChaosTcgGameModel.Position> position;

            {
                bind(position = select(character, "position"));
            }

            @Override
            protected double computeValue() {
                ChaosTcgGameModel.Position p = position.get();
                if (p == null) {
                    return 0;
                }
                switch (p) {
                    case REST:
                        return 90;
                    case REVERSE:
                        return 180;
                    default:
                        return 0;
                }
            }
        });
        characterImage.imageProperty().bind(new ObjectBinding<Image>() {
            private ObjectBinding<GameObject> t;
            private ObjectBinding<ChaosTcgGameModel.Face> f;

            {
                character.addListener(o -> rebind());
                rebind();
            }

            private void rebind() {
                if (t != null) {
                    unbind(t);
                }
                if (f != null) {
                    unbind(f);
                }
                bind(t = valueAt(character.get().getCharacter(), 0), f = select(character, "face"));
                invalidate();
            }

            @Override
            protected Image computeValue() {
                GameObject o = t.get();
                if (o == null) {
                    characterImage.getProperties().put(GameObjectTooltip.KEY_GAME_OBJECT, GameObject.getDefaultInstance());
                    return null;
                }
                characterImage.getProperties().put(GameObjectTooltip.KEY_GAME_OBJECT, o);
                return ImageCache.getImage(f.get() == ChaosTcgGameModel.Face.UP ? o.getInfo().getImageUrl() : DOWNFACE_URL);
            }
        });
        modifierImage.sizeProperty().addListener(o -> {
            while (getChildren().size() <= modifierImage.size()) {
                ImageView modifierView = addImageView();
                ObjectBinding<GameObject> t = valueAt(modifierImage, getChildren().size() - 2);
                modifierView.imageProperty().bind(ImageCache.bind(t));
                t.addListener(oo -> modifierView.getProperties().put(GameObjectTooltip.KEY_GAME_OBJECT, t.get()));
            }
        });
    }

    private ImageView addImageView() {
        ImageView ret = new ImageView();
        ret.setPreserveRatio(true);
        ret.fitWidthProperty().bind(effSize);
        ret.fitHeightProperty().bind(effSize);
        ret.layoutXProperty().bind(midX2.subtract(selectDouble(ret.layoutBoundsProperty(), "width")).divide(2));
        ret.layoutYProperty().bind(deltaY.multiply(getChildren().size()).subtract(selectDouble(ret.layoutBoundsProperty(), "minY")));
        Tooltip.install(ret, GameObjectTooltip.getInstance());
        getChildren().add(0, ret);
        return ret;
    }

    public ObjectProperty<CharacterBean> characterProperty() {
        return character;
    }

    public void setCharacter(CharacterBean character) {
        this.character.set(character);
    }

    public CharacterBean getCharacter() {
        return character.get();
    }

    // TODO: remove this when RT-37389 is patched. (will be patched at 8u40)
    private static <E> ObjectBinding<E> valueAt(final ObservableList<E> op, final int index) {
        if (op == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }

        return new ObjectBinding<E>() {
            {
                super.bind(op);
            }

            @Override
            public void dispose() {
                super.unbind(op);
            }

            @Override
            protected E computeValue() {
                try {
                    return op.get(index);
                } catch (IndexOutOfBoundsException ex) {
                }
                return null;
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(op);
            }
        };
    }
}
