package com.starwars.millenniumfalcononboardcomputer;

import com.starwars.millenniumfalcononboardcomputer.model.dto.BountyHunterDto;
import com.starwars.millenniumfalcononboardcomputer.model.dto.EmpireInformationDto;
import com.starwars.millenniumfalcononboardcomputer.model.entity.Route;
import com.starwars.millenniumfalcononboardcomputer.model.pojo.MillenniumFalcon;
import com.starwars.millenniumfalcononboardcomputer.repository.RouteRepository;
import com.starwars.millenniumfalcononboardcomputer.service.MillenniumFalconService;
import com.starwars.millenniumfalcononboardcomputer.service.RouteService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MillenniumServiceTest {
    @Mock
    private MillenniumFalconService millenniumFalconService;
    @Mock
    private RouteService routeService;
    @Mock
    private RouteRepository routeRepository;

    @BeforeEach
    void setUp() {
        List<Route> routes = List.of(
                new Route().setOrigin("Tatooine").setDestination("Dagobah").setTravelTime(6),
                new Route().setOrigin("Dagobah").setDestination("Endor").setTravelTime(4),
                new Route().setOrigin("Dagobah").setDestination("Hoth").setTravelTime(1),
                new Route().setOrigin("Hoth").setDestination("Endor").setTravelTime(1),
                new Route().setOrigin("Tatooine").setDestination("Hoth").setTravelTime(6)
        );
        Mockito.when(routeRepository.findAll()).thenReturn(routes);
        routeService = Mockito.spy(new RouteService(routeRepository));
        millenniumFalconService = Mockito.spy(new MillenniumFalconService(routeService));

    }

    @ParameterizedTest
    @MethodSource("empireInformationProvider")
    void computeOddsTest(float expectedResult, EmpireInformationDto empireInformationDto) {
        val millenniumFalcon = new MillenniumFalcon();
        millenniumFalcon.setDeparture("Tatooine");
        millenniumFalcon.setAutonomy(6);
        millenniumFalcon.setArrival("Endor");
        millenniumFalcon.setRoutesDb("universe.db");
        val result = millenniumFalconService.computeBestOddsToReachDestination(empireInformationDto, millenniumFalcon);
        assertEquals(expectedResult, result);
    }

    public static Stream<Arguments> empireInformationProvider() {
        val bountyHunters = List.of(new BountyHunterDto("Hoth", 6), new BountyHunterDto("Hoth", 7), new BountyHunterDto("Hoth", 8));
        val empireInformationDto1 = new EmpireInformationDto(7, bountyHunters);
        val empireInformationDto2 = new EmpireInformationDto(8, bountyHunters);
        val empireInformationDto3 = new EmpireInformationDto(9, bountyHunters);
        val empireInformationDto4 = new EmpireInformationDto(10, bountyHunters);
        return Stream.of(
                Arguments.of(0f, empireInformationDto1),
                Arguments.of(81f, empireInformationDto2),
                Arguments.of(90f, empireInformationDto3),
                Arguments.of(100f, empireInformationDto4)
        );
    }
}
