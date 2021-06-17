package person.clp.stationeryback.service;

import person.clp.stationeryback.entity.Record;

import java.util.List;

public interface RecordService {

    public void addRecord(Record record);

    List<Record> getAllBorrowRecordByUsername(String username,Integer way);

    List<Record> getAllBackRecordByUsername(String username,Integer way);
}
