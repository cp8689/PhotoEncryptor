package com.example.helloworld;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public final class ControllerHelper {
    private static FXMLLoader fxmlLoader;

    private ControllerHelper() {
        // Empty constructor
    }

    public static <T> T getLoadedFxmlController() {
        return fxmlLoader.getController();
    }

    public static void setFxmlLoader(String resourcePath) {
        fxmlLoader = new FXMLLoader(ControllerHelper.class.getResource(resourcePath));
    }

    public static FXMLLoader getFXMLLoader() {
        return fxmlLoader;
    }

    public static <T> T loadFxml() throws Exception {
        T result = null;

        try {
            result = fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new Exception(exception.getMessage());
        }

        return result;
    }
}
