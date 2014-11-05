/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Character;
import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.CharacterOrBuilder;
import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Face;
import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Position;
import bupjae.tcg.common.proto.GameObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Bupjae
 */
public final class CharacterBean {

    private final ObservableList<GameObject> character = FXCollections.observableArrayList();
    private final ObservableList<GameObject> level = FXCollections.observableArrayList();
    private final ObservableList<GameObject> set = FXCollections.observableArrayList();
    private final ObjectProperty<Face> face = new SimpleObjectProperty<>(this, "face", Face.UP);
    private final ObjectProperty<Position> position = new SimpleObjectProperty<>(this, "position", Position.STAND);

    public CharacterBean() {
    }

    public CharacterBean(CharacterOrBuilder c) {
        set(c);
    }

    public void set(CharacterOrBuilder c) {
        character.setAll(c.getCharacterList());
        level.setAll(c.getLevelList());
        set.setAll(c.getSetList());
        face.setValue(c.getFace());
        position.setValue(c.getPosition());
    }

    public Character.Builder toBuilder() {
        return Character.newBuilder()
                .addAllCharacter(character)
                .addAllLevel(level)
                .addAllSet(set)
                .setFace(face.get())
                .setPosition(position.get());
    }

    public ObservableList<GameObject> getCharacter() {
        return character;
    }

    public ObservableList<GameObject> getLevel() {
        return level;
    }

    public ObservableList<GameObject> getSet() {
        return set;
    }

    public Face getFace() {
        return face.get();
    }

    public void setFace(Face face) {
        this.face.set(face);
    }

    public ObjectProperty<Face> faceProperty() {
        return face;
    }

    public Position getPosition() {
        return position.get();
    }

    public void setPosition(Position position) {
        this.position.set(position);
    }

    public ObjectProperty<Position> positionProperty() {
        return position;
    }
}
