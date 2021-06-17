package person.clp.stationeryback.service;

import person.clp.stationeryback.entity.Stationery;

import java.util.List;

public interface StationeryService {
    List<Stationery> getAllStationery();

    void addStationery(String type,Integer count);

    void reduceStationery(String type,Integer count);
}
