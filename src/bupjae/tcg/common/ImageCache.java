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
import javafx.beans.binding.Binding;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import org.fxmisc.easybind.EasyBind;

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

    public static Binding<Image> bind(ObservableValue<?> v) {
        return EasyBind.map(v, obj -> {
            String url;
            if (obj instanceof GameObject) {
                url = ((GameObject) obj).getInfo().getImageUrl();
            } else if (obj instanceof CardInfo) {
                url = ((CardInfo) obj).getImageUrl();
            } else {
                url = (obj == null ? "" : obj.toString());
            }
            return getImage(url);
        });
    }
}
