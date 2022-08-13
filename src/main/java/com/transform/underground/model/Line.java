package com.transform.underground.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Line {
    private String name;

    @Override
    public String toString(){
        return "Line: " + name;
    }
}
