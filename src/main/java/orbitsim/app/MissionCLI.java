package orbitsim.app;

//import packages
import orbitsim.mission.*;
import orbitsim.exception.OrbitSimException;
import orbitsim.patterns.chain.*;
import orbitsim.patterns.factory.AnomalyFactory;
import orbitsim.patterns.observer.BlackBoxObserver;
import orbitsim.patterns.observer.ConsoleAlertObserver;
import orbitsim.patterns.observer.MissionEvent;
import orbitsim.patterns.observer.MissionEventBus;
import orbitsim.spacecraft.Spacecraft;
import orbitsim.util.LogManager;

import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * CLI interattivo del Mission Control per HORUS-21.
 * Integra tutti i pattern: Strategy (fasi), Observer (alert),
 * Chain (anomalie), Memento (snapshot), Factory (anomalie),
 * Composite (sistemi), Iterator (scan).
 * @author Zappalà Gianluca
 * @version 1.0
 */



public class MissionCLI {
    //instances
    boolean missionRunning = false;
    private final MissionEventBus eventBus      = new MissionEventBus();
    private final BlackBoxObserver blackBox = new BlackBoxObserver();
    private MissionPhase currentPhase            = null;
    private final Spacecraft spacecraft = new Spacecraft(eventBus);
    //init scanner input utente
    Scanner scanner1 = new Scanner(System.in);



    private long missionStartMs;
    //chain of responsibility
    private AnomalyHandler anomalyPipeline;


    //banner iniziale
    private static void printBanner() {
        System.out.println("""
                
                ╔═══════════════════════════════════════════════════╗
                ║         HORUS-21 MISSION CONTROL SYSTEM           ║
                ║         OrbitSim v1.0 — Java SE Edition           ║
                ╠═══════════════════════════════════════════════════╣
                ║               "PER ASPERA AD ASTRA"               ║
                ╚═══════════════════════════════════════════════════╝
                
                  Type LAUNCH to begin mission sequence.
                  Type HELP for available commands.
                """);
    }

    public static void main(String[] args) {
        new MissionCLI().run();
    }

