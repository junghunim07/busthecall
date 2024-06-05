package capston.busthecall.service;


import capston.busthecall.domain.Beacon;
import capston.busthecall.repository.BeaconRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BeaconService {

    private final BeaconRepository beaconRepository;


    @Transactional
    public Long excute(String uuId)
    {
        Beacon beacon = beaconRepository.findBeaconByUuId(uuId);

        return beacon.getStationId();
    }

    public Beacon findOne(Long stationId) {
        return beaconRepository.findBeaconByStationId(stationId).orElse(null);
    }

    public List<Beacon> findAll() {
        return beaconRepository.findAll();
    }
}
