package com.dw.artgallery.controller;


import com.dw.artgallery.DTO.ArtistDTO;
import com.dw.artgallery.service.ArtistService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/artist")
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAllArtist(){
        return new ResponseEntity<>(artistService.getAllArtist(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
        public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id){
        return new ResponseEntity<>(artistService.getArtistById(id),HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ArtistDTO>> getArtistByName(@PathVariable String name){
        return new ResponseEntity<>(artistService.getArtistByName(name),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ArtistDTO> saveArtist(@RequestBody ArtistDTO artistDTO){
        return new ResponseEntity<>(artistService.saveArtist(artistDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteArtist(@PathVariable Long id) {
        return new ResponseEntity<>(artistService.deleteArtist(id), HttpStatus.OK);
    }
}