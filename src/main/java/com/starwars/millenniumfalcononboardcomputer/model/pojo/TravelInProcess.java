package com.starwars.millenniumfalcononboardcomputer.model.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TravelInProcess {
    private String currentPosition;
    private int autonomy;
    private int numberOfDays = 0;
    private int numberOfMeetingsWithHunters = 0;
}

