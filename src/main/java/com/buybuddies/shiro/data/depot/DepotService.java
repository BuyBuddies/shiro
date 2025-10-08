package com.buybuddies.shiro.data.depot;

import com.buybuddies.shiro.data.home.Home;
import com.buybuddies.shiro.data.home.HomeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepotService {
    private final DepotRepository depotRepository;
    private final HomeRepository homeRepository;

    public DepotDTO createDepot(DepotDTO depotDTO) {
        Home home = homeRepository.findById(depotDTO.getHomeId())
                .orElseThrow(() -> new RuntimeException("Home not found"));

        Depot depot = new Depot();
        depot.setName(depotDTO.getName());
        depot.setDescription(depotDTO.getDescription());
        depot.setHome(home);

        depot = depotRepository.save(depot);
        return convertToDTO(depot);
    }

    public DepotDTO getDepot(Long id) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depot not found"));
        return convertToDTO(depot);
    }

    public List<DepotDTO> getAllDepots() {
        return depotRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DepotDTO> getDepotsByHome(Long homeId) {
        return depotRepository.findByHomeId(homeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DepotDTO> searchDepotsByHome(Long homeId, String searchTerm) {
        return depotRepository.findByHomeIdAndNameContainingIgnoreCase(homeId, searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DepotDTO updateDepot(Long id, DepotDTO depotDTO) {
        Depot depot = depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depot not found"));

        depot.setName(depotDTO.getName());
        depot.setDescription(depotDTO.getDescription());

        if (depotDTO.getHomeId() != null && !depotDTO.getHomeId().equals(depot.getHome().getId())) {
            Home newHome = homeRepository.findById(depotDTO.getHomeId())
                    .orElseThrow(() -> new RuntimeException("New home not found"));
            depot.setHome(newHome);
        }

        depot = depotRepository.save(depot);
        return convertToDTO(depot);
    }

    @Transactional
    public void deleteDepot(Long id) {
        if (!depotRepository.existsById(id)) {
            throw new RuntimeException("Depot not found");
        }
        depotRepository.deleteById(id);
    }

    private DepotDTO convertToDTO(Depot depot) {
        DepotDTO dto = new DepotDTO();
        dto.setId(depot.getId());
        dto.setName(depot.getName());
        dto.setDescription(depot.getDescription());
        dto.setHomeId(depot.getHome().getId());
        return dto;
    }
}