package com.starwars.millenniumfalcononboardcomputer.service;

import com.starwars.millenniumfalcononboardcomputer.model.dto.BountyHunterDto;
import com.starwars.millenniumfalcononboardcomputer.model.dto.EmpireInformationDto;
import com.starwars.millenniumfalcononboardcomputer.model.pojo.AccessibleRoute;
import com.starwars.millenniumfalcononboardcomputer.model.pojo.MillenniumFalcon;
import com.starwars.millenniumfalcononboardcomputer.model.pojo.TravelInProcess;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MillenniumFalconService {
    private final RouteService routeService;

    /**
     *
     * @param empireInformationDto countdown and bounty hunters' positions
     * @return The odds that the Millennium Falcon reaches the destination in time
     */
    public float computeBestOddsToReachDestination(EmpireInformationDto empireInformationDto, MillenniumFalcon millenniumFalcon) {
        val huntersPositionsByDays = getHuntersPositionsByDay(empireInformationDto.bountyHunters());
        int minimumNumberOfMeetingsWithHunters = Integer.MAX_VALUE;
        val queue = new ArrayDeque<TravelInProcess>();
        var accessibleRoutes = routeService.getAccessibleRoutesMap(millenniumFalcon);

        val departureStep = new TravelInProcess()
                .setCurrentPosition(millenniumFalcon.getDeparture())
                .setAutonomy(millenniumFalcon.getAutonomy())
                .setNumberOfDays(0);
        queue.add(departureStep);

        while (!queue.isEmpty() && minimumNumberOfMeetingsWithHunters != 0) {
            val travelInProcess = queue.poll();
            if (travelInProcess.getNumberOfMeetingsWithHunters() < minimumNumberOfMeetingsWithHunters &&
                travelInProcess.getNumberOfDays() <= empireInformationDto.countdown()) {
                val areHuntersPresent = areHuntersPresentOnPlanet(travelInProcess.getNumberOfDays(), travelInProcess.getCurrentPosition(), huntersPositionsByDays);
                if (areHuntersPresent)
                    travelInProcess.setNumberOfMeetingsWithHunters(travelInProcess.getNumberOfMeetingsWithHunters()+1);
                if (travelInProcess.getCurrentPosition().equals(millenniumFalcon.getArrival())) {
                    minimumNumberOfMeetingsWithHunters = Math.min(minimumNumberOfMeetingsWithHunters, travelInProcess.getNumberOfMeetingsWithHunters());
                    continue;
                }

                for (val accessibleRoute: accessibleRoutes.get(travelInProcess.getCurrentPosition())) {
                    processNextAccessibleRoute(accessibleRoute, travelInProcess, queue);
                }
                refuelStep(travelInProcess, queue, millenniumFalcon);
            }
        }
        return computeFinalOdd(minimumNumberOfMeetingsWithHunters);
    }

    /**
     * Refuel the Millennium Falcon on the current planet
     * @param travelInProcess current travel step
     * @param queue queue to process all next steps
     */
    private void refuelStep(TravelInProcess travelInProcess, Queue<TravelInProcess> queue, MillenniumFalcon millenniumFalcon) {
        val refuelStep = new TravelInProcess()
                .setNumberOfDays(travelInProcess.getNumberOfDays() + 1)
                .setCurrentPosition(travelInProcess.getCurrentPosition())
                .setNumberOfMeetingsWithHunters(travelInProcess.getNumberOfMeetingsWithHunters())
                .setAutonomy(millenniumFalcon.getAutonomy());
        queue.add(refuelStep);
    }

    /**
     * Process the next travel step from an accessible route
     * @param accessibleRoute Next travel step
     * @param travelInProcess Current travel step
     * @param queue Queue to process all next steps
     */
    private void processNextAccessibleRoute(AccessibleRoute accessibleRoute,
                                            TravelInProcess travelInProcess,
                                            Queue<TravelInProcess> queue) {
        if (accessibleRoute.travelTime() <= travelInProcess.getAutonomy()) {
            val nextTravelStep = new TravelInProcess()
                    .setNumberOfDays(travelInProcess.getNumberOfDays() + accessibleRoute.travelTime())
                    .setCurrentPosition(accessibleRoute.planet())
                    .setNumberOfMeetingsWithHunters(travelInProcess.getNumberOfMeetingsWithHunters())
                    .setAutonomy(travelInProcess.getAutonomy() - accessibleRoute.travelTime());
            queue.add(nextTravelStep);
        }
    }

    /**
     * Get the bounty hunters positions according to the days
     * @param bountyHunters Bounty hunters positions
     * @return A map with the presence of the hunters on the planets according to the days
     */
    private Map<Integer, Set<String>> getHuntersPositionsByDay(List<BountyHunterDto> bountyHunters) {
        return bountyHunters
                .stream()
                .collect(
                        Collectors.groupingBy(
                                BountyHunterDto::day,
                                Collectors.mapping(BountyHunterDto::planet, Collectors.toSet()))
                );
    }

    /**
     * Check if bounty hunters are present on the current planet
     * @param dayNumber Current day
     * @param planet Current planet
     * @param huntersPositionsByDays Hunters positions
     * @return The presence of hunters
     */
    private boolean areHuntersPresentOnPlanet(int dayNumber, String planet, Map<Integer, Set<String>> huntersPositionsByDays) {
        return huntersPositionsByDays.get(dayNumber) != null && huntersPositionsByDays.get(dayNumber).contains(planet);
    }

    /**
     * Compute odds to survive in percentage
     * @param numberOfMeetingsWithHunters Minimum number of meetings with hunters to reach the destination
     * @return Odds to survive
     */
    private float computeFinalOdd(int numberOfMeetingsWithHunters) {
        float finalOdds = 1;
        if (numberOfMeetingsWithHunters == Integer.MAX_VALUE) {
            return 0;
        } else {
            for (int i = 0; i < numberOfMeetingsWithHunters; i++) {
                finalOdds -= Math.pow(9, i)/ Math.pow(10, i+1);
            }
            return finalOdds * 100;
        }
    }

}
