package com.afkl.exercises.spring.fares;

import lombok.Value;

@Value
public class FareDescriptor {
	Fare fare;
	String descriptionOrigion, descriptionDestination;

}