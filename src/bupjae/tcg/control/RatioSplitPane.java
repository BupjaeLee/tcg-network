/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.control;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.control.SplitPane;

/**
 *
 * @author Bupjae
 */
public class RatioSplitPane extends SplitPane {

    private boolean toRestore;

    public RatioSplitPane() {
        initialize();
    }

    private void initialize() {
        widthProperty().addListener(this::preserveDividerPosition);
        heightProperty().addListener(this::preserveDividerPosition);
    }

    private void preserveDividerPosition(Observable o) {
        if (toRestore) {
            return;
        }
        double[] backupDividerPosition = getDividerPositions();
        toRestore = true;
        Platform.runLater(() -> {
            setDividerPositions(backupDividerPosition);
            toRestore = false;
        });
    }
}
