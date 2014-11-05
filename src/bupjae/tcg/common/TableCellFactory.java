/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

/**
 *
 * @author Bupjae
 */
public class TableCellFactory {

    private TableCellFactory() {
    }
    
    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> forInteger() {
        return TextFieldTableCell.forTableColumn(new IntegerStringConverter());
    }
    
    public static <T> Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>> forBoolean() {
        return CheckBoxTableCell.<T>forTableColumn(null);
    }
}
