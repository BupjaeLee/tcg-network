/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common.data;

import bupjae.tcg.common.MainController;
import bupjae.tcg.common.proto.CardInfo;
import bupjae.tcg.common.proto.CardInfoList;
import bupjae.tcg.common.proto.Deck;
import bupjae.tcg.common.proto.GameObject;
import com.google.protobuf.ExtensionRegistry;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

/**
 *
 * @author Bupjae
 */
public class DataManager {

    private static final ExtensionRegistry registry;
    private final ObservableList<CardInfo> master;
    private final Map<String, CardInfo> masterLookup;
    private final DeckWrapper deck;

    static {
        ExtensionRegistry r = ExtensionRegistry.newInstance();
        bupjae.tcg.common.proto.Proto.registerAllExtensions(r);
        bupjae.tcg.chaostcg.proto.Proto.registerAllExtensions(r);
        registry = r.getUnmodifiable();
    }

    public DataManager(String... resources) {
        ObservableList<CardInfo> theMaster = FXCollections.observableArrayList();
        Map<String, CardInfo> theLookup = new HashMap<>();
        for (String r : resources) {
            try {
                CardInfoList s = readResource(r);
                theMaster.addAll(s.getInfoList());
                s.getInfoList().stream().forEach(c -> {
                    theLookup.put(c.getSerial(), c);
                });
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
        this.master = FXCollections.unmodifiableObservableList(theMaster);
        this.masterLookup = Collections.unmodifiableMap(theLookup);
        this.deck = new DeckWrapper();
    }

    public CardInfo lookup(String id) {
        return masterLookup.get(id);
    }

    private static CardInfoList readResource(String resource) throws IOException {
        try (InputStream is = MainController.class.getResourceAsStream(resource);
                Reader r = new InputStreamReader(is, "UTF-8")) {
            CardInfoList.Builder builder = CardInfoList.newBuilder();
            com.google.protobuf.TextFormat.merge(r, registry, builder);
            return builder.build();
        }
    }

    public static ExtensionRegistry getRegistry() {
        return registry;
    }

    public void readDeck(File file) throws IOException {
        try (FileInputStream fin = new FileInputStream(file)) {
            deck.setDeck(Deck.newBuilder().mergeFrom(fin, registry));
        }
    }

    public ObservableList<CardInfo> getMaster() {
        return master;
    }

    public DeckWrapper getDeck() {
        return deck;
    }

    public List<GameObject> unpackDeck() {
        GameObject[] first = new GameObject[1];
        List<GameObject> ret = new ArrayList<>();
        deck.deck.getEntryList().stream().forEach(e -> {
            for (int i = 0; i < e.getQty(); i++) {
                GameObject o = GameObject.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setInfo(lookup(e.getSerial()))
                        .build();
                if (first[0] == null && e.getSerial().equals(deck.deck.getFirstCard())) {
                    first[0] = o;
                } else {
                    ret.add(o);
                }
            }
        });
        Collections.shuffle(ret);
        if (first[0] != null) {
            ret.add(0, first[0]);
        }
        return ret;
    }

    public class DeckWrapper extends ModifiableObservableListBase<DeckWrapper.EntryWrapper> {

        private final Deck.Builder deck = Deck.newBuilder();
        private final List<EntryWrapper> entries = new ArrayList<>();
        private final StringProperty deckFirstCardProperty;

        private DeckWrapper() {
            try {
                deckFirstCardProperty = new JavaBeanStringPropertyBuilder()
                        .bean(deck)
                        .beanClass(Deck.Builder.class)
                        .name("firstCard")
                        .build();
            } catch (NoSuchMethodException ex) {
                throw new AssertionError(ex);
            }
        }

        private void setDeck(Deck.Builder deck) {
            clear();
            this.deck.clear().setLoader(deck.getLoader());
            deckFirstCardProperty.set(deck.getFirstCard());
            deck.getEntryBuilderList().stream().forEachOrdered(e -> add(new EntryWrapper(e)));
        }

        public Deck getProto() {
            return deck.build();
        }

        @Override
        public EntryWrapper get(int index) {
            return entries.get(index);
        }

        @Override
        public int size() {
            return entries.size();
        }

        @Override
        protected void doAdd(int index, EntryWrapper element) {
            deck.addEntry(index, element.entry);
            entries.add(index, element);
        }

        @Override
        protected EntryWrapper doSet(int index, EntryWrapper element) {
            deck.setEntry(index, element.entry);
            return entries.set(index, element);
        }

        @Override
        protected EntryWrapper doRemove(int index) {
            deck.removeEntry(index);
            return entries.remove(index);
        }

        public class EntryWrapper {

            private final Deck.Entry.Builder entry;
            private final IntegerProperty qtyProperty;
            private final BooleanProperty firstCardProperty;

            private EntryWrapper(Deck.Entry.Builder entry) {
                this.entry = entry;
                try {
                    qtyProperty = new JavaBeanIntegerPropertyBuilder()
                            .bean(entry)
                            .name("qty")
                            .build();
                } catch (NoSuchMethodException ex) {
                    throw new AssertionError(ex);
                }
                firstCardProperty = new BooleanPropertyBase() {
                    {
                        bind(Bindings.equal(entry.getSerial(), deckFirstCardProperty));
                    }

                    @Override
                    public void set(boolean newValue) {
                        String deckSerial = deckFirstCardProperty.get();
                        String mySerial = entry.getSerial();
                        if (newValue && !deckSerial.equals(mySerial)) {
                            deckFirstCardProperty.set(mySerial);
                        }
                        if (!newValue && deckSerial.equals(mySerial)) {
                            deckFirstCardProperty.set("");
                        }
                    }

                    @Override
                    public Object getBean() {
                        return EntryWrapper.this;
                    }

                    @Override
                    public String getName() {
                        return "firstCard";
                    }

                };
            }

            public String getSerial() {
                return entry.getSerial();
            }

            public String getName() {
                return masterLookup.get(entry.getSerial()).getName();
            }

            public IntegerProperty qtyProperty() {
                return qtyProperty;
            }

            public BooleanProperty firstCardProperty() {
                return firstCardProperty;
            }

            public String getImageUrl() {
                return masterLookup.get(entry.getSerial()).getImageUrl();
            }
        }
    }
}
