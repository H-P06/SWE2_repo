package com.example.javafxtest;

public class Launcher {
    public static void main(String[] args) {
        // This calls the main method in your App class.
        // This is the "trick" that allows JavaFX to run from a JAR.
        application.main(args);
    }
}