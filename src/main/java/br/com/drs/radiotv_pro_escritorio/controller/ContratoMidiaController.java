package br.com.drs.radiotv_pro_escritorio.controller;

import br.com.drs.radiotv_pro_escritorio.service.ContratoMidiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contratoMidia")
@RequiredArgsConstructor
public class ContratoMidiaController {

    private final ContratoMidiaService service;
}
