package eshop.backend.controller;

import eshop.backend.exception.DetailSectionNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.DetailSection;
import eshop.backend.request.DetailSectionRequest;
import eshop.backend.service.DetailSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detail-sections")
@RequiredArgsConstructor
public class DetailSectionController {

    private final DetailSectionService detailSectionService;

    @PostMapping
    public ResponseEntity<DetailSection> createDetailSection(@RequestBody DetailSectionRequest request) throws ProductNotFoundException {
        DetailSection createdDetailSection = detailSectionService.createDetailSection(request);
        return new ResponseEntity<>(createdDetailSection, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailSection> updateDetailSection(@PathVariable Long id, @RequestBody DetailSectionRequest request) throws DetailSectionNotFoundException {
        if (!id.equals(request.id())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        DetailSection updatedDetailSection = detailSectionService.updateDetailSection(request);
        return new ResponseEntity<>(updatedDetailSection, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailSection> getDetailSection(@PathVariable Long id) throws DetailSectionNotFoundException {
        DetailSection detailSection = detailSectionService.getDetailSection(id);
        return new ResponseEntity<>(detailSection, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetailSection(@PathVariable Long id) throws DetailSectionNotFoundException {
        detailSectionService.deleteDetailSection(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}