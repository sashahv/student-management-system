package pl.dfjp.students.service.address;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;
import pl.dfjp.students.repository.address.current.CurrentAddressRepository;
import pl.dfjp.students.repository.address.current.PlaceOfLivingRepository;

import java.util.List;

@Service
@Slf4j
public class CurrentAddressService {
    private final PlaceOfLivingRepository placeOfLivingRepository;
    private final CurrentAddressRepository currentAddressRepository;

    @Autowired
    public CurrentAddressService(PlaceOfLivingRepository placeOfLivingRepository,
                                 CurrentAddressRepository currentAddressRepository) {
        this.placeOfLivingRepository = placeOfLivingRepository;
        this.currentAddressRepository = currentAddressRepository;
    }

    public void addNewPlaceOfLiving(String name,
                                    int roomSize,
                                    int decreaseScholarshipAmount){
        PlaceOfLiving placeOfLiving = new PlaceOfLiving();
        if(name.contains("-")){
            String[] split = name.split("-");
            placeOfLiving.setName(split[0]);
        } else {
            placeOfLiving.setName(name);
        }
        placeOfLiving.setRoomSize(roomSize);
        placeOfLiving.setDecreaseScholarship(decreaseScholarshipAmount);
        placeOfLivingRepository.save(placeOfLiving);
    }

    public void deletePlaceOfLiving(Long id) {
        PlaceOfLiving placeOfLiving = placeOfLivingRepository.findById(id).orElse(null);
        List<CurrentAddress> currentAddresses = currentAddressRepository.findByPlaceOfLivingId(id);
        if(placeOfLiving!=null){
            if (currentAddresses.isEmpty()) {
                placeOfLivingRepository.deleteById(id);
            } else {
                throw new RuntimeException("Miejsce zamieszkania stypendysty musi być zamienione przez usuwaniem");
            }
        }
    }
}
