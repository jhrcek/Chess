/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jhrcek
 */
public interface Tree<T> {

    Node<T> getRoot();

    public static interface Node<T> {

        public T getData();

        public void setData(T data);

        public List<T> getChildren();
    }
}