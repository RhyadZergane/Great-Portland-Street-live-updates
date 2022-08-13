package com.transform.underground.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Platform {
    private String name;

    @Override
    public String toString(){
        return "Platform: " + name;
    }
}
