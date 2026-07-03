package com.hotel;

/**
 * Standard Launcher class to sidestep JavaFX modular runtime issues
 * when running outside of module-aware execution environments.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
