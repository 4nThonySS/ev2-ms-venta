package cl.duocuc.venta.controller;

import cl.duocuc.venta.dto.VentaRequest;
import cl.duocuc.venta.dto.VentaResponse;
import cl.duocuc.venta.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listarVentas() {
        log.info("Recibida petición GET para listar ventas");
        List<VentaResponse> ventas = ventaService.listarVentas();
        log.info("Retornando {} ventas", ventas.size());
        return ResponseEntity.ok(ventas);
    }

    @PostMapping
    public ResponseEntity<VentaResponse> crearVenta(@Valid @RequestBody VentaRequest request) {
        log.info("Recibida petición POST para crear venta - Cliente: {}", request.getCliente());
        VentaResponse response = ventaService.crearVenta(request);
        log.info("Venta creada correctamente, retornando respuesta");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> obtenerVentaPorId(@PathVariable Long id) {
        log.info("Recibida petición GET para obtener venta ID: {}", id);
        return ResponseEntity.ok(ventaService.obtenerVentaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponse> actualizarVenta(
            @PathVariable Long id,
            @Valid @RequestBody VentaRequest request) {

        log.info("Recibida petición PUT para actualizar venta ID: {}", id);
        return ResponseEntity.ok(ventaService.actualizarVenta(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        log.info("Recibida petición DELETE para eliminar venta ID: {}", id);
        ventaService.eliminarVenta(id);
        log.info("Venta eliminada correctamente");
        return ResponseEntity.noContent().build();
    }

}
