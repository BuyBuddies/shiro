package com.buybuddies.shiro.data.depot;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depots")
@RequiredArgsConstructor
public class DepotController {
    private final DepotService depotService;

    @PostMapping
    public ResponseEntity<DepotDTO> createDepot(@RequestBody DepotDTO depotDTO) {
        return new ResponseEntity<>(depotService.createDepot(depotDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepotDTO> getDepot(@PathVariable Long id) {
        return ResponseEntity.ok(depotService.getDepot(id));
    }

    @GetMapping
    public ResponseEntity<List<DepotDTO>> getAllDepots() {
        return ResponseEntity.ok(depotService.getAllDepots());
    }

    @GetMapping("/home/{homeId}")
    public ResponseEntity<List<DepotDTO>> getDepotsByHome(@PathVariable Long homeId) {
        return ResponseEntity.ok(depotService.getDepotsByHome(homeId));
    }

    @GetMapping("/home/{homeId}/search")
    public ResponseEntity<List<DepotDTO>> searchDepotsByHome(
            @PathVariable Long homeId,
            @RequestParam String name) {
        return ResponseEntity.ok(depotService.searchDepotsByHome(homeId, name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepotDTO> updateDepot(@PathVariable Long id, @RequestBody DepotDTO depotDTO) {
        return ResponseEntity.ok(depotService.updateDepot(id, depotDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepot(@PathVariable Long id) {
        depotService.deleteDepot(id);
        return ResponseEntity.noContent().build();
    }
}