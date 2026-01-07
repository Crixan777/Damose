package org.damose;

import org.damose.controller.GestoreMode;
import org.damose.controller.GestoreRealTime;
import org.damose.view.MyFrame;

public class Main {
    public static void main(String[] args) {
        // Avvia l'interfaccia principale
        new MyFrame();

        // Avvia il thread che gestisce la modalità online/offline
        GestoreMode modeThread = new GestoreMode();
        modeThread.start();

        // Avvia l'aggiornamento in tempo reale dei dati
        GestoreRealTime.startAggiornamento();
    }
}