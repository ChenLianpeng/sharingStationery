package person.clp.stationeryback.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.clp.stationeryback.entity.Record;
import person.clp.stationeryback.entity.User;
import person.clp.stationeryback.mapper.RecordMapper;
import person.clp.stationeryback.mapper.UserMapper;
import person.clp.stationeryback.service.RecordService;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    RecordMapper recordMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public void addRecord(Record record) {
        recordMapper.insert(record);
    }

    @Override
    public List<Record> getAllBorrowRecordByUsername(String username, Integer way) {
        User user = userMapper.selectByName(username);
        List<Record> recordByUserId = recordMapper.getRecordByUserId(user.getId(), 1);
        return recordByUserId;
    }

    @Override
    public List<Record> getAllBackRecordByUsername(String username, Integer way) {
        User user = userMapper.selectByName(username);
        List<Record> recordByUserId = recordMapper.getRecordByUserId(user.getId(), 0);
        return recordByUserId;
    }
}
