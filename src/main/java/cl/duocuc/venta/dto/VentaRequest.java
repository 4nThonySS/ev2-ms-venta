package cl.duocuc.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaRequest {

    @NotBlank(message = "El cliente es obligatorio")
    @Size(min = 3, max = 100, message = "El cliente debe tener entre 3 y 100 caracteres")
    private String cliente;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

}
