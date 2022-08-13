package com.transform.underground.model;

import lombok.*;

import java.time.LocalTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StationArrivals {
    private String stationName;
    // TFL API looks like ity uses one-to-one with line
    private Line line;
    // TFL API looks like it uses one-to-one with platform
    private Platform platform;
    // TFL API looks like it has a reference to desitnation station
    // but it is called towards not destination
    private String destination;
    // TFL API looks like it has a reference to the current station of a train
    private String currentLocation;

    // inbound/outbound
    private String direction;

    // time the
    private LocalTime expectedArrival;

    @Override
    public String toString(){
        LocalTime timeOfNextTrainInMins = expectedArrival.minusMinutes(LocalTime.now().getMinute());
        StringBuilder sb = new StringBuilder();
        /*return "The next train at " + name + " will be the " + line + " line service to " + destination +
                "departing on platform " + platform + ". it is currently at " + currentLocation +
                " and is due to arrive in " + timeDiffInMins.getMinute() + " minutes";*/
        sb.append("station: ").append(stationName).append(", line: ").append(line.getName())
                .append(", platform: ").append(platform.getName());

        // if 0 mins then say due now

        switch (timeOfNextTrainInMins.getMinute()) {
            case 0 -> sb.append(", due now ");
            case 1 -> sb.append(", arriving in: ").append(timeOfNextTrainInMins.getMinute()).append(" minute ");
            default -> sb.append(", arriving in: ").append(timeOfNextTrainInMins.getMinute()).append(" minutes ");
        }

        sb.append("terminating at: ").append(destination);

        return sb.toString();
    }
}
