package person.clp.stationeryback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import person.clp.stationeryback.entity.Record;
import person.clp.stationeryback.entity.Stationery;
import person.clp.stationeryback.entity.User;
import person.clp.stationeryback.mapper.RecordMapper;
import person.clp.stationeryback.mapper.StationeryMapper;
import person.clp.stationeryback.mapper.UserMapper;

import java.util.List;

@SpringBootTest
class StationeryBackApplicationTests {

    @Autowired
    private StationeryMapper stationeryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void getAllStationeryNumber(){
        List<Stationery> listall = stationeryMapper.getStationery();
        System.out.println(listall);
    }

    @Test
    public void getUserByUsername(){
        User user1 = userMapper.selectByName("root");
        System.out.println(user1);
    }

    @Test
    public void updateNumber(){
        List<Stationery> stationery = stationeryMapper.getStationery();
        System.out.println(stationery.size());
        Stationery stationery1 = stationery.get(0);
        System.out.println(stationery1.getEraser()+2);
        stationeryMapper.updateStationery("eraser", stationery1.getEraser()+2);
    }

    @Test
    public void insert(){

    }

    @Test
    public void listAllRecord(){
        User root = userMapper.selectByName("root");
        List<Record> root1 = recordMapper.getRecordByUserId(24, 1);
        System.out.println(root1);
    }
}
