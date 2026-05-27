package orbitsim.app;

import orbitsim.exception.OrbitSimException;
import orbitsim.mission.AscentPhase;
import orbitsim.mission.LaunchPhase;
import orbitsim.mission.MissionPhase;
import orbitsim.mission.OrbitalPhase;
import orbitsim.spacecraft.Spacecraft;
import orbitsim.util.LogManager;

import java.util.Scanner;
import java.util.logging.Logger;

import static java.lang.Character.toUpperCase;
import static java.lang.Thread.sleep;

/**
 * CLI interattivo del Mission Control per HORUS-21.
 * Integra tutti i pattern: Strategy (fasi), Observer (alert),
 * Chain (anomalie), Memento (snapshot), Factory (anomalie),
 * Composite (sistemi), Iterator (scan).
 */



public class MissionCLI {

    static boolean missionRunning = false;
    //banner iniziale
    private static void printBanner() {
        System.out.println("""
                
                ╔═══════════════════════════════════════════════════╗
                ║         HORUS-21 MISSION CONTROL SYSTEM           ║
                ║         OrbitSim v1.0 — Java SE Edition           ║
                ╠═══════════════════════════════════════════════════╣
                ║                      test                         ║
                ╚═══════════════════════════════════════════════════╝
                
                  Type LAUNCH to begin mission sequence.
                  Type HELP for available commands.
                """);
    }


    public static void main (String[]args) throws InterruptedException {
          System.out.print("System loading");
          //simulazione loading
          for (int i =0;i < 10; i++){
              System.out.print(" > ");
              sleep(200);
          }
          printBanner();


        //init logger
        LogManager log = new LogManager("C:/Users/THINKPAD P17 G2/Desktop/logMission/mission.log");

        //inizializiamo scanner input utente
          Scanner scanner1 = new Scanner(System.in);


          while (true) {
              String command = scanner1.nextLine().toUpperCase();
              try {
                  switch (command) {
                      case "LAUNCH":
                          launch(log);
                          break;
                      case "STATUS":
                          status();
                          break;
                      case "TELEMETRY":
                          telemetry();
                          break;
                      case "SYSTEMS":
                          systems();
                          break;
                      case "MANEUVER":
                          maneuver();
                          break;
                      case "SNAPSHOT":
                          snapshot();
                          break;
                      case "SNAPSHOTS":
                          listSnapshots();
                          break;
                      case "RESTORE":
                          restore();
                          break;
                      case "SCAN":
                          scan();
                          break;
                      case "INJECT_ANOMALY":
                          injectAnomaly();
                          break;
                      case "REENTRY":
                          reentry();
                          break;
                      case "REPORT":
                          report();
                          break;
                      case "ABORT":
                          abort();
                          break;
                      case "HELP":
                          help();
                          break;
                      case "EXIT":
                          exit(log);
                          return;

                      default:
                          throw new IllegalStateException("Unexpected value: " + command);

                  }
              }   catch (IllegalStateException e){
                  System.out.println("Error: "+ e.getMessage());
                  System.out.println("Try to enter a new valid command");


              }
              //scanner1.close();
          }



    }

    private static void launch(LogManager log) throws OrbitSimException {

        if (missionRunning) {
            System.out.println("  Mission already in progress.");
            log.appendLogWarn("Mission already in progress");
            return;
        }else{

            System.out.println("LAUNCH SEQUENCE IS STARTED!");

            System.out.println("ALL SYSTEM ARE CHECKED!");
            for (int i = 10;i > 0; i--){
                log.appendLogInfo("countdown "+ i);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            missionRunning = true;
            System.out.println("Liftoff!We are taking off!");
            log.appendLogInfo("Liftoff OK");
        }




    }

    private static void exit(LogManager log){
        log.closeLog();
    }

    private static void abort() {
    }

    private static void report() {
    }

    private static void reentry() {
    }

    private static void injectAnomaly() {
    }

    private static void restore() {
    }

    private static void scan() {
    }

    private static void listSnapshots() {
    }

    private static void snapshot() {
    }

    private static void maneuver() {
    }

    private static void systems() {
    }

    private static void telemetry() {
    }

    private static void status() {
    }


    private static void help() {


    }

    private String commandDesc(String cmd){
        return switch (cmd) {
            case "STATUS" -> "Mission and phase overview";
            case "TELEMETRY" -> "Live sensor readings";
            case "SYSTEMS" -> "Composite system hierarchy";
            case "MANEUVER" -> "Execute orbital maneuver [HOHMANN|REBOOST]";
            case "SNAPSHOT" -> "Save mission state (Memento)";
            case "SNAPSHOTS" -> "List all saved snapshots";
            case "RESTORE" -> "View snapshot [ID]";
            case "SCAN" -> "Iterator scan of all subsystems";
            case "INJECT_ANOMALY" -> "Trigger anomaly [type] — Chain of Responsibility";
            case "REENTRY" -> "Begin reentry sequence";
            case "REPORT" -> "Full mission report + save to file";
            case "ABORT" -> "Emergency mission abort";
            default -> "";
        };
    }


}