    private void run() {

        System.out.print("System loading");
        //simulazione loading
        loadingAnim();
        printBanner();

        //init logger
        LogManager log = new LogManager("logs/HORUS-21-mission.log");

        // Registra gli observer all' EventBus
        eventBus.subscribe(new ConsoleAlertObserver());
        eventBus.subscribe(blackBox);

        anomalyPipeline = new DetectionHandler();
        anomalyPipeline.setNext(new AssessmentHandler())
                .setNext(new IsolationHandler())
                .setNext(new NotificationHandler())
                .setNext(new EscalationHandler());

          while (true) {
              System.out.print("\n  MISSION CONTROL> ");

              // 1. Leggi l'input e dividilo in parole usando lo spazio come separatore
              String completeInput = scanner1.nextLine().trim();
              String[] parts = completeInput.split("\\s+"); // Divide la stringa a ogni spazio

              // 2. La prima parola è il comando reale (es: "MANEUVER")
              String command = parts[0].toUpperCase();

              // 3. Creiamo l' array 'args' con le parole successive (es: "REBOOST")
              String[] argsCmd = new String[parts.length - 1];
              System.arraycopy(parts, 1, argsCmd, 0, argsCmd.length);
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
                          maneuver(argsCmd,log);
                          break;
                      case "SCAN":
                          scan(log);
                          break;
                      case "INJECT_ANOMALY":
                          injectAnomaly(argsCmd, log);
                          break;
                      case "REENTRY":
                          reentry(log);
                          break;
                      case "ABORT":
                          abort(log);
                          break;
                      case "HELP":
                          help();
                          break;
                      case "REPORT":
                          report();
                          break;
                      case "EXIT","QUIT":
                          System.out.println("IF YOU ARE SURE CONFIRM WRITE 'YES'");
                          String commandExit = scanner1.nextLine().toUpperCase();
                          if (commandExit.equals("YES")){
                              exit(log);
                              scanner1.close();
                              return;
                          } else {
                              break;
                          }


                      default:
                          throw new IllegalStateException("Unexpected value: " + command);

                  }
              }   catch (IllegalStateException e){
                  System.out.println("Error: "+ e.getMessage());
                  System.out.println("Try to enter a new valid command");


              }catch (OrbitSimException e) {
                  // Exception Shielding: messaggio pulito, niente stack trace
                  System.out.println("  [MISSION CONTROL] " + e.getMessage());
              }


          }



    }

    private void launch(LogManager log) throws OrbitSimException {



        //controllo input launch one time
        if (missionRunning) {
            System.out.println("  Mission already in progress.");
            log.appendLogWarn("Mission already in progress");
        }else{

            //transizione fase
            transitionTo(new LaunchPhase());


            System.out.println("LAUNCH SEQUENCE IS STARTED!");
            log.appendLogInfo("launch sequence is started by the user ");

            System.out.println("ALL SYSTEMS HAVE BEEN CHECKED!");
            log.appendLogInfo("all systems have been checked due to init launch phase");
            for (int i = 10;i > 0; i--){
                log.appendLogInfo("countdown "+ i);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);

                }
            }
            missionRunning = true;
            spacecraft.setLaunchState();
            missionStartMs = System.currentTimeMillis();
            System.out.println("Liftoff!We are taking off!");
            log.appendLogInfo("Liftoff OK");

            loadingAnim();

            transitionTo(new AscentPhase());
            spacecraft.setAscentState();
            log.appendLogInfo("Ascent phase is started!");

            loadingAnim();

            transitionTo(new OrbitalPhase());
            spacecraft.setOrbitalState();
            log.appendLogInfo("Orbital phase is reached!");
        }
    }

    private static void exit(LogManager log){
        log.appendLogInfo("user shutdown the system with EXIT command");
        log.closeLog();

        System.out.println("\n  Mission Control offline. Goodbye.\n");
    }

    private void abort(LogManager log) throws OrbitSimException {
        System.out.println("\n  !!! ABORT COMMAND RECEIVED !!!");
        transitionTo(new AbortPhase());
        log.appendLogSevere("ABORT executed by operator");

    }


    private void reentry(LogManager log) {
       try {
           requirePhase("REENTRY requires orbital phase.");


           loadingAnim();
           log.appendLogInfo("user init reentry phase");
           transitionTo(new ReentryPhase());
           spacecraft.setReentryState();

           loadingAnim();
           log.appendLogInfo("enter in splashdown phase");
           transitionTo(new SplashdownPhase());

       } catch (OrbitSimException e) {
           System.out.println(e.getMessage());

       }

    }

    private void injectAnomaly(String[] args, LogManager log) throws OrbitSimException {
        requirePhase("Anomaly injection available in ORBITAL phase only.");
        String type = args.length > 0 ? args[0] : "";
        if (type.isBlank()) {
            System.out.println("  Usage: INJECT_ANOMALY <type>");
            System.out.println("  Types: " + AnomalyFactory.listTypes());
            return;
        }

        // Factory crea il contesto anomalia
        AnomalyContext ctx = AnomalyFactory.create(type);


        // Observer notifica
        eventBus.publish(new MissionEvent(
                MissionEvent.EventType.ANOMALY, ctx.getSourceSystem(),
                ctx.getAnomalyType() + " — Severity " + ctx.getSeverity(),
                ctx.getSeverity() >= 4 ? MissionEvent.Severity.EMERGENCY : MissionEvent.Severity.CRITICAL));

        System.out.println("\n  ╔══════ ANOMALY RESPONSE PROTOCOL ══════╗");
        System.out.println("  " + ctx.getAnomalyType());
        System.out.println("  ╚═══════════════════════════════════════╝");

        // Chain of Responsibility in azione — visibile step per step
        anomalyPipeline.handle(ctx);

        log.appendLogWarn("Anomaly handled: " + ctx.getAnomalyType() +
                " — actions: " + ctx.getActionLog().size());

        if (ctx.isMissionAborted()) {
            System.out.println("\n  !!! MISSION ABORT INITIATED !!!");
            transitionTo(new AbortPhase());
        } else {
            System.out.println("\n  Anomaly contained");
        }
    }

    private  void scan(LogManager log) {
        System.out.println("\n  ── FULL SYSTEM SCAN ──");
        // Iterator pattern in azione
        var it = spacecraft.systemIterator();
        int ok = 0, warn = 0, crit = 0;
        while (it.hasNext()) {
            var sys = it.next();
            System.out.print("  " + sys.getStatusReport());
            switch (sys.getStatus()) {
                case NOMINAL  -> ok++;
                case DEGRADED -> warn++;
                case CRITICAL, OFFLINE -> crit++;
            }
        }
        System.out.printf("\n  Summary: %d nominal, %d degraded, %d critical\n", ok, warn, crit);
        log.appendLogInfo("System scan: " + ok + " OK, " + warn + " warn, " + crit + " crit");
    }



    private void maneuver(String[] args, LogManager log) throws OrbitSimException {
        requirePhase("MANEUVER requires orbital phase.");
        String type = args.length > 0 ? args[0].toUpperCase() : "HOHMANN";
        switch (type) {
            case "HOHMANN" -> {
                System.out.println("\n  Executing Hohmann transfer burn...");
                loadingAnimShort();
                System.out.println("  Delta-V applied: +28 m/s");
                System.out.println("  New altitude: 420 km — nominal");
                log.appendLogInfo("Hohmann transfer executed");

            }
            case "REBOOST" -> {
                System.out.println("\n  Reboost burn: +12 m/s");
                log.appendLogInfo("Reboost executed");
            }
            default -> System.out.println("  Unknown maneuver. Available: HOHMANN, REBOOST");
        }
    }

    private void telemetry() { System.out.println(spacecraft.getTelemetry()); }

    private void systems() { System.out.println(spacecraft.getSystemsReport()); }


    private void status() {
        String phase = currentPhase != null ? currentPhase.getName() : "PRE-LAUNCH";
        long elapsed = missionRunning ? (System.currentTimeMillis() - missionStartMs) / 1000 : 0;
        //stampa formattata con segnaposti
        System.out.printf(
                """
                        
                          ── MISSION STATUS ──
                          Spacecraft: %s
                          Phase:      %s — %s
                          Overall:    %s
                          Elapsed:    %ds
                        """,
                spacecraft.getName(),
                phase,
                currentPhase != null ? currentPhase.getDescription() : "Awaiting launch",
                spacecraft.getOverallStatus(),
                elapsed);
        if (currentPhase != null) {
            System.out.println("  Commands:   " + String.join(", ", currentPhase.availableCommands()));
        }
    }


    private void help() {
        String phase = currentPhase != null ? currentPhase.getName() : "PRE-LAUNCH";
        System.out.println("\n  ── AVAILABLE COMMANDS [" + phase + "] ──");
        if (currentPhase == null) {
            System.out.println("  LAUNCH         — Start HORUS-21 mission");
        } else {
            for (String c : currentPhase.availableCommands()) {
                System.out.println("  " + padRight(c) + commandDesc(c));
            }
        }
        System.out.println("  EXIT           — Quit mission control");

    }

    // nuovo metodo
    private void report() {
        boolean missionEnded = currentPhase instanceof SplashdownPhase
                || currentPhase instanceof AbortPhase;
        if (!missionEnded) {
            System.out.println("  REPORT is available only after mission end.");
            return;
        }
        System.out.println(blackBox.generateReport());
    }

    private String commandDesc(String cmd){
        return switch (cmd) {
            case "STATUS" -> "Mission and phase overview";
            case "TELEMETRY" -> "Live sensor readings";
            case "SYSTEMS" -> "Composite system hierarchy";
            case "MANEUVER" -> "Execute orbital maneuver [HOHMANN|REBOOST]";
            case "SCAN" -> "Iterator scan of all subsystems";
            case "INJECT_ANOMALY" -> "Trigger anomaly [type] — Chain of Responsibility";
            case "REENTRY" -> "Begin reentry sequence";
            case "ABORT" -> "Emergency mission abort";
            case "REPORT" -> "Full mission black box report";
            default -> "";
        };
    }



//------------------helpers---------------------------------

    //gestione transizioni fase missione
    private void transitionTo(MissionPhase next) {
        if (currentPhase != null && !currentPhase.canTransitionTo(next)) {
            System.out.println("  Invalid phase transition: " + currentPhase.getName() + " → " + next.getName());
            return;
        }
        if (currentPhase != null) System.out.println(currentPhase.onExit());
        currentPhase = next;
        loadingAnimShort();
        System.out.println(next.onEnter());
    }

    //gestisco i requisiti di cambio fase e relative eccezioni
    private void requirePhase(String msg)
            throws OrbitSimException {
        if (!(currentPhase instanceof OrbitalPhase))
            throw new OrbitSimException(msg + " Current phase: " +
                    (currentPhase != null ? currentPhase.getName() : "PRE-LAUNCH"));
    }
    //animazione caricamento lunga
    private void loadingAnim(){
        for (int i =0;i < 10; i++){
            System.out.print(">");
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //animazione caricamento corta
    private void loadingAnimShort(){
        for (int i =0;i < 5; i++){
            System.out.print(">");
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //formattazione help
    private String padRight(String s) {
        return String.format("%-" + 18 + "s", s);
    }
}

