/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common;

import bupjae.tcg.common.proto.CardInfo;
import bupjae.tcg.common.proto.GameObject;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

/**
 *
 * @author Bupjae
 */
public class ImageCache {

    private static final Map<String, SoftReference<Image>> cache = new HashMap<>();

    private ImageCache() {
    }

    public static Image getImage(String url) {
        Image ret = null;
        SoftReference<Image> img = cache.get(url);
        if (img != null) {
            ret = img.get();
        }
        if (ret == null) {
            ret = new Image(url, true);
            cache.put(url, new SoftReference<>(ret));
        }
        return ret;
    }

    public static ObjectBinding<Image> bind(ObservableValue<?> v) {
        return Bindings.createObjectBinding(() -> {
            Object obj = v.getValue();
            String url;
            if (obj instanceof GameObject) {
                url = ((GameObject) obj).getInfo().getImageUrl();
            } else if (obj instanceof CardInfo) {
                url = ((CardInfo) obj).getImageUrl();
            } else {
                url = (obj == null ? "" : obj.toString());
            }
            return getImage(url);
        }, v);
    }
}
