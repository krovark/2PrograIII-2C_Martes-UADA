package SolucionMisionUada;

import MisionUada.Decision;
import MisionUada.Desplazamiento;
import MisionUada.Estacion;
import MisionUada.Movimiento;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Escenario alternativo de prueba para validar el algoritmo de backtracking.
 * Se modela un recorrido posible dentro de la facultad con varios caminos
 * y decisiones alternativas para que el algoritmo de {@link EncontrarRecorridoUadaImp}
 * pueda evaluar distintas ramas.
 */
public class PrincipalTest2 {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA 2 DEL ALGORITMO DE BACKTRACKING ===\n");

        int bateriaInicial = 100;

        ArrayList<Estacion> lugaresDisponibles = new ArrayList<>();
        ArrayList<Estacion> lugaresObligatorios = new ArrayList<>();

        Estacion aula633 = new Estacion("Aula 633", 633, true);
        Estacion patioCentral = new Estacion("Patio Central", 10, false);
        Estacion biblioteca = new Estacion("Biblioteca Central", 220, false);
        Estacion laboratorioIA = new Estacion("Laboratorio de IA", 320, true);
        Estacion salaProyectos = new Estacion("Sala de Proyectos", 450, true);

        lugaresDisponibles.add(patioCentral);
        lugaresDisponibles.add(biblioteca);
        lugaresDisponibles.add(laboratorioIA);
        lugaresDisponibles.add(salaProyectos);

        lugaresObligatorios.add(patioCentral);
        lugaresObligatorios.add(laboratorioIA);
        lugaresObligatorios.add(salaProyectos);

        mostrarLugares(lugaresObligatorios, "Lugares obligatorios a visitar:");
        mostrarLugares(lugaresDisponibles, "Lugares disponibles para visitar:");

        ArrayList<Desplazamiento> desplazamientos = new ArrayList<>();

        ArrayList<Movimiento> caminarSaltar = new ArrayList<>();
        caminarSaltar.add(Movimiento.CAMINAR);
        caminarSaltar.add(Movimiento.SALTAR);

        ArrayList<Movimiento> caminarPatas = new ArrayList<>();
        caminarPatas.add(Movimiento.CAMINAR);
        caminarPatas.add(Movimiento.PATAS_ARRIBA);

        ArrayList<Movimiento> todos = new ArrayList<>();
        todos.add(Movimiento.CAMINAR);
        todos.add(Movimiento.SALTAR);
        todos.add(Movimiento.PATAS_ARRIBA);

        desplazamientos.add(new Desplazamiento(aula633, patioCentral, caminarSaltar, 12));
        desplazamientos.add(new Desplazamiento(aula633, salaProyectos, caminarPatas, 35));
        desplazamientos.add(new Desplazamiento(patioCentral, biblioteca, caminarPatas, 22));
        desplazamientos.add(new Desplazamiento(patioCentral, laboratorioIA, caminarSaltar, 28));
        desplazamientos.add(new Desplazamiento(biblioteca, laboratorioIA, todos, 18));
        desplazamientos.add(new Desplazamiento(laboratorioIA, salaProyectos, caminarPatas, 16));
        desplazamientos.add(new Desplazamiento(laboratorioIA, aula633, todos, 20));
        desplazamientos.add(new Desplazamiento(salaProyectos, aula633, caminarSaltar, 14));

        mostrarDesplazamientos(desplazamientos);

        EncontrarRecorridoUadaImp recorridoUada = new EncontrarRecorridoUadaImp();

        System.out.println("Ejecutando algoritmo con batería inicial de " + bateriaInicial + "%...\n");
        long startTime = System.currentTimeMillis();
        ArrayList<Decision> decisiones = recorridoUada.encontrarSecuenciaRecorridoUada(
                bateriaInicial,
                aula633,
                lugaresDisponibles,
                lugaresObligatorios,
                desplazamientos
        );
        long endTime = System.currentTimeMillis();

        mostrarResultado(decisiones, endTime - startTime, "output_test2.txt");
    }

    private static void mostrarLugares(ArrayList<Estacion> estaciones, String titulo) {
        System.out.println(titulo);
        for (Estacion estacion : estaciones) {
            String tipo = estacion.getEsAula() ? "(Aula con recarga)" : "(Sin recarga)";
            System.out.println("  - " + estacion.getNombre() + " " + tipo);
        }
        System.out.println();
    }

    private static void mostrarDesplazamientos(ArrayList<Desplazamiento> desplazamientos) {
        System.out.println("Desplazamientos modelados en el grafo:");
        for (Desplazamiento desplazamiento : desplazamientos) {
            System.out.print("  - " + desplazamiento.getOrigen().getNombre() + " -> " +
                    desplazamiento.getDestino().getNombre() + " (" + desplazamiento.getTiempoBase() + " seg) | Movimientos: ");
            System.out.println(desplazamiento.getMovimientosPermitidos());
        }
        System.out.println();
    }

    private static void mostrarResultado(ArrayList<Decision> decisiones, long tiempoMs, String archivoSalida) {
        System.out.println("=== RESULTADOS PRUEBA 2 ===");
        System.out.println("Tiempo de ejecución: " + tiempoMs + " ms");

        if (decisiones.isEmpty()) {
            System.out.println("No se encontró una solución que cumpla con todas las restricciones.");
            return;
        }

        Decision ultima = decisiones.get(decisiones.size() - 1);
        System.out.println("Decisiones totales: " + decisiones.size());
        System.out.println("Tiempo total acumulado: " + ultima.getTiempoAcumulado() + " segundos");
        System.out.println("Batería final: " + ultima.getBateriaRemanente() + "%\n");

        guardarImpresionDecisiones(decisiones, archivoSalida);
    }

    private static void guardarImpresionDecisiones(ArrayList<Decision> decisiones, String archivoSalida) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < decisiones.size(); i++) {
            Decision decision = decisiones.get(i);
            sb.append(String.format(
                    "Decisión #%d\n" +
                            "  Desde: %s\n" +
                            "  Hasta: %s\n" +
                            "  Movimiento: %s\n" +
                            "  Batería remanente: %d%%\n" +
                            "  Tiempo acumulado: %d segundos\n\n",
                    i + 1,
                    decision.getOrigen().getNombre(),
                    decision.getDestino().getNombre(),
                    decision.getMovimientoEmpleado(),
                    decision.getBateriaRemanente(),
                    decision.getTiempoAcumulado()
            ));
        }

        System.out.println("--- SECUENCIA DE DECISIONES ---");
        System.out.println(sb);

        String rutaArchivo = Paths.get(System.getProperty("user.dir"), archivoSalida).toString();
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(sb.toString());
            System.out.println("Detalle guardado en: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("No se pudo escribir el archivo de salida: " + e.getMessage());
        }
    }
}
