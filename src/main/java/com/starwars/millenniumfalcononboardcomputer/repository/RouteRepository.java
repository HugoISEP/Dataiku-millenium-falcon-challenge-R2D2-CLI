package com.starwars.millenniumfalcononboardcomputer.repository;

import com.starwars.millenniumfalcononboardcomputer.model.entity.Route;
import com.starwars.millenniumfalcononboardcomputer.model.entity.RouteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, RouteId> {}
