package co.edu.uptc.Bocanegra_Segura__Gonzalez_Diaz__paginacion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

//Clase frame (Marco)
public class Frame {
    private int page;
    private String timestampCarga;

    public Frame(int page) {
        if (page < 0) {
            this.page = -1;
        } else {
            this.page = page;
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getTimestampCarga() {
        return timestampCarga;
    }

    public void setTimestampCarga() {
        this.timestampCarga = Instant.now()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SS"));
    }

}

