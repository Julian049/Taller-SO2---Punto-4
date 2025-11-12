package co.edu.uptc.Bocanegra_Segura__Gonzalez_Diaz__paginacion;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

//Clase memoria
public class Memory {

    private final int marcosTotales = 4;
    private Frame[] marcos;
    private ArrayList<Integer> paginas;
    private int posicionPaginaAntigua = 0;
    private int acceso = 1;
    private ArrayList<String[]> registros;
    private int fallos;
    private int reemplazosTotales;
    private ArrayList<Integer> paginasReemplazadas = new ArrayList<>();

    public Memory() {
        marcos = new Frame[marcosTotales];
        paginas = new ArrayList<>(Arrays.asList(
                2, 3, 2, 1, 5, 2, 4, 5, 3, 2, 5, 2, 7, 3, 4, 5, 6, 7, 2, 4
        ));
        registros = new ArrayList<>();
        inicializarMarcos();
        fallos = 0;
        reemplazosTotales = 0;
    }

    public void simulador() {
        System.out.println("Iniciando sistema de paginacion FIFO...\n\n");
        for (int i = 0; i < paginas.size(); i++) {
            accederPaginaFIFO(paginas.get(i));
        }
        verTabla();
        exportarAExcel("resultados_paginacion.xlsx ");
        tablaMetricas();
    }

    private void inicializarMarcos() {
        for (int i = 0; i < marcos.length; i++) {
            marcos[i] = new Frame(-1);
        }
    }

    private void accederPaginaFIFO(int pagina) {
        System.out.println("\n==================================================");
        System.out.printf(" ACCESO  #%-3d | Página solicitada: %d%n", acceso, pagina);
        System.out.println("==================================================");
        System.out.println("Verificando si la página está en memoria...");

        String fallo = "";
        String marcoVictima = "-";
        String timestamp = "";

        if (verificarPaginaEnMemoria(pagina) == null) {
            System.out.println("Página NO está en memoria → FALLO DE PÁGINA");
            System.out.println("Comprobando marcos libres...");

            if (marcosLlenos()) {
                System.out.println("No hay marcos libres → Iniciando sistema de reemplazo FIFO");
                System.out.println("Buscando marco víctima...");

                marcoVictima = "Pag " + marcos[posicionPaginaAntigua].getPage() + " (marco " + posicionPaginaAntigua + ")";
                paginasReemplazadas.add(marcos[posicionPaginaAntigua].getPage());

                System.out.printf("Víctima: marco %d (reemplaza pág %d) → entra pág %d%n",
                        posicionPaginaAntigua, marcos[posicionPaginaAntigua].getPage(), pagina);

                marcos[posicionPaginaAntigua].setPage(pagina);
                marcos[posicionPaginaAntigua].setTimestampCarga();
                timestamp = marcos[posicionPaginaAntigua].getTimestampCarga();
                aumentarPosicion();
                reemplazosTotales++;


                System.out.printf("Reemplazo realizado :) a las %s%n", timestamp);
            } else {
                System.out.println("Hay marcos libres → Cargando página...");
                int marcoLibreIndex = -1;
                for (int i = 0; i < marcos.length; i++) {
                    if (marcos[i].getPage() == -1) {
                        marcos[i].setPage(pagina);
                        marcos[i].setTimestampCarga();
                        timestamp = marcos[i].getTimestampCarga();
                        marcoLibreIndex = i;
                        break;
                    }
                }
                System.out.printf(" :) Página cargada en el marco: %d%n a las %s%n", marcoLibreIndex, timestamp);
            }

            fallo = "Si";
            fallos++;
        } else {
            System.out.println("✓ Página encontrada en memoria → ACCESO EXITOSO (sin fallo)");
            System.out.println("Timestamp de carga de la página en memoria: " + timestamp);
            fallo = "No";
        }
        String m0 = (marcos[0].getPage() == -1 ? " " : String.valueOf(marcos[0].getPage()));
        String m1 = (marcos[1].getPage() == -1 ? " " : String.valueOf(marcos[1].getPage()));
        String m2 = (marcos[2].getPage() == -1 ? " " : String.valueOf(marcos[2].getPage()));
        String m3 = (marcos[3].getPage() == -1 ? " " : String.valueOf(marcos[3].getPage()));

        System.out.println("--------------------------------------------------\n");


        String[] registro = new String[6];
        registro[0] = String.valueOf(acceso++);
        registro[1] = String.valueOf(pagina);
        registro[2] = m0;
        registro[3] = m1;
        registro[4] = m2;
        registro[5] = m3;

        registros.add(new String[]{registro[0], registro[1], registro[2], registro[3], registro[4], registro[5], fallo, marcoVictima, timestamp});
    }


    private void verTabla() {

        System.out.println("\nTABLA DE ACCESOS FINAL - REEMPLAZO FIFO\n");

        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(8) + "+" + "-".repeat(37)
                + "+" + "-".repeat(10) + "+" + "-".repeat(17) + "+" + "-".repeat(15) + "+");

