package br.com.drs.radiotv_pro_escritorio.dto;

import br.com.drs.radiotv_pro_escritorio.service.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/administrador")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdministradorController {

    private final AdministradorService service;

    @GetMapping("/visao-completa")
    public ResponseEntity<AdministradorDTO> getVisaoCompleta(
            @RequestParam(required = false) String mes) {
        return ResponseEntity.ok(service.gerarVisaoCompleta(mes));
    }
}