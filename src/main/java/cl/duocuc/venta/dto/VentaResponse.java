package cl.duocuc.venta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponse {

    private Long id;
    private LocalDateTime fecha;
    private String cliente;
    private Long productoId;
    private Integer cantidad;
    private Double total;

}