        System.out.printf(
                "| %-6s | %-6s | %-35s | %-8s | %-15s | %-13s |%n",
                "Acceso", "Página", "Estado Memoria (Marcos 0-3)", "¿Fallo?", "Víctima", "Timestamp"
        );

        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(8) + "+" + "-".repeat(37)
                + "+" + "-".repeat(10) + "+" + "-".repeat(17) + "+" + "-".repeat(15) + "+");

        for (String[] registro : registros) {
            System.out.printf(
                    "| %-6s | %-6s | [%-2s, %-2s, %-2s, %-2s]                | %-8s | %-15s | %-13s |%n",
                    registro[0],
                    registro[1],
                    registro[2], registro[3], registro[4], registro[5],
                    registro[6],
                    registro[7],
                    registro.length > 8 ? registro[8] : "-"
            );
        }

        System.out.println("+" + "-".repeat(8) + "+" + "-".repeat(8) + "+" + "-".repeat(37)
                + "+" + "-".repeat(10) + "+" + "-".repeat(17) + "+" + "-".repeat(15) + "+");
    }


    private void tablaMetricas() {
        String metrica = "FIFO";
        double tasaFallosTotales = ((double) fallos / paginas.size()) * 100;
        List<Integer> paginasMasReemplazadas = paginasMasReemplazadas();

        System.out.printf("%-30s %s%n", "Métrica", metrica);
        System.out.println("----------------------------------------------");
        System.out.printf("%-30s %s%n", "Fallos de página totales", fallos);
        System.out.printf("%-30s %s%%%n", "Tasa de fallos", tasaFallosTotales);
        System.out.printf("%-30s %s%n", "Reemplazos realizados", reemplazosTotales);
        System.out.printf("%-30s %s%n", "Páginas más reemplazadas", paginasMasReemplazadas);
    }


    private List<Integer> paginasMasReemplazadas() {

        Map<Integer, Integer> conteo = new HashMap<>();

        for (int num : paginasReemplazadas) {
            conteo.put(num, conteo.getOrDefault(num, 0) + 1);
        }

        int maxFrecuencia = Collections.max(conteo.values());

        List<Integer> masRepetidos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entrada : conteo.entrySet()) {
            if (entrada.getValue() == maxFrecuencia) {
                masRepetidos.add(entrada.getKey());
            }
        }

        return masRepetidos;
    }

    private boolean marcosLlenos() {
        boolean out = true;
        for (Frame f : marcos) {
            if (f.getPage() == -1) {
                out = false;
            }
        }
        return out;
    }

    private void aumentarPosicion() {
        if (posicionPaginaAntigua == marcos.length - 1) {
            posicionPaginaAntigua = 0;
        } else {
            posicionPaginaAntigua++;
        }
    }

    private Frame verificarPaginaEnMemoria(int pagina) {
        Frame out = null;
        for (Frame f : marcos) {
            if (f.getPage() == pagina) {
                out = f;
                break;
            }
        }
        return out;
    }

    public void exportarAExcel(String rutaSalida) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Resultados FIFO");
            int rowNum = 0;

            String[] headers = {"Acceso", "Página", "Marco 0", "Marco 1", "Marco 2", "Marco 3", "¿Fallo?", "Víctima"};
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            for (String[] registro : registros) {
                Row dataRow = sheet.createRow(rowNum++);

                dataRow.createCell(0).setCellValue(Integer.parseInt(registro[0]));
                dataRow.createCell(1).setCellValue(Integer.parseInt(registro[1]));

                for (int i = 2; i <= 5; i++) {
                    Cell cell = dataRow.createCell(i);
                    String val = registro[i].trim();
                    if (val.isEmpty()) {
                        cell.setBlank();
                    } else {
                        cell.setCellValue(Integer.parseInt(val));
                    }
                }

                dataRow.createCell(6).setCellValue(registro[6]);
                dataRow.createCell(7).setCellValue(registro[7]);
            }

            rowNum += 4;

            double tasaFallosTotales = ((double) fallos / paginas.size()) * 100;
            List<Integer> paginasMasReemplazadas = paginasMasReemplazadas();

            Row rowMetrica = sheet.createRow(rowNum++);
            rowMetrica.createCell(0).setCellValue("Métrica");
            rowMetrica.createCell(1).setCellValue("FIFO");

            Row rowFallos = sheet.createRow(rowNum++);
            rowFallos.createCell(0).setCellValue("Fallos de página totales");
            rowFallos.createCell(1).setCellValue(fallos);

            Row rowTasa = sheet.createRow(rowNum++);
            rowTasa.createCell(0).setCellValue("Tasa de fallos");
            rowTasa.createCell(1).setCellValue(String.format("%.1f%%", tasaFallosTotales));

            Row rowReemplazos = sheet.createRow(rowNum++);
            rowReemplazos.createCell(0).setCellValue("Reemplazos realizados");
            rowReemplazos.createCell(1).setCellValue(reemplazosTotales);

            Row rowMasReemplazadas = sheet.createRow(rowNum++);
            rowMasReemplazadas.createCell(0).setCellValue("Páginas más reemplazadas");
            rowMasReemplazadas.createCell(1).setCellValue(paginasMasReemplazadas.toString());

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(rutaSalida)) {
                workbook.write(outputStream);
            }

            System.out.println("\nResultados exportados exitosamente a " + rutaSalida);

        } catch (IOException e) {
            System.err.println("Error al exportar a Excel: " + e.getMessage());
        }
    }

}
