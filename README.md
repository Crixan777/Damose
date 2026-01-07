 # Damose  Sistema di Visualizzazione e Ricerca GTFS (Roma)

Damose è un’applicazione desktop sviluppata in Java per la **visualizzazione, ricerca e gestione dei dati GTFS** (General Transit Feed Specification) relativi al trasporto pubblico di Roma.

Il progetto è stato realizzato con un’architettura **MVC (Model-View-Controller)** e consente di esplorare fermate e linee su mappa, utilizzare una modalità online/offline e visualizzare aggiornamenti in tempo reale.

---

## Features

- Visualizzazione delle fermate su mappa con icone personalizzate
- Ricerca dinamica di fermate e linee
- Visualizzazione del percorso delle linee (andata e ritorno)
- Gestione dei preferiti (fermate e linee)
- Modalità offline / online
- Aggiornamenti in tempo reale (GTFS Realtime)
- Interfaccia grafica modulare e dinamica

---

## Technologies

- **Java**
- **Swing** (GUI)
- **JXMapViewer** (visualizzazione mappa)
- **GTFS / GTFS-Realtime**
- **Maven / librerie esterne**

---

## Architecture

Il progetto segue il pattern **MVC (Model-View-Controller)**:

- **Model**: gestione dei dati GTFS (Stop, Route, Trip, Shape, ecc.)
- **View**: interfaccia grafica Swing (pannelli, mappa, finestre)
- **Controller**: logica applicativa e gestione degli eventi

Questa separazione garantisce modularità, manutenibilità e scalabilità del sistema.

---

## Project Structure

```
org.damose
├── model        # Modelli GTFS
├── view         # Interfaccia grafica
├── controller   # Logica applicativa
└── Main         # Entry point dell'applicazione
```

---

## How to Run

1. Importare il progetto in **IntelliJ IDEA**
2. Scaricare il file "rome_static_gtfs.zip" da https://romamobilita.it/sistemi-e-tecnologie/open-data/ 
3. Unzzipare il file e mettere "stop_times.txt" in "src/main/resources/gtfsStatici"
3. Verificare che tutte le librerie richieste siano correttamente incluse
4. Avviare la classe `Main`

---

## Author

**Dumitru Gabriel Cristian**  \
Università – Sapienza Roma

---

## Notes

Questo progetto è stato sviluppato a scopo didattico e rappresenta un esempio di applicazione desktop Java basata su dati GTFS, con particolare attenzione ai principi di **Object-Oriented Programming** e ai **design pattern architetturali**.


