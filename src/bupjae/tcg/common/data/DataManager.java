/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common.data;

import bupjae.tcg.common.proto.CardInfo;
import bupjae.tcg.common.proto.CardInfoList;
import bupjae.tcg.common.proto.Deck;
import bupjae.tcg.common.proto.GameObject;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
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

    public static final Object CURRENT_DATA_MANAGER_KEY = new Object();

    private final ExtensionRegistry registry;
    private final ObservableList<CardInfo> master;
    private final Map<String, CardInfo> masterLookup;
    private final DeckWrapper deck;

    public static class Builder {

        private final ExtensionRegistry registry = ExtensionRegistry.newInstance();
        private final ObservableList<CardInfo> master = FXCollections.observableArrayList();
        private final Map<String, CardInfo> masterLookup = new HashMap<>();

        public Builder registerExtension(Consumer<ExtensionRegistry> callback) {
            callback.accept(registry);
            return this;
        }

        public Builder load(String resource) {
            try {
                CardInfoList.Builder s = readTextInternal(registry, DataManager.class.getResource(resource), CardInfoList.newBuilder());
                master.addAll(s.getInfoList());
                s.getInfoList().stream().forEach(c -> {
                    masterLookup.put(c.getSerial(), c);
                });
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return this;
        }

        public DataManager build() {
            return new DataManager(
                    registry.getUnmodifiable(),
                    FXCollections.unmodifiableObservableList(master),
                    Collections.unmodifiableMap(masterLookup));
        }
    }

    private DataManager(ExtensionRegistry registry, ObservableList<CardInfo> master, Map<String, CardInfo> masterLookup) {
        this.registry = registry;
        this.master = master;
        this.masterLookup = masterLookup;
        this.deck = new DeckWrapper();
    }

    public CardInfo lookup(String id) {
        return masterLookup.get(id);
    }

    private static <T extends Message.Builder> T readTextInternal(ExtensionRegistry registry, URL url, T builder) throws IOException {
        try (InputStream is = url.openStream();
                Reader r = new InputStreamReader(is, "UTF-8")) {
            com.google.protobuf.TextFormat.merge(r, registry, builder);
            return builder;
        }
    }

    private static <T extends Message.Builder> T readBinaryInternal(ExtensionRegistry registry, URL url, T builder) throws IOException {
        try (InputStream is = url.openStream()) {
            return (T) builder.mergeFrom(is, registry);
        }
    }

    public void readDeck(File file) throws IOException {
        deck.setDeck(readBinaryInternal(registry, file.toURI().toURL(), Deck.newBuilder()));
    }

    public ObservableList<CardInfo> getMaster() {
        return master;
    }

    public DeckWrapper getDeck() {
        return deck;
    }
    
    public <T extends Message.Builder> T readTestData(URL url, T builder) {
        try {
            return readTextInternal(registry, url, builder);
        } catch(IOException ex) {
            throw new AssertionError("Reading test data failed", ex);
        }
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
