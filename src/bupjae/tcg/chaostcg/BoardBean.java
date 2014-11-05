/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.chaostcg;

import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Board;
import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.BoardOrBuilder;
import bupjae.tcg.chaostcg.proto.ChaosTcgGameModel.Character;
import bupjae.tcg.common.proto.GameObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Bupjae
 */
public final class BoardBean {

    private final ObservableList<GameObject> mainDeck = FXCollections.observableArrayList();
    private final ObservableList<GameObject> extraDeck = FXCollections.observableArrayList();
    private final ObservableList<GameObject> extraDeckFaceUp = FXCollections.observableArrayList();
    private final ObservableList<GameObject> hand = FXCollections.observableArrayList();
    private final ObservableList<GameObject> waitingRoom = FXCollections.observableArrayList();
    private final ObservableList<GameObject> backyard = FXCollections.observableArrayList();
    private final ReadOnlyObjectProperty<CharacterBean> partner = new ReadOnlyObjectPropertyBase<CharacterBean>() {
        private CharacterBean value = new CharacterBean();

        @Override
        public CharacterBean get() {
            return value;
        }

        @Override
        public Object getBean() {
            return BoardBean.this;
        }

        @Override
        public String getName() {
            return "partner";
        }
    };

    private final ObservableList<CharacterBean> friend = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(
            new CharacterBean(), new CharacterBean(), new CharacterBean(), new CharacterBean()));

    public BoardBean() {
        workaroundInit();
    }

    public BoardBean(BoardOrBuilder b) {
        this();
        set(b);
    }

    public void set(BoardOrBuilder b) {
        mainDeck.setAll(b.getMainDeckList());
        extraDeck.setAll(b.getExtraDeckList());
        extraDeckFaceUp.setAll(b.getExtraDeckFaceUpList());
        hand.setAll(b.getHandList());
        waitingRoom.setAll(b.getWaitingRoomList());
        backyard.setAll(b.getBackyardList());
        partner.get().set(b.getPartner());
        for (int i = 0; i < 4; i++) {
            if (b.getFriendCount() <= i) {
                friend.get(i).set(Character.getDefaultInstance());
            } else {
                friend.get(i).set(b.getFriendOrBuilder(i));
            }
        }
    }

    public Board.Builder toBuilder() {
        Board.Builder builder = Board.newBuilder()
                .addAllMainDeck(mainDeck)
                .addAllExtraDeck(extraDeck)
                .addAllExtraDeckFaceUp(extraDeckFaceUp)
                .addAllHand(hand)
                .addAllWaitingRoom(waitingRoom)
                .addAllBackyard(backyard)
                .setPartner(partner.get().toBuilder());
        friend.stream().map(f -> f.toBuilder()).forEachOrdered(f -> builder.addFriend(f));
        return builder;
    }

    public ObservableList<GameObject> getMainDeck() {
        return mainDeck;
    }

    public ObservableList<GameObject> getExtraDeck() {
        return extraDeck;
    }

    public ObservableList<GameObject> getExtraDeckFaceUp() {
        return extraDeckFaceUp;
    }

    public ObservableList<GameObject> getHand() {
        return hand;
    }

    public ObservableList<GameObject> getWaitingRoom() {
        return waitingRoom;
    }

    public ObservableList<GameObject> getBackyard() {
        return backyard;
    }

    public CharacterBean getPartner() {
        return partner.get();
    }

    public ReadOnlyObjectProperty<CharacterBean> partnerBean() {
        return partner;
    }

    public ObservableList<CharacterBean> getFriend() {
        return friend;
    }

    // TODO: Remove these workarounds when FXML supports "size" operation of given collection
    private final IntegerProperty mainDeckSize = new SimpleIntegerProperty(this, "mainDeckSize");
    private final IntegerProperty extraDeckSize = new SimpleIntegerProperty(this, "extraDeckSize");
    private final IntegerProperty extraDeckFaceUpSize = new SimpleIntegerProperty(this, "extraDeckFaceUpSize");
    private final IntegerProperty handSize = new SimpleIntegerProperty(this, "handSize");
    private final IntegerProperty waitingRoomSize = new SimpleIntegerProperty(this, "waitingRoomSize");
    private final IntegerProperty backyardSize = new SimpleIntegerProperty(this, "backyardSize");

    private void workaroundInit() {
        mainDeckSize.bind(Bindings.size(mainDeck));
        extraDeckSize.bind(Bindings.size(extraDeck));
        extraDeckFaceUpSize.bind(Bindings.size(extraDeckFaceUp));
        handSize.bind(Bindings.size(hand));
        waitingRoomSize.bind(Bindings.size(waitingRoom));
        backyardSize.bind(Bindings.size(backyard));
    }

    public int getMainDeckSize() {
        return mainDeckSize.get();
    }

    public ReadOnlyIntegerProperty mainDeckSizeProperty() {
        return mainDeckSize;
    }

    public int getExtraDeckSize() {
        return extraDeckSize.get();
    }

    public ReadOnlyIntegerProperty extraDeckSizeProperty() {
        return extraDeckSize;
    }

    public int getExtraDeckFaceUpSize() {
        return extraDeckFaceUpSize.get();
    }

    public ReadOnlyIntegerProperty extraDeckFaceUpSizeProperty() {
        return extraDeckFaceUpSize;
    }

    public int getHandSize() {
        return handSize.get();
    }

    public ReadOnlyIntegerProperty handSizeProperty() {
        return handSize;
    }

    public int getWaitingRoomSize() {
        return waitingRoomSize.get();
    }

    public ReadOnlyIntegerProperty waitingRoomSizeProperty() {
        return waitingRoomSize;
    }

    public int getBackyardSize() {
        return backyardSize.get();
    }

    public ReadOnlyIntegerProperty backyardSizeProperty() {
        return backyardSize;
    }
}
