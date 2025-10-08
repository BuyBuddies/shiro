package com.buybuddies.shiro.data.home;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @PostMapping
    public ResponseEntity<HomeDTO> createHome(@RequestBody HomeDTO homeDTO) {
        return new ResponseEntity<>(homeService.createHome(homeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeDTO> getHome(@PathVariable Long id) {
        return ResponseEntity.ok(homeService.getHome(id));
    }

    @GetMapping
    public ResponseEntity<List<HomeDTO>> getAllHomes() {
        return ResponseEntity.ok(homeService.getAllHomes());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<HomeDTO>> getHomesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(homeService.getHomesByOwner(ownerId));
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<List<HomeDTO>> getHomesByMember(@PathVariable Long userId) {
        return ResponseEntity.ok(homeService.getHomesByMember(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeDTO> updateHome(
            @PathVariable Long id,
            @RequestBody HomeDTO homeDTO) {
        return ResponseEntity.ok(homeService.updateHome(id, homeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHome(@PathVariable Long id) {
        homeService.deleteHome(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{homeId}/members/{userId}")
    public ResponseEntity<HomeDTO> addMember(
            @PathVariable Long homeId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(homeService.addMember(homeId, userId));
    }

    @DeleteMapping("/{homeId}/members/{userId}")
    public ResponseEntity<HomeDTO> removeMember(
            @PathVariable Long homeId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(homeService.removeMember(homeId, userId));
    }
}